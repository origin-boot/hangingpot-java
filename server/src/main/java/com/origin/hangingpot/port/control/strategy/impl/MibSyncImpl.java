package com.origin.hangingpot.port.control.strategy.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.ErrorItem;
import com.origin.hangingpot.domain.JobLog;
import com.origin.hangingpot.domain.constants.TableConstants;
import com.origin.hangingpot.infrastructure.db.DataSourceFactory;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.repository.JobLogRepository;
import com.origin.hangingpot.infrastructure.util.DBUtils;
import com.origin.hangingpot.port.control.strategy.SyncStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author: 徐杰
 * @Date: 2024/6/5 18:05
 * @Description: 医保结算清单实现逻辑
 **/
@Service("Sync1")
@RequiredArgsConstructor
@Slf4j
public class MibSyncImpl implements SyncStrategy {
    final DatabaseConnectionRepository dcRepository;
    final JobLogRepository jobLogRepository;
    private  ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Long MAX_COUNT = 1000L;
    /**
     * 根据源头端、目标端ID以及时间范围进行同步
     *
     * @param sourceId
     * @param destId
     * @param startTime
     * @param endTime
     */
    private String[] otherTables = new String[]{"mib_data_bldinfo", "mib_data_diseinfo", "mib_data_icuinfo", "mib_data_iteminfo", "mib_data_oprninfo", "mib_data_opspdiseinfo", "mib_data_payinfo", "mib_data_setlinfo"};

    @Override
    public void SyncData(Long sourceId, Long destId, String startTime, String endTime) {
        //错误记录列表
        List<ErrorItem> errorItems = new ArrayList<>();
        //日志记录
        JobLog jobLog = new JobLog();
        Double costTime = 0.0;
        jobLog.setStartTime(DateUtil.parse(startTime).toInstant());
        jobLog.setFinishTime(DateUtil.parse(endTime).toInstant());
        jobLog.setScheduleMode("定时任务");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Optional<DatabaseConnection> source = dcRepository.findById(sourceId);
        Optional<DatabaseConnection> dest = dcRepository.findById(destId);
        //获取信息
        Long totalCount = 0L;
        if (source.isPresent() && dest.isPresent()) {
            DatabaseConnection sourceDc = source.get();
            DatabaseConnection destDc = dest.get();
            DruidDataSource sourceDruid = DataSourceFactory.getDruidDataSource(String.valueOf(sourceDc.getId()), sourceDc.getBaseDbInfo());
            DruidDataSource destDruid = DataSourceFactory.getDruidDataSource(String.valueOf(destDc.getId()), destDc.getBaseDbInfo());

            //先查询总条数
            try (DruidPooledConnection connection = sourceDruid.getConnection()) {
                String countSql = DBUtils.getCountSql(TableConstants.MAIN_TABLE, TableConstants.CONDITION_COL, startTime, endTime);
                PreparedStatement preparedStatement = connection.prepareStatement(countSql);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    log.info("总共：" + resultSet.getLong(1) + "条数据");
                    totalCount = resultSet.getLong(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(totalCount == 0){
                log.info("无数据！");
                return;
            }

            int batchCount;
            //向下取整
            if (totalCount % MAX_COUNT == 0) {
                batchCount = (int) (totalCount / MAX_COUNT);
            } else {
                batchCount = (int) (totalCount / MAX_COUNT) + 1;
            }
            Long nowCount = 0L;
            while(batchCount > 0){
                //获取查询sql
                String selectSql = DBUtils.getSelectSql(TableConstants.MAIN_TABLE, TableConstants.CONDITION_COL, startTime, endTime, nowCount,MAX_COUNT);
                //获取insert sql
                String insertSql = null;
                try (DruidPooledConnection connection = sourceDruid.getConnection()) {

                    insertSql = DBUtils.assembleSQL(selectSql, connection, TableConstants.MAIN_TABLE, TableConstants.TabkeKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ObjectUtils.isEmpty(insertSql)) {
                    log.info("错误！"+insertSql);
                    return;
                }
                //目标端执行sql
                Boolean isTrue = true;
                try (DruidPooledConnection connection = destDruid.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                    boolean execute = preparedStatement.execute();


                } catch (Exception e) {
                    e.printStackTrace();
                    isTrue = false;
                }
                if(!isTrue){
                    try(DruidPooledConnection connection1 = destDruid.getConnection()){
                        List<ErrorItem> errorItems1 = insertOneByOne(insertSql, connection1);
                        errorItems.addAll(errorItems1);
                    }catch (Exception e){
                        e.printStackTrace();
                        log.error("同步失败："  + "失败了，错误信息：" + e.getMessage());
                    }

                }
                batchCount --;
                nowCount ++;
            }
            //同步其余表
            //查出来所有id
            String selectOnlyIdSql = DBUtils.getSelectOnlyIdSql(TableConstants.MAIN_TABLE, TableConstants.CONDITION_COL, startTime, endTime);
            //执行selectOnlyIdSql 获取id数组
            StringBuilder ids = new StringBuilder("(");
            Long count = 0L;

            try (DruidPooledConnection connection = sourceDruid.getConnection();DruidPooledConnection connection1 = destDruid.getConnection()) {
                //源头获取ids
                List<Long> sourceIdList = new ArrayList<>();
                List<Long> destIdList = new ArrayList<>();
                PreparedStatement preparedStatement = connection.prepareStatement(selectOnlyIdSql);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    count++;
                    long id = resultSet.getLong(TableConstants.TabkeKey);
                    sourceIdList.add(id);
                    ids.append(id).append(",");
                }
                ids.deleteCharAt(ids.length() - 1).append(")");


                //目标端获取ids
                PreparedStatement preparedStatement1 = connection1.prepareStatement(selectOnlyIdSql);
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                while (resultSet1.next()) {
                    long id = resultSet1.getLong(TableConstants.TabkeKey);
                    destIdList.add(id);
                }
                //差集
                destIdList.removeAll(sourceIdList);
                if(destIdList.size() > 0){
                    log.info("目标端差集数据：" + destIdList.size());
                    jobLog.setDiffItems(com.alibaba.fastjson2.JSON.toJSONString(destIdList));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            stopWatch.stop();

            log.info("共计：" + count + "条数据"+ "=====》耗时：" + stopWatch.getTotalTimeSeconds() + "秒");
            String selectByIdSql = "select * from %s where OriginalID in " + ids;
            jobLog.setTotalCount(Math.toIntExact(count));
            costTime += stopWatch.getTotalTimeSeconds();
            class TableTask implements Callable<Map>{
                private String tableName;

                public TableTask(String table) {
                    this.tableName = table;
                }

                @Override
                public Map call() throws Exception {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    Map<String,Object> map = new HashMap<>();

                    try (DruidPooledConnection connection = sourceDruid.getConnection()) {

                        String insertSql1 = DBUtils.assembleSQL(String.format(selectByIdSql, tableName), connection, tableName, TableConstants.TabkeKey);

                        Boolean isTrue = true;
                        if (insertSql1 == null)
                            return null;
                        try (DruidPooledConnection connection1 = destDruid.getConnection()) {
                            PreparedStatement preparedStatement = connection1.prepareStatement(insertSql1);
                            boolean execute = preparedStatement.execute();
                        } catch (Exception e) {
                            //失败了  一条一条执行
                            isTrue = false;
                            log.error("同步 "+tableName + "失败了，开始一条一条执行，错误信息：" + e.getMessage());
                            e.printStackTrace();
                        }
                        if(!isTrue){
                            try(DruidPooledConnection connection1 = destDruid.getConnection()){
                                List<ErrorItem> errorItems1 = insertOneByOne(insertSql1, connection1, tableName);
                                map.put("errorItems",errorItems1);
                            }catch (Exception e){
                                e.printStackTrace();
                                log.error("同步失败：" + tableName + "失败了，错误信息：" + e.getMessage());
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        stopWatch.stop();
                        map.put("costTime",stopWatch.getTotalTimeSeconds());
                        return map;
                    }

                }

                private List<ErrorItem> insertOneByOne(String insertSql,DruidPooledConnection connection,String tableName) {
                    List<ErrorItem> errorItems = new ArrayList<>();
                    //分割成单条sql
                    String[] strings = DBUtils.splitInsertSql(insertSql);
                    Arrays.stream(strings).forEach(item ->{
                        try {
                            PreparedStatement preparedStatement = connection.prepareStatement(item);
                            preparedStatement.execute();
                        } catch (SQLException e) { //失败
                            log.error("执行失败：" + item);
                            errorItems.add(new ErrorItem(item,e.getMessage()));
                        }
                    });
                    return errorItems;
                }
            }


            ArrayList<Future<Map>> resultList = new ArrayList<>();
            Arrays.stream(otherTables).forEach(table -> {
                //并发执行
                Future<Map> submitted = executorService.submit(new TableTask(table));
                resultList.add(submitted);

            });

            for (int i = 0; i < resultList.size(); i++) {
                try {
                    Map map = resultList.get(i).get();
                    if(map != null){
                        List<ErrorItem> errorItems1 = (List<ErrorItem>) map.get("errorItems");
                        if(errorItems1 != null){
                            errorItems.addAll(errorItems1);
                        }
                        Double costTime1 = (Double) map.get("costTime");
                        costTime += costTime1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(errorItems.size() > 0) {
                jobLog.setErrorItems(com.alibaba.fastjson2.JSON.toJSONString(errorItems));
                jobLog.setErrorCount(errorItems.size());
                jobLog.setStatus("部分失败");
            }else{
                jobLog.setStatus("执行成功");
            }
            jobLog.setCreateTime(Instant.now());

            jobLog.setTotalTime(costTime);
            jobLogRepository.save(jobLog);


        }


    }
    private List<ErrorItem> insertOneByOne(String insertSql,DruidPooledConnection connection) {
        //分割成单条sql
        List<ErrorItem> errorItems = new ArrayList<>();
        String[] strings = DBUtils.splitInsertSql(insertSql);
        Arrays.stream(strings).forEach(item ->{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(item);
                preparedStatement.execute();
            } catch (SQLException e) { //失败
                log.error("执行失败：" + item);
                errorItems.add(new ErrorItem(item,e.getMessage()));
            }
        });
        return errorItems;



    }
}

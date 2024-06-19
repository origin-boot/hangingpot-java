package com.origin.hangingpot.port.control.strategy.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson2.util.DateUtils;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.ErrorItem;
import com.origin.hangingpot.domain.JobLog;
import com.origin.hangingpot.domain.ProjectMap;
import com.origin.hangingpot.domain.constants.TableConstants;
import com.origin.hangingpot.infrastructure.db.DataSourceFactory;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.repository.JobLogRepository;
import com.origin.hangingpot.infrastructure.repository.ProjectMapRepository;
import com.origin.hangingpot.infrastructure.repository.ScheduleJobRepository;
import com.origin.hangingpot.infrastructure.util.DBUtils;
import com.origin.hangingpot.port.control.strategy.SyncStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
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
    final ProjectMapRepository projectMapRepository;
    final ScheduleJobRepository scheduleJobRepository;
    private  ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Long MAX_COUNT = 10000L;
    /**
     * 根据源头端、目标端ID以及时间范围进行同步
     *
     * @param sourceId
     * @param destId
     * @param startTime
     * @param endTime
     */
    private String[] otherTables = new String[]{"mib_data_bldinfo", "mib_data_diseinfo", "mib_data_icuinfo", "mib_data_iteminfo", "mib_data_oprninfo", "mib_data_opspdiseinfo", "mib_data_payinfo", "mib_data_setlinfo"};

    /**
     * 同步数据方法，用于将一个数据源的数据同步到另一个数据源。
     * 参数包括源数据源ID、目标数据源ID、开始时间、结束时间、运行类型和可变参数args，其中args第一个元素为项目ID。
     * 同步过程包括查询源数据源的数据总量，分批从源数据源读取数据并写入目标数据源，以及同步关联的其他表数据。
     * 在同步过程中记录日志和错误信息，同步完成后保存同步日志。
     *
     * @param sourceId 源数据源ID
     * @param destId 目标数据源ID
     * @param startTime 同步的开始时间
     * @param endTime 同步的结束时间
     * @param runType 运行类型，表示同步的调度模式
     * @param args 可变参数，第一个元素为项目ID，用于查询项目相关的数据映射关系
     */
    @Override
    public void SyncData(Long sourceId, Long destId, String startTime, String endTime,String runType,Long...args) {
        // 初始化错误记录列表
        //错误记录列表
        List<ErrorItem> errorItems = new ArrayList<>();

        // 检查参数是否正确
        //映射
        if (args.length == 0) {
            log.error("参数错误！");
            return;
        }
        Long projectId = args[0];

        // 根据项目ID查询数据映射关系
        //获取项目映射
        List<ProjectMap> byProjectId = projectMapRepository.findByProject_Id(projectId);

        // 将数据映射关系转换为Map方便后续查询
        //转成map
        Map<String, Map<String,String>> map = new HashMap<>();
        byProjectId.forEach(item -> {
            String fieldName = item.getFieldName();
            String sourceVal = item.getSourceVal();
            String targetVal = item.getTargetVal();
            if(map.containsKey(fieldName)){
                map.get(fieldName).put(sourceVal,targetVal);
            }else{
                Map<String,String> temp = new HashMap<>();
                temp.put(sourceVal,targetVal);
                map.put(fieldName,temp);
            }
        });

        // 初始化任务日志
        //日志记录
        JobLog jobLog = new JobLog();
        Double costTime = 0.0;
        jobLog.setStartTime(DateTime.of(startTime, "yyyy-MM-dd HH:mm:ss"));
        jobLog.setFinishTime(DateTime.of(endTime, "yyyy-MM-dd HH:mm:ss"));
        jobLog.setScheduleMode(runType);

        // 开始计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 根据ID查询数据源连接
        Optional<DatabaseConnection> source = dcRepository.findById(sourceId);
        Optional<DatabaseConnection> dest = dcRepository.findById(destId);

        // 根据项目ID查询调度任务，并设置到任务日志中
        //获取对应的job
        scheduleJobRepository.findByProject_Id(projectId).ifPresent(job -> {
            jobLog.setJob(job);
            log.info("========开始同步：{}========" ,job.getJobName());
        });

        // 检查数据源是否存在
        Long totalCount = 0L;
        if (source.isPresent() && dest.isPresent()) {
            DatabaseConnection sourceDc = source.get();
            DatabaseConnection destDc = dest.get();
            DruidDataSource sourceDruid = DataSourceFactory.getDruidDataSource(String.valueOf(sourceDc.getId()), sourceDc.getBaseDbInfo());
            DruidDataSource destDruid = DataSourceFactory.getDruidDataSource(String.valueOf(destDc.getId()), destDc.getBaseDbInfo());

            // 查询数据总量
            //先查询总条数
            try (DruidPooledConnection connection = sourceDruid.getConnection()) {
                String countSql = DBUtils.getCountSql(TableConstants.MAIN_TABLE, TableConstants.CONDITION_COL, startTime, endTime);
                PreparedStatement preparedStatement = connection.prepareStatement(countSql);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    log.info("========总共：" + resultSet.getLong(1) + "条数据========");
                    totalCount = resultSet.getLong(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 如果数据总量为0，则结束同步
            if(totalCount == 0){
                log.info("========无数据！========");
                return;
            }

            // 计算需要分批处理的次数
            int batchCount;
            //向下取整
            if (totalCount % MAX_COUNT == 0) {
                batchCount = (int) (totalCount / MAX_COUNT);
            } else {
                batchCount = (int) (totalCount / MAX_COUNT) + 1;
            }
            Long nowCount = 0L;
            while(batchCount > 0){
                // 构造分批查询的SQL
                //获取查询sql
                String selectSql = DBUtils.getSelectSql(TableConstants.MAIN_TABLE, TableConstants.CONDITION_COL, startTime, endTime, nowCount,MAX_COUNT);

                // 构造对应的插入SQL
                //获取insert sql
                String insertSql = null;
                try (DruidPooledConnection connection = sourceDruid.getConnection()) {
                    insertSql = DBUtils.assembleSQL(selectSql, connection, TableConstants.MAIN_TABLE, TableConstants.TabkeKey,map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 尝试批量插入到目标数据源
                // 目标端执行sql
                Boolean isTrue = true;
                try (DruidPooledConnection connection = destDruid.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                    boolean execute = preparedStatement.execute();


                } catch (Exception e) {
                    e.printStackTrace();
                    isTrue = false;
                }

                // 如果批量插入失败，则尝试逐条插入
                if(!isTrue){
                    try(DruidPooledConnection connection1 = destDruid.getConnection()){
                        List<ErrorItem> errorItems1 = insertOneByOne(insertSql, connection1);
                        errorItems.addAll(errorItems1);
                    }catch (Exception e){
                        e.printStackTrace();
                        log.error("========同步失败："  + "失败了，错误信息：" + e.getMessage()+"========");
                    }

                }

                // 更新批次计数和总数计数
                batchCount --;
                nowCount ++;
            }

            // 同步其他关联表数据
            // 同步其余表
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
                    log.info("========目标端差集数据：{}========" ,destIdList.size());
                    jobLog.setDiffItems(com.alibaba.fastjson2.JSON.toJSONString(destIdList));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            stopWatch.stop();

            log.info("========共计：" + count + "条数据"+ "-耗时：" + stopWatch.getTotalTimeSeconds() + "秒========");
            String selectByIdSql = "select * from %s where OriginalID in " + ids;
            jobLog.setTotalCount(Math.toIntExact(count));
            costTime += stopWatch.getTotalTimeSeconds();
            class TableTask implements Callable<Map>{
                private String tableName;
                private Map<String,Map<String,String>> filedMap;

                public TableTask(String table,Map<String,Map<String,String>> map) {
                    this.filedMap = map;
                    this.tableName = table;
                }

                @Override
                public Map call() throws Exception {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    Map<String,Object> map = new HashMap<>();

                    try (DruidPooledConnection connection = sourceDruid.getConnection()) {

                        String insertSql1 = DBUtils.assembleSQL(String.format(selectByIdSql, tableName), connection, tableName, TableConstants.TabkeKey,filedMap);

                        Boolean isTrue = true;
                        if (insertSql1 == null)
                            return null;
                        try (DruidPooledConnection connection1 = destDruid.getConnection()) {
                            PreparedStatement preparedStatement = connection1.prepareStatement(insertSql1);
                            boolean execute = preparedStatement.execute();
                        } catch (Exception e) {
                            //失败了  一条一条执行
                            isTrue = false;
                            log.error("========同步 "+tableName + "失败了，开始一条一条执行，错误信息：" + e.getMessage()+"========");
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
                Future<Map> submitted = executorService.submit(new TableTask(table,map));
                resultList.add(submitted);

            });

            for (int i = 0; i < resultList.size(); i++) {
                try {
                    Map map1 = resultList.get(i).get();
                    if(map != null){
                        List<ErrorItem> errorItems1 = (List<ErrorItem>) map1.get("errorItems");
                        if(errorItems1 != null){
                            errorItems.addAll(errorItems1);
                        }
                        Double costTime1 = (Double) map1.get("costTime");
                        costTime += costTime1;
                        log.info("========同步 "+otherTables[i] + "成功，耗时：" + costTime1 + "秒========");
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
                jobLog.setErrorCount(0);
                jobLog.setStatus("执行成功");
            }
            DateTime now = DateTime.now();
            jobLog.setCreateTime(now);
            jobLog.setTotalTime(NumberUtil.round(costTime,4).doubleValue());
            jobLogRepository.save(jobLog);
            log.info("========同步日志开始========");
            log.info("========同步日志：" + com.alibaba.fastjson2.JSON.toJSONString(jobLog) + "========");
            log.info("========同步日志结束========");
            log.info("========同步完成========");


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

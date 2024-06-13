package com.origin.hangingpot.port.control.strategy.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.constants.TableConstants;
import com.origin.hangingpot.infrastructure.db.DataSourceFactory;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.util.DBUtils;
import com.origin.hangingpot.port.control.strategy.SyncStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Long MAX_COUNT = 100000L;
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

//            try {
//                sourceDruid.init();
//                destDruid.init();
//            } catch (SQLException e) {
//
//                e.printStackTrace();
//            }
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
                String selectSql = DBUtils.getSelectSql(TableConstants.MAIN_TABLE, TableConstants.CONDITION_COL, startTime, endTime, nowCount);
                //获取insert sql
                String insertSql = null;
                try (DruidPooledConnection connection = sourceDruid.getConnection()) {

                    insertSql = DBUtils.assembleSQL(selectSql, connection, TableConstants.MAIN_TABLE, TableConstants.TabkeKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ObjectUtils.isEmpty(insertSql)) {
                    log.info("错误！");
                    return;
                }
                //目标端执行sql
                try (DruidPooledConnection connection = destDruid.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                    boolean execute = preparedStatement.execute();
                } catch (Exception e) {
                    e.printStackTrace();
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
            try (DruidPooledConnection connection = sourceDruid.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(selectOnlyIdSql);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    count++;
                    ids.append(resultSet.getLong(TableConstants.TabkeKey)).append(",");
                }
                ids.deleteCharAt(ids.length() - 1).append(")");

            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("共计：" + count + "条数据ID");
            String selectByIdSql = "select * from %s where OriginalID in " + ids;


            Arrays.stream(otherTables).forEach(table -> {
                //并发执行
                executorService.execute(() -> {

                    try (DruidPooledConnection connection = sourceDruid.getConnection()) {

                        String insertSql1 = DBUtils.assembleSQL(String.format(selectByIdSql, table), connection, table, TableConstants.TabkeKey);
                        if (insertSql1 == null)
                            return;
                        try (DruidPooledConnection connection1 = destDruid.getConnection()) {
                            PreparedStatement preparedStatement = connection1.prepareStatement(insertSql1);
                            boolean execute = preparedStatement.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });


            });
            executorService.shutdown();

            while (true) {
                if (executorService.isTerminated()) {
                    stopWatch.stop();
                    log.info("同步完成，耗时：" + stopWatch.getTotalTimeSeconds());
                    break;
                }
            }


        }


    }
}

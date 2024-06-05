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

import java.sql.PreparedStatement;
import java.util.Optional;

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
    /**
     * 根据源头端、目标端ID以及时间范围进行同步
     *
     * @param sourceId
     * @param destId
     * @param startTime
     * @param endTime
     */
    @Override
    public void SyncData(Long sourceId, Long destId, String startTime, String endTime) {
        Optional<DatabaseConnection> source = dcRepository.findById(sourceId);
        Optional<DatabaseConnection> dest = dcRepository.findById(destId);
        //获取信息
        if(source.isPresent() && dest.isPresent()){
            DatabaseConnection sourceDc = source.get();
            DatabaseConnection destDc = dest.get();
            DruidDataSource sourceDruid = DataSourceFactory.getDruidDataSource(String.valueOf(sourceDc.getId()), sourceDc.getBaseDbInfo());
            DruidDataSource destDruid = DataSourceFactory.getDruidDataSource(String.valueOf(destDc.getId()), destDc.getBaseDbInfo());
            //获取查询sql
            String selectSql = DBUtils.getSelectSql(TableConstants.MAIN_TABLE, TableConstants.CONDITION_COL, startTime, endTime);
            //获取insert sql
            String insertSql = null;
            try(DruidPooledConnection connection = sourceDruid.getConnection()){
                insertSql = DBUtils.assembleSQL(selectSql,connection,TableConstants.MAIN_TABLE,TableConstants.TabkeKey);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(ObjectUtils.isEmpty(insertSql)){
                log.info("错误！");
                return;
            }
            //目标端执行sql
            try(DruidPooledConnection connection = destDruid.getConnection()){
                log.info(insertSql);
                PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                boolean execute = preparedStatement.execute();

            }catch (Exception e){
                e.printStackTrace();
            }


        }



    }
}

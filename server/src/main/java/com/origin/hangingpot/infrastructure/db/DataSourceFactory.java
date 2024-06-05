package com.origin.hangingpot.infrastructure.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.origin.hangingpot.domain.BaseDBInfo;
import com.origin.hangingpot.domain.constants.DbSyncConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: YourName
 * @Date: 2024/6/3 11:32
 * @Description:
 **/

public class DataSourceFactory {
    private static volatile Map<String, DruidDataSource> dataSourceMap;

    static{
        dataSourceMap = new HashMap<>();
    }
    public static DruidDataSource getDruidDataSource(String dbType, BaseDBInfo baseDBInfo){
        //根据id查询是否在map中
        if (!dataSourceMap.containsKey(dbType)){
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(baseDBInfo.getUrl());
            dataSource.setDriverClassName(baseDBInfo.getDriver());
            dataSource.setUsername(baseDBInfo.getUsername());
            dataSource.setPassword(baseDBInfo.getPassword());
            dataSource.setInitialSize(10);  //初始连接数，默认0
            dataSource.setMaxActive(30);  //最大连接数，默认8
            dataSource.setMinIdle(10);  //最小闲置数
            dataSource.setMaxWait(6000);  //获取连接的最大等待时间，单位毫秒
            dataSource.setTimeBetweenEvictionRunsMillis(3600000);
            dataSource.setMinEvictableIdleTimeMillis(3600000);
            if(DbSyncConstants.TYPE_DB_MYSQL.equals(baseDBInfo.getDbtype())){  //MySQL
                dataSource.setValidationQuery("SELECT 1");
            }else if(DbSyncConstants.TYPE_DB_ORACLE.equals(baseDBInfo.getDbtype())){ //Oracle
                dataSource.setValidationQuery("SELECT 1 FROM DUAL");
            }else if(DbSyncConstants.TYPE_DB_SQLSERVER.equals(baseDBInfo.getDbtype())){
                //TODO 待实现
            }
            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestOnReturn(false);
            dataSource.setPoolPreparedStatements(true); //缓存PreparedStatement，默认false
            dataSource.setMaxOpenPreparedStatements(20); //缓存PreparedStatement的最大数量，默认-1（不缓存）。大于0时会自动开启缓存PreparedStatement，所以可以省略上一句代码
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
            dataSourceMap.put(dbType, dataSource);
        }
        return dataSourceMap.get(dbType);
    }
}

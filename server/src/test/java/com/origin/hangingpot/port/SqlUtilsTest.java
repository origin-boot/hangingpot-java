package com.origin.hangingpot.port;

import cn.hutool.core.date.DateTime;
import com.alibaba.druid.pool.DruidDataSource;
import com.origin.hangingpot.domain.BaseDBInfo;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.TableInfo;
import com.origin.hangingpot.domain.constants.TableConstants;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.db.DataSourceFactory;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.util.DBUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

/**
 * @Author: YourName
 * @Date: 2024/6/4 10:47
 * @Description:
 **/
@SpringBootTest
public class SqlUtilsTest {
    @Resource
    private DatabaseConnectionRepository databaseConnectionRepository;
    @Test
    public void testUtils(){
        Optional<DatabaseConnection> byId = databaseConnectionRepository.findById(1L);
       if(byId.isPresent()){
           DatabaseConnection databaseConnection = byId.get();
           TableInfo metaInfo = DBUtils.getMetaInfo(databaseConnection,"te");
           System.out.println(metaInfo);
       }else{
           System.out.println("null");
       }
    }

    @Test
    public void testAssignSql() throws SQLException {
        Optional<DatabaseConnection> byId = databaseConnectionRepository.findById(1L);
        if(byId.isPresent()){
            DatabaseConnection databaseConnection = byId.get();

            BaseDBInfo baseDBInfo = new BaseDBInfo();
            baseDBInfo.setUrl(databaseConnection.getUrl());
            baseDBInfo.setUsername(databaseConnection.getUsername());
            baseDBInfo.setPassword(databaseConnection.getPassword());

            DruidDataSource mysql = DataSourceFactory.getDruidDataSource("MySQL", baseDBInfo);
            String[] strings = {"id", "phone", "className", "classId"};

            String testSql = DBUtils.getSelectSql(TableConstants.MAIN_TABLE,"jssj","2024-01-04 00:00:00","2024-01-08 00:00:00");
            String assembleSQL = DBUtils.assembleSQL(String.format(testSql,TableConstants.MAIN_TABLE), mysql.getConnection(),  TableConstants.MAIN_TABLE, "OriginalID");
            System.out.println(assembleSQL);
        }else{
            System.out.println("null");
        }

    }
}

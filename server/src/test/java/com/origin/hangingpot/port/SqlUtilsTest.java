package com.origin.hangingpot.port;

import com.alibaba.druid.pool.DruidDataSource;
import com.origin.hangingpot.domain.BaseDBInfo;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.TableInfo;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.db.DataSourceFactory;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.util.DBUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
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

            String assembleSQL = DBUtils.assembleSQL("select * from orders", mysql.getConnection(),  "orders", strings, "id");
            System.out.println(assembleSQL);
        }else{
            System.out.println("null");
        }

    }
}

package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.TableInfo;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.util.DBUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
}

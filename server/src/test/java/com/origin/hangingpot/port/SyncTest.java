package com.origin.hangingpot.port;

import com.origin.hangingpot.port.control.strategy.context.SyncContext;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: YourName
 * @Date: 2024/6/5 18:34
 * @Description:
 **/
@SpringBootTest

public class SyncTest {

    @Resource
    private SyncContext syncContext;

    @Test
    void TestSync(){
        syncContext.SyncData(1L,2L,"1000-01-01 00:00:00","9999-06-08 00:00:00","Sync1",1L,"手动执行");
    }

    @Test
    void TestSync2(){
        String insertStatement =  "insert into mib_data_oprninfo (setl_list_oprn_id,OriginalID,setl_id,psn_no,mdtrt_id,main_oprn_flag,oprn_oprt_name,oprn_oprt_code,oprn_oprt_date,anst_way,oper_dr_name,oper_dr_code,anst_dr_name,anst_dr_code,oprn_oprt_begntime,oprn_oprt_endtime,anst_begntime,anst_endtime,oprn_oprt_type) values ('1','1013521','520100G0000674613891','52000001000000000000142577','52000001000000000000142577','1','中医按摩手法治疗','93.3907','2024-04-17',null,'姜怡','D520102021714',null,null,'2024-04-17T14:00','2024-04-26T12:00',null,null,'1'),('2','1013521','520100G0000674613891','52000001000000000000142577','52000001000000000000142577','0','穴位贴敷治疗','17.95150','2024-04-17',null,'姜怡','D520102021714',null,null,'2024-04-17T14:00','2024-04-26T12:00',null,null,'2'),('3','1013521','520100G0000674613891','52000001000000000000142577','52000001000000000000142577','0','中药熏药治疗','17.95420','2024-04-17',null,'姜怡','D520102021714',null,null,'2024-04-17T14:00','2024-04-26T12:00',null,null,'2'),('4','1013521','520100G0000674613891','52000001000000000000142577','52000001000000000000142577','0','中药局部熏洗治疗','17.95510','2024-04-17',null,'姜怡','D520102021714',null,null,'2024-04-17T14:00','2024-04-26T12:00',null,null,'2'),('1','1022021','529900G0000626099101','52000099000000001011474511','52000099000000001011474511','1','无创呼吸机辅助通气(双水平气道正压[BiPAP])','93.9000x002','2024-02-19',null,'杜昕璐','D520102013618',null,null,'2024-02-19T10:18','2024-02-19T11:30',null,null,'1'),('2','1022021','529900G0000626099101','52000099000000001011474511','52000099000000001011474511','0','穴位贴敷','93.3910','2024-02-19',null,'杜昕璐','D520102013618',null,null,'2024-02-19T10:18','2024-02-28T12:00',null,null,'2'),('3','1022021','529900G0000626099101','52000099000000001011474511','52000099000000001011474511','0','中药局部熏洗治疗','17.95510','2024-02-19',null,'杜昕璐','D520102013618',null,null,'2024-02-19T10:18','2024-02-28T12:00',null,null,'2'),('4','1022021','529900G0000626099101','52000099000000001011474511','52000099000000001011474511','0','皮内针治疗','17.91230','2024-02-19',null,'杜昕璐','D520102013618',null,null,'2024-02-19T10:18','2024-02-28T12:00',null,null,'2') on duplicate key update setl_list_oprn_id= values(setl_list_oprn_id),setl_id= values(setl_id),psn_no= values(psn_no),mdtrt_id= values(mdtrt_id),main_oprn_flag= values(main_oprn_flag),oprn_oprt_name= values(oprn_oprt_name),oprn_oprt_code= values(oprn_oprt_code),oprn_oprt_date= values(oprn_oprt_date),anst_way= values(anst_way),oper_dr_name= values(oper_dr_name),oper_dr_code= values(oper_dr_code),anst_dr_name= values(anst_dr_name),anst_dr_code= values(anst_dr_code),oprn_oprt_begntime= values(oprn_oprt_begntime),oprn_oprt_endtime= values(oprn_oprt_endtime),anst_begntime= values(anst_begntime),anst_endtime= values(anst_endtime),oprn_oprt_type= values(oprn_oprt_type)"; // 将您的SQL语句放在此处

        //分割on duplicate key update
        String[] insertStatementArr = insertStatement.split(" on duplicate key update ");
        insertStatement = insertStatementArr[0];
        String onDuplicateKeyUpdate = insertStatementArr[1];
        //values分割前后
        insertStatementArr = insertStatement.split(" values ");
        String head = insertStatementArr[0];
        String allValues = insertStatementArr[1];
        // 按逗号分割INSERT语句中的每组值
        insertStatement = allValues;
        String[] values = insertStatement.split("\\),\\(");
        String sql = """
                %s values %s  on duplicate key update %s
                """;
        for (String value : values) {
            // 如果不是第一组值，添加左括号
            if (!value.startsWith("(")) {
                value = "(" + value;
            }
            // 如果不是最后一组值，添加右括号
            if (!value.endsWith(")")) {
                value = value + ")";
            }
            System.out.println(String.format(sql, head, value, onDuplicateKeyUpdate));

        }
    }
}

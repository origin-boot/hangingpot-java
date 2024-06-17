package com.origin.hangingpot.infrastructure.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.origin.hangingpot.domain.BaseDBInfo;
import com.origin.hangingpot.domain.ColumnInfo;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.TableInfo;
import com.origin.hangingpot.infrastructure.db.DataSourceFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: xujie
 * @Date: 2024/6/3 12:06
 * @Description: 用于数据库获取主表数据，拼接插入数据的sql工具
 **/

public class DBUtils {

    private static final String SelectOne = "select * from %s limit 1";

    /**
     * 获取插入数据的sql
     *
     * @param tableName
     * @param columns
     * @return
     */
    public static String getInsertSql(String tableName, String[] columns) {
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(tableName).append(" (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(",");
            }
        }
        sql.append(") values (");
        for (int i = 0; i < columns.length; i++) {
            sql.append("%s"); //占位符
            if (i < columns.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        return sql.toString();
    }

    /**
     * 获取查询数据的sql
     *
     * @param tableName
     * @param columns
     * @return
     */
    public static String getSelectSql(String tableName, String[] columns) {
        StringBuilder sql = new StringBuilder("select ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(",");
            }
        }
        sql.append(" from ").append(tableName);
        return sql.toString();
    }

    /**
     * 获取更新数据的sql
     *
     * @param tableName
     * @param columns
     * @return
     */
    public static String getUpdateSql(String tableName, String[] columns) {
        StringBuilder sql = new StringBuilder("update ");
        sql.append(tableName).append(" set ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                sql.append(",");
            }
        }
        return sql.toString();
    }


    /**
     * 获取条件查询数据的sql
     *
     * @param tableName
     * @param columns
     * @param conditions
     * @return
     */
    public static String getSelectSql(String tableName, String[] columns, String[] conditions) {
        StringBuilder sql = new StringBuilder("select ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(",");
            }
        }
        sql.append(" from ").append(tableName).append(" where ");
        for (int i = 0; i < conditions.length; i++) {
            sql.append(conditions[i]).append(" = ?");
            if (i < conditions.length - 1) {
                sql.append(" and ");
            }
        }
        return sql.toString();
    }

    /**
     * 根据 sync_time 开始和结束来生成对应查询sql
     */
    public static String getSelectSql(String tableName, String conditionCol,String startTime, String endTime,Long nowCount,Long maxCount) {
        StringBuilder sql = new StringBuilder("select *");
        sql.append(" from ").append(tableName).append(" where ");

        sql.append(conditionCol+" >= '%s' and "+conditionCol+" < '%s' limit ").append(nowCount*maxCount).append(","+maxCount);
        return String.format(sql.toString(),startTime,endTime);
    }
    /**
     * 根据 sync_time 开始和结束来生成查询总数量sql
     */
    public static String getCountSql(String tableName, String conditionCol,String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("select count(*)");
        sql.append(" from ").append(tableName).append(" where ");

        sql.append(conditionCol+" >= '%s' and "+conditionCol+" < '%s'");
        return String.format(sql.toString(),startTime,endTime);
    }
    /**
     * 根据 sync_time 开始和结束来生成对应查询sql，只要OriginalID
     */
    public static String getSelectOnlyIdSql(String tableName, String conditionCol,String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("select OriginalID");
        sql.append(" from ").append(tableName).append(" where ");

        sql.append(conditionCol+" >= '%s' and "+conditionCol+" < '%s'");
        return String.format(sql.toString(),startTime,endTime);
    }
    /**
     * 批量插入代码
     */
    public static String assembleSQL(String srcSql, Connection conn,  String destTable,  String destTableKey) throws SQLException {
        String uniqueName = "123";

        //默认的srcFields数组与destFields相同
        String[] srcFields = new String[0];
        String[] destFields = new String[0];
        PreparedStatement pst = conn.prepareStatement(srcSql);
        ResultSet rs = pst.executeQuery();
        rs.setFetchSize(1000);
        //获取表列数
        int columnCount = rs.getMetaData().getColumnCount();
        srcFields = new String[columnCount];
        destFields = new String[columnCount];
        String[] updateFields = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            srcFields[i - 1] = rs.getMetaData().getColumnName(i);
            destFields[i - 1] = rs.getMetaData().getColumnName(i);
        }

        updateFields = Arrays.stream(srcFields).filter(s -> !s.equals(destTableKey)).toArray(String[]::new);

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(destTable).append(" (").append(String.join(",",destFields)).append(") values ");
        long count = 0;


        while (rs.next()) {
            sql.append("(");
            for (int index = 0; index < destFields.length; index++) {
                Object fieldValue = rs.getObject(destFields[index].trim()); //Todo 待实现字段映射
                if (fieldValue == null) {
                    sql.append(fieldValue).append(index == (destFields.length - 1) ? "" : ",");
                } else {
                    sql.append("'").append(fieldValue).append(index == (destFields.length - 1) ? "'" : "',");
                }
            }
            sql.append("),");
            count++;
        }
        if (rs != null) {
            rs.close();
        }
        if (pst != null) {
            pst.close();
        }
        if(count > 0){
            //去掉末尾逗号
            sql = sql.deleteCharAt(sql.length() - 1);
            //加入主键冲突处理  -》更新
            sql.append(" on duplicate key update ");
            for (int index = 0; index < updateFields.length; index++) {
                sql.append(updateFields[index]).append("= values(").append(updateFields[index]).append(index == (updateFields.length - 1) ? ")" : "),");
            }


        }
        if(count == 0){
            return null;
        }
        return sql.toString();
    }

    /**
     * 获取数据库的表列表
     */
    public static TableInfo getMetaInfo(DatabaseConnection databaseConnection,String tableName) {
        BaseDBInfo baseDBInfo = new BaseDBInfo();
        baseDBInfo.setUrl(databaseConnection.getUrl());
        baseDBInfo.setUsername(databaseConnection.getUsername());
        baseDBInfo.setPassword(databaseConnection.getPassword());

        DruidDataSource mysql = DataSourceFactory.getDruidDataSource("MySQL", baseDBInfo);
        TableInfo tableInfo = new TableInfo();
        try (Connection root = mysql.getConnection();) {
            PreparedStatement preparedStatement = root.prepareStatement(String.format(SelectOne, tableName));
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.setFetchSize(1000);
            int columnCount = resultSet.getMetaData().getColumnCount();

            List<ColumnInfo> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setFiledName(resultSet.getMetaData().getColumnName(i));
                columnInfo.setFiledType(resultSet.getMetaData().getColumnTypeName(i));
                columnInfo.setRemarks(resultSet.getMetaData().getColumnLabel(i));
                columns.add(columnInfo);
            }
            tableInfo.setColumns(columns);
            tableInfo.setTableName(resultSet.getMetaData().getCatalogName(1));
            tableInfo.setRemarks(resultSet.getMetaData().getCatalogName(1));
            tableInfo.setType(resultSet.getMetaData().getCatalogName(1));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return tableInfo;

    }


    /**
     * 分割insert 为单独的
     */

    public static String[] splitInsertSql(String insertStatement) {
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
        List<String> sqlList = new ArrayList<>();
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
            sqlList.add(String.format(sql, head, value, onDuplicateKeyUpdate));
        }
        return sqlList.toArray(new String[0]);
    }


}

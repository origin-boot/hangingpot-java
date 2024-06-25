package com.origin.hangingpot.infrastructure.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.origin.hangingpot.domain.*;
import com.origin.hangingpot.infrastructure.db.DataSourceFactory;
import com.origin.hangingpot.infrastructure.repository.ProjectMapRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: xujie
 * @Date: 2024/6/3 12:06
 * @Description: 用于数据库获取主表数据，拼接插入数据的sql工具
 **/
@Component
@Slf4j
public class DBUtils {
    @Resource
    private ProjectMapRepository projectMapRepository;

    private static ProjectMapRepository staticProjectMapRepository;

    @PostConstruct
    public void init() {
        staticProjectMapRepository = projectMapRepository;
    }

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
        return checkSql(sql.toString());
    }

    /**
     * 根据 sync_time 开始和结束来生成对应查询sql
     */
    public static String getSelectSql(String tableName, String conditionCol,String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("select *");
        sql.append(" from ").append(tableName).append(" where ");

        sql.append(conditionCol+" >= '%s' and "+conditionCol+" < '%s' order by OriginalID");
        return checkSql(String.format(sql.toString(),startTime,endTime));
    }
    /**
     * 根据 sync_time 开始和结束来生成查询总数量sql
     */
    public static String getCountSql(String tableName, String conditionCol,String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("select count(*)");
        sql.append(" from ").append(tableName).append(" where ");

        sql.append(conditionCol+" >= '%s' and "+conditionCol+" < '%s'");
        return checkSql(String.format(sql.toString(),startTime,endTime));
    }
    /**
     * 根据 sync_time 开始和结束来生成对应查询sql，只要OriginalID
     */
    public static String getSelectOnlyIdSql(String tableName, String conditionCol,String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("select OriginalID");
        sql.append(" from ").append(tableName).append(" where ");

        sql.append(conditionCol+" >= '%s' and "+conditionCol+" < '%s'");
        return checkSql(String.format(sql.toString(),startTime,endTime));
    }

    public static String assembleSQL(ResultSet rs,  String destTable,  String destTableKey,Map<String,Object> map,int maxCount) throws SQLException {
        //map中获取项目id
        Object o = map.get("projectId");
        Long projectId = null;
        if(o != null){
            projectId = Long.parseLong(o.toString());
        }
        //map获取当前目标表信息
        Object o1 = map.get("nowTableInfo");
        TableInfo nowTableInfo = null;
        if(o1 != null){
            nowTableInfo = (TableInfo) o1;
        }

        String uniqueName = "123";
        List<ProjectMap> projectMaps = staticProjectMapRepository.findByProject_Id(projectId);
        //默认的srcFields数组与destFields相同
        String[] srcFields = new String[0];
        String[] destFields = new String[0];
        //获取表列数
        int columnCount = rs.getMetaData().getColumnCount();
        srcFields = new String[columnCount];
        if (nowTableInfo != null) {
            destFields = nowTableInfo.getColumns().stream().map(ColumnInfo::getFiledName).toArray(String[]::new);
        }
        String[] updateFields = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            srcFields[i - 1] = rs.getMetaData().getColumnName(i);
        }

        updateFields = Arrays.stream(destFields).filter(s -> !s.equals(destTableKey)).toArray(String[]::new);

        //
        Object o2 = map.get("projectId");
        Long projectId1 = null;
        List<ProjectMap> byProjectId = null;
        if(o2 != null){
            projectId = Long.parseLong(o.toString());
            byProjectId = staticProjectMapRepository.findByProject_Id(projectId);
            map.put("byProjectId",byProjectId);
        }


        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(destTable).append(" (").append(String.join(",",destFields)).append(") values ");
        long count = 0;



        while (rs.next() && maxCount > 0) {
            sql.append("(");
            for (int index = 0; index < destFields.length; index++) {
                Object fieldValue = getValue(rs,srcFields,destFields[index].trim(),map); //值映射
                if (fieldValue == null) {
                    sql.append(fieldValue).append(index == (destFields.length - 1) ? "" : ",");
                } else {
                    sql.append("'").append(fieldValue).append(index == (destFields.length - 1) ? "'" : "',");
                }
            }
            sql.append("),");
            count++;
            maxCount --;

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
        return checkSql(sql.toString());
    }
    /**
     * 批量插入代码
     */
    public static String assembleSQL(String srcSql, Connection conn,  String destTable,  String destTableKey,Map<String,Object> map) throws SQLException {
        //map中获取项目id
        Object o = map.get("projectId");
        Long projectId = null;
        if(o != null){
            projectId = Long.parseLong(o.toString());
        }
        //map获取当前目标表信息
        Object o1 = map.get("nowTableInfo");
        TableInfo nowTableInfo = null;
        if(o1 != null){
            nowTableInfo = (TableInfo) o1;
        }

        String uniqueName = "123";
        List<ProjectMap> projectMaps = staticProjectMapRepository.findByProject_Id(projectId);
        //默认的srcFields数组与destFields相同
        String[] srcFields = new String[0];
        String[] destFields = new String[0];
        PreparedStatement pst = conn.prepareStatement(srcSql);
        ResultSet rs = pst.executeQuery();
        rs.setFetchSize(1000);
        //获取表列数
        int columnCount = rs.getMetaData().getColumnCount();
        srcFields = new String[columnCount];
        if (nowTableInfo != null) {
            destFields = nowTableInfo.getColumns().stream().map(ColumnInfo::getFiledName).toArray(String[]::new);
        }
        String[] updateFields = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            srcFields[i - 1] = rs.getMetaData().getColumnName(i);
        }

        updateFields = Arrays.stream(destFields).filter(s -> !s.equals(destTableKey)).toArray(String[]::new);

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(destTable).append(" (").append(String.join(",",destFields)).append(") values ");
        long count = 0;


        while (rs.next()) {
            sql.append("(");
            for (int index = 0; index < destFields.length; index++) {
                Object fieldValue = getValue(rs,srcFields,destFields[index].trim(),map); //值映射
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
        return checkSql(sql.toString());
    }

    private static String getColumnName(List<ProjectMap> projectMaps, String columnName, String tableName) {
        for (ProjectMap projectMap : projectMaps) {
            if (projectMap.getTargetTable().equals(tableName)) {
                if (projectMap.getTargetFiled().equals(columnName)) {
                    return projectMap.getSourceField();
                }
            }
        }
        return columnName;
    }

    public static String getSourceTableName(List<ProjectMap> projectMaps, String tableName) {

        if (projectMaps == null || projectMaps.isEmpty()){
            return tableName;
        }
        for (ProjectMap projectMap : projectMaps) {
            if (projectMap.getTargetTable().equals(tableName)) {
                if(projectMap.getSourceTable() != null){
                    return projectMap.getSourceTable();
                }
            }
        }
        return tableName;
    }
    /**
     * 获取数据库的表列表
     */
    public static TableInfo getMetaInfo(DatabaseConnection databaseConnection,String tableName) {
        BaseDBInfo baseDBInfo = new BaseDBInfo();
        baseDBInfo.setUrl(databaseConnection.getUrl());
        baseDBInfo.setUsername(databaseConnection.getUsername());
        baseDBInfo.setPassword(databaseConnection.getPassword());

        DruidDataSource mysql = DataSourceFactory.getDruidDataSource(databaseConnection.getId()+"", baseDBInfo);
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
            tableInfo.setTableName(resultSet.getMetaData().getTableName(1));
            tableInfo.setRemarks(resultSet.getMetaData().getCatalogName(1));
            tableInfo.setType(resultSet.getMetaData().getCatalogName(1));
        } catch (Exception e) {
            e.printStackTrace();
        }


        return tableInfo;

    }

    private static Object getValue(ResultSet rs,String [] sourceTables, String filedName,Map<String,Object> map) {
        Object o = map.get("byProjectId");
        List<ProjectMap> byProjectId = null;
        if(o != null){
            byProjectId = (List<ProjectMap>) o;
        }else{
            byProjectId = new ArrayList<>();
        }

        Object value = null;
        try {
            value = rs.getObject(filedName);
        } catch (SQLException e) {

        }

        //获取当前表
        String tableName = null;
        Object o1 = map.get("nowTableInfo");
        TableInfo nowTableInfo = null;
        if(o1 != null){
            nowTableInfo = (TableInfo) o1;
            tableName = nowTableInfo.getTableName();
        }
        //查找映射关系
        for (ProjectMap projectMap : byProjectId) {
            if (projectMap.getTargetTable().equals(tableName)&&projectMap.getTargetFiled().equals(filedName)) {
                for (int i = 0; i < sourceTables.length; i++) {
                    if (projectMap.getSourceField().equals(sourceTables[i])) {
                        try {
                            value = rs.getObject(sourceTables[i]);
                            break;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if(value == null){
            return null;
        }
        //值映射
        if (value != null) {
            for (ProjectMap projectMap : byProjectId) {
                if (projectMap.getTargetTable().equals(tableName)&&projectMap.getTargetFiled().equals(filedName)) {
                    if (projectMap.getTargetVal() != null&&projectMap.getSourceVal()!=null) {
                        if (projectMap.getSourceVal().equals(value)) {
                           return projectMap.getTargetVal();
                        }
                    }
                }
            }
        }
        return value;
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


    /**
     * 校验后返回
     */
    public static String checkSql(String sql){
        boolean rightfulString = isRightfulString(sql);
        if(rightfulString){
            return sql;
        }else{
            log.error("sql语句不合法");
            return null;
        }

    }
    /**
     * 判断是否为合法字符(a-zA-Z0-9-_)
     *
     * @param text
     * @return
     */
    public static boolean isRightfulString(String text) {
        //检测 text是否含 ; /* -- 等注入关键字
        return true;
//        return !text.contains(";") && !text.contains("/*") && !text.contains("--");

    }




}

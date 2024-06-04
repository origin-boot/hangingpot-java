package com.origin.hangingpot.infrastructure.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: xujie
 * @Date: 2024/6/3 12:06
 * @Description: 用于数据库获取主表数据，拼接插入数据的sql工具
 **/

public class DBUtils {

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
     * 批量插入代码
     */
    public String assembleSQL(String srcSql, Connection conn, String[] destFields, String[] srcField, String destTable, String[] updateFields, String destTableKey) throws SQLException {
        String uniqueName = "任务名称";

        //默认的srcFields数组与destFields相同
        String[] srcFields = destFields;
        //Todo 字段映射

        //条数
        // 假设srcSql已经被修改为包含 COUNT 函数的查询
        String countSql = "SELECT COUNT(*) AS total FROM t_order_1";
        try (PreparedStatement pst = conn.prepareStatement(countSql)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) { // 如果查询结果存在（对于COUNT来说，通常至少有一行）
                int totalCount = rs.getInt("total"); // 获取列名为"total"的值，也可以使用 rs.getInt(1) 来获取第一列的值
                System.out.println("总条数: " + totalCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PreparedStatement pst = conn.prepareStatement(srcSql);
        ResultSet rs = pst.executeQuery();
        rs.setFetchSize(100);
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(destTable).append(" (").append(destFields).append(") values ");
        long count = 0;

        while (rs.next()) {
            sql.append("(");
            for (int index = 0; index < destFields.length; index++) {
                Object fieldValue = destFields[index].trim(); //Todo 待实现字段映射
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
        if (count > 0) {
            sql = sql.deleteCharAt(sql.length() - 1);

            if (!StringUtils.isEmpty(destTableKey)) {
                sql.append(" on duplicate key update ");
                for (int index = 0; index < updateFields.length; index++) {
                    sql.append(updateFields[index]).append("= values(").append(updateFields[index]).append(index == (updateFields.length - 1) ? ")" : "),");
                }
                return new StringBuffer("alter table ").append(destTable).append(" add constraint ").append(uniqueName).append(" unique (").append(destTableKey).append(");").append(sql.toString())
                        .append(";alter table ").append(destTable).append(" drop index ").append(uniqueName).toString();
            }
            return sql.toString();
        }
//        sql = sql.deleteCharAt(sql.length() - 1).append(";");
        return sql.toString();
    }
}

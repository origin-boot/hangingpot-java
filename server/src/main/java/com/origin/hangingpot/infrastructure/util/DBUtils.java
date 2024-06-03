package com.origin.hangingpot.infrastructure.util;

/**
 * @Author: xujie
 * @Date: 2024/6/3 12:06
 * @Description: 用于数据库获取主表数据，拼接插入数据的sql工具
 **/

public class DBUtils {

    /**
     * 获取插入数据的sql
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
}

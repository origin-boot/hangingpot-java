package com.origin.hangingpot.domain.constants;

import lombok.Data;

/**
 * @Author: YourName
 * @Date: 2024/6/3 11:36
 * @Description:
 **/

public class DbSyncConstants {
    /**
     * 源数据库
     */
    public static final String TYPE_SOURCE = "source";

    /**
     * 目标数据库
     */
    public static final String TYPE_DEST = "dest";
    /**
     * sqlserver数据库
     */
    public static final String TYPE_DB_SQLSERVER = "sqlserver";

    /**
     * MySQL数据库
     */
    public static final String TYPE_DB_MYSQL = "mysql";
    /**
     * Oracle数据库
     */
    public static final String TYPE_DB_ORACLE = "oracle";
}

package com.origin.hangingpot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: YourName
 * @Date: 2024/6/3 11:35
 * @Description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDBInfo implements Serializable {
    private static final long serialVersionUID = -5546462343272223569L;

    //数据库连接
    private String url;
    //数据库用户名
    private String username;
    //数据库密码
    private String password;
    //数据库驱动
    private String driver;
    //数据库类型(对应mysql还是sqlserver,还是oracle)
    private String dbtype;

}

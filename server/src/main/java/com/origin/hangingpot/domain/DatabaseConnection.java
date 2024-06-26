package com.origin.hangingpot.domain;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "database_connection")
public class DatabaseConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 200)
    @NotNull
    @NotEmpty
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Size(max = 200)
    @NotNull
    @NotEmpty
    @Column(name = "type", nullable = false, length = 200)
    private String type;

    @Size(max = 255)
    @NotNull
    @NotEmpty
    @Column(name = "version", nullable = false)
    private String version;

    @Size(max = 200)
    @NotNull
    @NotEmpty
    @Column(name = "driver", nullable = false, length = 200)
    private String driver;

    @Size(max = 200)
    @NotNull
    @NotEmpty
    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Size(max = 20)
    @NotNull
    @NotEmpty
    @Column(name = "port", nullable = false, length = 20)
    private String port;

    @Size(max = 200)
    @NotNull
    @NotEmpty
    @Column(name = "database_name", nullable = false, length = 200)
    private String databaseName;

    @Size(max = 20)
    @NotNull
    @NotEmpty
    @Column(name = "character_encoding", nullable = false, length = 20)
    private String characterEncoding;

    @Lob
    @NotEmpty
    @Column(name = "url")
    private String url;

    @Size(max = 200)
    @NotNull
    @NotEmpty
    @Column(name = "username", nullable = false, length = 200)
    private String username;

    @Size(max = 200)
    @NotNull
    @NotEmpty
    @Column(name = "password", nullable = false, length = 200)
    private String password;


    @Column(name = "create_time")
    private Date createTime;


    @Column(name = "update_time")
    private Date updateTime;


    @Column(name = "project_id")
    private Long projectId;

    @Lob
    @NotNull
    @NotEmpty
    @Column(name = "source_type",nullable = false)
    private String sourceType;

    private String projectName;

    /**
     * 获取BaseDBInfo
     *
     */
    @JsonIgnore
    public BaseDBInfo getBaseDbInfo(){
        BaseDBInfo baseDBInfo = new BaseDBInfo();
        baseDBInfo.setUsername(username);
        baseDBInfo.setPassword(password);
        baseDBInfo.setUrl(url);
        baseDBInfo.setDriver(driver);
        return baseDBInfo;
    }
}
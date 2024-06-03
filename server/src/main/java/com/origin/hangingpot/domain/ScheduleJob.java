package com.origin.hangingpot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "xuanhu_schedule_job")
public class ScheduleJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "job_name")
    private String jobName;

    @Size(max = 255)
    @Column(name = "cron_expression")
    private String cronExpression;

    @Size(max = 255)
    @Column(name = "db_source")
    private String dbSource;

    @Size(max = 255)
    @Column(name = "db_dest")
    private String dbDest;

    @NotNull
    @Column(name = "status", nullable = false)
    private Byte status;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @NotNull
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    @NotNull
    @Column(name = "del_flag", nullable = false)
    private Byte delFlag;

    @NotNull
    @Column(name = "db_source_info", nullable = false)
    private Long dbSourceInfo;

    @NotNull
    @Column(name = "db_dest_info", nullable = false)
    private Long dbDestInfo;

}
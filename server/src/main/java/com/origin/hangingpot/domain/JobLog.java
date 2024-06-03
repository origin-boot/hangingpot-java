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
@Table(name = "xuanhu_job_log")
public class JobLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Size(max = 200)
    @NotNull
    @Column(name = "job_key", nullable = false, length = 200)
    private String jobKey;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "finish_time", nullable = false)
    private Instant finishTime;

    @NotNull
    @Column(name = "schedule_mode", nullable = false)
    private Short scheduleMode;

    @NotNull
    @Column(name = "status", nullable = false)
    private Short status;

    @Lob
    @Column(name = "error_log")
    private String errorLog;

    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @NotNull
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

}
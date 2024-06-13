package com.origin.hangingpot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "schedule_job")
public class ScheduleJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "job_name", nullable = false, length = 50)
    private String jobName;

    @Size(max = 20)
    @NotNull
    @Column(name = "cron_expression", nullable = false, length = 20)
    private String cronExpression;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Size(max = 500)
    @Column(name = "job_desc", length = 500)
    private String jobDesc;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
    @Column(name = "map_id", nullable = false)
    private Long mapId;


    @NotNull
    @Column(name = "`range`", nullable = false)
    private Long range;

}
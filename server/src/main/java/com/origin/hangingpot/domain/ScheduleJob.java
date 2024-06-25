package com.origin.hangingpot.domain;

import cn.hutool.core.date.DateTime;
import com.origin.hangingpot.infrastructure.validGroup.Insert;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.Date;

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
    @NotNull(groups = {Insert.class})
    @Column(name = "job_name", nullable = false, length = 50)
    private String jobName;

    @Size(max = 20)
    @NotNull(groups = {Insert.class})
    @Column(name = "cron_expression", nullable = false, length = 20)
    private String cronExpression;

    @NotNull(groups = {Insert.class})
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Size(max = 500)
    @Column(name = "job_desc", length = 500)
    private String jobDesc;

    @NotNull(groups = {Insert.class})
    @Column(name = "status", nullable = false)
    private Boolean status = false;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "create_time", nullable = false)
    private Date createTime;


    @Column(name = "update_time", nullable = false)
    private Date updateTime;


    @NotNull(groups = {Insert.class})
    @Column(name = "`range`", nullable = false)
    private Long range;

    @Column(name = "start_time")
    private Date startTime;

}
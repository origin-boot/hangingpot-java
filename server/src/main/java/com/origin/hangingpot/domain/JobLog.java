package com.origin.hangingpot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "job_log")
public class JobLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @NotNull
    @Column(name = "finish_time", nullable = false)
    private Date finishTime;

    @Size(max = 50)
    @Column(name = "schedule_mode", length = 50)
    private String scheduleMode;

    @Column(name = "total_time", nullable = false)
    private Double totalTime;

    @Size(max = 50)
    @Column(name = "status", nullable = false, length = 50)
    private String status;


    @Column(name = "error_items")
    private String errorItems;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "error_count")
    private Integer errorCount;



    @Column(name = "diff_items")
    private String diffItems;

}
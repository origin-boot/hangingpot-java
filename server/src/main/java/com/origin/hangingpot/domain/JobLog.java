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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private ScheduleJob job;

    @Override
    public String toString() {
        return "JobLog {" +
                "\n    startTime: " + startTime + ", \"任务开始时间\"" +
                "\n    finishTime: " + finishTime + ", \"任务结束时间\"" +
                "\n    scheduleMode: '" + scheduleMode + "', \"任务调度模式\"" +
                "\n    totalTime: " + totalTime + ", \"任务总耗时\"" +
                "\n    status: '" + status + "', \"任务状态\"" +
                "\n    errorItems: '" + errorItems + "', \"错误项\"" +
                "\n    createTime: " + createTime + ", \"日志创建时间\"" +
                "\n    totalCount: " + totalCount + ", \"总计数\"" +
                "\n    errorCount: " + errorCount + ", \"错误计数\"" +
                "\n    diffItems: '" + diffItems + "', \"不同项\"" +
                "\n    job: " + job + " \"相关任务\"" +
                "\n}";
    }
}
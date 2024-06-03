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
@Table(name = "xuanhu_job_config")
public class JobConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @NotNull
    @Column(name = "source_connection_id", nullable = false)
    private Long sourceConnectionId;

    @Size(max = 1024)
    @NotNull
    @Column(name = "source_schema", nullable = false, length = 1024)
    private String sourceSchema;

    @Size(max = 32)
    @Column(name = "table_type", length = 32)
    private String tableType;

    @Lob
    @Column(name = "source_tables")
    private String sourceTables;

    @NotNull
    @Column(name = "excluded", nullable = false)
    private Boolean excluded = false;

    @NotNull
    @Column(name = "target_connection_id", nullable = false)
    private Long targetConnectionId;

    @Size(max = 200)
    @NotNull
    @Column(name = "target_schema", nullable = false, length = 200)
    private String targetSchema;

    @Size(max = 32)
    @NotNull
    @Column(name = "table_name_case", nullable = false, length = 32)
    private String tableNameCase;

    @Size(max = 32)
    @NotNull
    @Column(name = "column_name_case", nullable = false, length = 32)
    private String columnNameCase;

    @Lob
    @Column(name = "table_name_map")
    private String tableNameMap;

    @Lob
    @Column(name = "column_name_map")
    private String columnNameMap;

    @Size(max = 255)
    @NotNull
    @Column(name = "target_base_table", nullable = false)
    private String targetBaseTable;

    @NotNull
    @Column(name = "target_auto_increment", nullable = false)
    private Boolean targetAutoIncrement = false;

    @Size(max = 32)
    @NotNull
    @Column(name = "table_sort", nullable = false, length = 32)
    private String tableSort;

    @Size(max = 4096)
    @Column(name = "before_sql_scripts", length = 4096)
    private String beforeSqlScripts;

    @Size(max = 4096)
    @Column(name = "after_sql_scripts", length = 4096)
    private String afterSqlScripts;

    @NotNull
    @Column(name = "batch_size", nullable = false)
    private Long batchSize;

    @NotNull
    @Column(name = "first_flag", nullable = false)
    private Boolean firstFlag = false;

    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

}
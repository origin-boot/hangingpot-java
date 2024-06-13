package com.origin.hangingpot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Proxy;

import java.time.Instant;

@Getter
@Setter
@Entity
@Proxy(lazy=false)
@Table(name = "project")
public class Project {
    @Id
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Size(max = 255)
    @Column(name = "project_desc")
    private String projectDesc;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;

}
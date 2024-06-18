package com.origin.hangingpot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "project_map")
public class ProjectMap {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Size(max = 255)
    @Column(name = "field_name")
    private String fieldName;

    @Size(max = 255)
    @Column(name = "source_val")
    private String sourceVal;

    @Size(max = 255)
    @Column(name = "target_val")
    private String targetVal;

}
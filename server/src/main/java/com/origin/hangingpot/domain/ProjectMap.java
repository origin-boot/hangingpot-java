package com.origin.hangingpot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "project_map")
public class ProjectMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "project_id")
    private Project project;

    @Size(max = 255)
    @NotNull
    @Column(name = "field_name")
    private String fieldName;

    @Size(max = 255)
    @Column(name = "source_val")
    @NotNull
    private String sourceVal;

    @Size(max = 255)
    @Column(name = "target_val")
    @NotNull
    private String targetVal;

}
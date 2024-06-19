package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.ProjectMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectMapRepository extends JpaRepository<ProjectMap, Long> {
    List<ProjectMap> findByProject_Id(Long id);

    @Query("select p from ProjectMap p where p.project.projectName like ?1")
    Page<ProjectMap> findByProjectName(String projectName, Pageable pageable);
}

package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("select p from Project p where p.projectName like ?1")
    Page<Project> findByProjectNameLike(String projectName, Pageable pageable);
}

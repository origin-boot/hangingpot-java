package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.ProjectMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMapRepository extends JpaRepository<ProjectMap, Long> {
    List<ProjectMap> findByProject_Id(Long id);
}

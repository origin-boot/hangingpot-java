package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}

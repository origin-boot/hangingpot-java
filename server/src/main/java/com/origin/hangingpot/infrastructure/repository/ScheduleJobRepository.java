package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.ScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleJobRepository extends JpaRepository<ScheduleJob, Long>{
    List<ScheduleJob> findByStatus(Boolean status);

    Optional<ScheduleJob> findByProject_Id(Long id);
}

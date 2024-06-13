package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.ScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleJobRepository extends JpaRepository<ScheduleJob, Long>{
    List<ScheduleJob> findByStatus(Boolean status);

}

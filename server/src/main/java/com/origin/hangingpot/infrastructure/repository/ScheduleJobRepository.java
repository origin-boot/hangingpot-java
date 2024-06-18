package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.ScheduleJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScheduleJobRepository extends JpaRepository<ScheduleJob, Long>{
    List<ScheduleJob> findByStatus(Boolean status);

    Optional<ScheduleJob> findByProject_Id(Long id);

    @Query("select s from ScheduleJob s where s.jobName like ?1")
    Page<ScheduleJob> findByJobNameLike(String jobName, Pageable pageable);
}

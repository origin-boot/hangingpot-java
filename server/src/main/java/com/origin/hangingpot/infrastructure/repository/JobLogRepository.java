package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.JobLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobLogRepository extends JpaRepository<JobLog, Long>{
}

package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.JobLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface JobLogRepository extends JpaRepository<JobLog, Long>{

    @Query("select j from JobLog j where j.startTime >= ?1 and j.finishTime <= ?2 and j.scheduleMode like ?3")
    Page<JobLog> findByStartTimeGreaterThanEqualAndFinishTimeLessThanEqualAndScheduleModeLike(Date startTime, Date finishTime, String scheduleMode, Pageable pageable);
}

package com.origin.hangingpot.port;

import cn.hutool.core.date.DateUtil;
import com.origin.hangingpot.domain.JobLog;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.JobLogRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Author: YourName
 * @Date: 2024/6/18 09:24
 * @Description:
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class JobLogController {
    final JobLogRepository jobLogRepository;

    /**
     * 分页查询定时任务日志
     */
    @GetMapping
    public Ok<PageResource> list(@Valid PageCommand pageCommand) {
        if(pageCommand.getSearchText() != null && pageCommand.getStartTime()!= null){
            Page<JobLog> jobLogs = jobLogRepository.findByStartTimeGreaterThanEqualAndFinishTimeLessThanEqualAndScheduleModeLike(DateUtil.parse(pageCommand.getStartTime()), DateUtil.parse(pageCommand.getEndTime()), pageCommand.getSearchText(), PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
            return Ok.of(PageResource.of(jobLogs));
        }else if(pageCommand.getSearchText() != null){
            JobLog jobLog = new JobLog();
            jobLog.setScheduleMode(pageCommand.getSearchText().replaceAll("%",""));
            Example<JobLog> example = Example.of(jobLog);

            Page<JobLog> jobLogs = jobLogRepository.findAll(example,PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
            return Ok.of(PageResource.of(jobLogs));

        }
        else{
            Page<JobLog> jobLogs = jobLogRepository.findAll(PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
            return Ok.of(PageResource.of(jobLogs));
        }

    }
}

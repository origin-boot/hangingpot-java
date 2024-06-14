package com.origin.hangingpot.port;

import com.google.common.eventbus.AsyncEventBus;
import com.origin.hangingpot.domain.ScheduleJob;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.ScheduleJobRepository;
import com.origin.hangingpot.port.control.CronTaskRegistrar;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: YourName
 * @Date: 2024/6/14 14:22
 * @Description:
 **/
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class ScheduleJobController {
    final ScheduleJobRepository scheduleJobRepository;

    final AsyncEventBus asyncEventBus;
    final CronTaskRegistrar cronTaskRegistrar;



    /**
     * 分页获取定时任务列表
     */
    @GetMapping
    public Ok<PageResource> list(@Valid PageCommand pageCommand) {
        Page<ScheduleJob> jobs = scheduleJobRepository.findAll(PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
        return Ok.of(PageResource.of(jobs));
    }

    /**
     * POST /api/jobs 新增定时任务
     */
    @PostMapping
    public void add(@Valid @RequestBody ScheduleJob job) {
        //查询数据库是否有projectId相同的任务
        scheduleJobRepository.findByProject_Id(job.getProject().getId()).ifPresent(j -> {
            throw new IllegalArgumentException("项目已存在定时任务");
        });
        asyncEventBus.post(job);
        scheduleJobRepository.save(job);
    }
    /**
     * PATCH /api/jobs/{id} 修改定时任务
     */
    @PatchMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody ScheduleJob job) {
        //查询数据库是否有projectId相同的任务
        scheduleJobRepository.findByProject_Id(job.getProject().getId()).ifPresent(j -> {
            throw new IllegalArgumentException("项目已存在定时任务");
        });

        job.setId(id);
        scheduleJobRepository.save(job);
        asyncEventBus.post(job);
    }
    /**
     * DELETE /api/jobs/{id} 删除定时任务
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        //查询对应的定时任务
        ScheduleJob job = scheduleJobRepository.findById(id).orElseThrow();
        //取消定时任务
        cronTaskRegistrar.removeTask(job.getId()+job.getJobName());
        scheduleJobRepository.deleteById(id);

    }
    /**
     * POST /api/jobs/{id}/run 立即执行定时任务
     */
    @PostMapping("/{id}/run")
    public void run(@PathVariable Long id) {
        ScheduleJob job = scheduleJobRepository.findById(id).orElseThrow();
        // TODO 执行定时任务
    }


}

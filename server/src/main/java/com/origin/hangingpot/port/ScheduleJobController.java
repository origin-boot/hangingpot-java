package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.ScheduleJob;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.ScheduleJobRepository;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        scheduleJobRepository.save(job);
    }
    /**
     * PATCH /api/jobs/{id} 修改定时任务
     */
    @PatchMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody ScheduleJob job) {
        job.setId(id);
        scheduleJobRepository.save(job);
    }
    /**
     * DELETE /api/jobs/{id} 删除定时任务
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
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

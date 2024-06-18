package com.origin.hangingpot.port;

import cn.hutool.core.date.DateUtil;
import com.google.common.eventbus.AsyncEventBus;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.ScheduleJob;
import com.origin.hangingpot.domain.User;
import com.origin.hangingpot.domain.error.UnauthorizedError;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.repository.ScheduleJobRepository;
import com.origin.hangingpot.infrastructure.util.ObjectUtil;
import com.origin.hangingpot.port.control.CronTaskRegistrar;
import com.origin.hangingpot.port.control.JwtService;
import com.origin.hangingpot.port.control.strategy.context.SyncContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

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
    final DatabaseConnectionRepository dcRepository;
    final SyncContext syncContext;

    final AsyncEventBus asyncEventBus;
    final CronTaskRegistrar cronTaskRegistrar;
    final JwtService jwtService;



    /**
     * 分页获取定时任务列表
     */
    @GetMapping
    public Ok<PageResource> list(@Valid PageCommand pageCommand) {
        if(Objects.isNull(pageCommand.getSearchText())){
            Page<ScheduleJob> jobs = scheduleJobRepository.findAll(PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
            return Ok.of(PageResource.of(jobs));
        }else{
            Page<ScheduleJob> jobs = scheduleJobRepository.findByJobNameLike(pageCommand.getSearchText(), PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
            return Ok.of(PageResource.of(jobs));
        }

    }

    /**
     * 新增定时任务
     */
    @PostMapping
    public Ok add(@Valid @RequestBody ScheduleJob job, HttpServletRequest request) throws Exception {
        //查询数据库是否有projectId相同的任务
        scheduleJobRepository.findByProject_Id(job.getProject().getId()).ifPresent(j -> {
            throw new IllegalArgumentException("项目已存在定时任务");
        });
        String token = jwtService.extractToken(request);
        try {
            String userId = jwtService.extractId(token);
            User user = new User();
            user.setId(Long.parseLong(userId));
            job.setUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("token解析失败");
        }
        asyncEventBus.post(job);
        job.setCreateTime(DateUtil.date());
        job.setUpdateTime(DateUtil.date());
        scheduleJobRepository.save(job);
        return Ok.empty();
    }
    /**
     * 修改定时任务
     */
    @PatchMapping("/{id}")
    public Ok update(@PathVariable Long id, @Valid @RequestBody ScheduleJob job) {
        //查询数据库是否有projectId相同的任务
        scheduleJobRepository.findByProject_Id(job.getProject().getId()).ifPresent(j -> {
            if(!j.getProject().getId().equals(job.getProject().getId()))
                throw new IllegalArgumentException("项目已存在定时任务");
        });

        job.setId(id);
        ScheduleJob scheduleJob = scheduleJobRepository.findById(id).orElseThrow();
        cronTaskRegistrar.removeTask(scheduleJob.getId()+scheduleJob.getJobName());

        ObjectUtil.copyPropertiesIgnoreNull(job, scheduleJob);
        scheduleJobRepository.save(scheduleJob);
        asyncEventBus.post(scheduleJob);
        return Ok.empty();
    }
    /**
     * 删除定时任务
     */
    @DeleteMapping("/{id}")
    public Ok delete(@PathVariable Long id) {
        //查询对应的定时任务
        ScheduleJob job = scheduleJobRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("定时任务不存在"));
        //取消定时任务
        cronTaskRegistrar.removeTask(job.getId()+job.getJobName());
        scheduleJobRepository.deleteById(id);
        return Ok.empty();
    }
    /**
     * 即执行定时任务
     */
    @PostMapping("/{id}/run")
    public Ok run(@PathVariable Long id, @RequestBody Map<String,String> map) {
        String startTime = map.get("startTime");
        String endTime = map.get("endTime");
        if(Objects.isNull(startTime) || Objects.isNull(endTime)){
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        if(DateUtil.parse(startTime).after(DateUtil.parse(endTime))){
            throw new IllegalArgumentException("开始时间不能大于结束时间");
        }
        ScheduleJob job = scheduleJobRepository.findById(id).orElseThrow();
        //查询数据源
        DatabaseConnection source = dcRepository.findBySourceTypeAndProjectId("源头端", job.getProject().getId()).orElseThrow();
        DatabaseConnection target = dcRepository.findBySourceTypeAndProjectId("目标端", job.getProject().getId()).orElseThrow();
        //如果为null
        if(Objects.isNull(source) || Objects.isNull(target)){
            throw new IllegalArgumentException("数据源不存在");
        }
        //执行定时任务
        syncContext.SyncData(source.getId(), target.getId(), startTime, endTime, "Sync1", job.getProject().getId(),"手动执行");
        return Ok.empty();
    }


}

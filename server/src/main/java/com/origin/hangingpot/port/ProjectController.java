package com.origin.hangingpot.port;

import cn.hutool.core.date.DateUtil;
import com.origin.hangingpot.domain.Project;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.ProjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Author: YourName
 * @Date: 2024/6/4 00:00
 * @Description:
 **/

@RestController
@RequiredArgsConstructor
public class ProjectController {
    final ProjectRepository projectRepository;

    /**
     * 分页获取所有项目信息
     */
    @GetMapping("/api/project/list")
    public Ok<PageResource> list(@Valid PageCommand pageCommand) {

        if(pageCommand.getSearchText() != null)
        {
            Page<Project> byProjectNameLike = projectRepository.findByProjectNameLike(pageCommand.getSearchText(), PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
            return Ok.of(PageResource.of(byProjectNameLike));
        }else{
            Page<Project> all = projectRepository.findAll(PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
            return Ok.of(PageResource.of(all));
        }

    }
    /**
     * 新增项目
     */
    @PostMapping("/api/project/add")
    public Ok add(@Valid @RequestBody Project project) {
        project.setCreateTime(new Date());
        project.setUpdateTime(new Date());

        projectRepository.save(project);
        return Ok.empty();
    }
    /**
     * 删除项目
     *
     */
    @DeleteMapping("/api/project/{id}")
    public Ok delete(@PathVariable Long id) {
        projectRepository.deleteById(id);
        return Ok.empty();
    }
    /**
     * 更新项目信息
     */
    @PutMapping("/api/project/update")
    public Ok update(@Valid @RequestBody Project project) {
        project.setUpdateTime(DateUtil.date());
        projectRepository.save(project);
        return Ok.empty();
    }


}

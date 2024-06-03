package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.Project;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.ProjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

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
        Page<Project> projects = projectRepository.findAll(PageRequest.of(pageCommand.getPage(), pageCommand.getSize()));
        return Ok.of(PageResource.of(projects));
    }
    /**
     * 添加
     */
    @PostMapping("/api/project/add")
    public void add(@Valid @RequestBody Project project) {
        projectRepository.save(project);
    }
    /**
     * 删除
     *
     */
    @DeleteMapping("/api/project/{id}")
    public void delete(@PathVariable Long id) {
        projectRepository.deleteById(id);
    }
    /**
     * 更新
     */
    @PutMapping("/api/project/update")
    public void update(@Valid @RequestBody Project project) {
        projectRepository.save(project);
    }


}

package com.origin.hangingpot.port;

import cn.hutool.core.date.DateUtil;
import com.origin.hangingpot.domain.JobLog;
import com.origin.hangingpot.domain.ProjectMap;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.ProjectMapRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目字典模块控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobMap")
public class ProjectMapController {
    final ProjectMapRepository projectMapRepository;


    /**
     * 分页获取项目字典
     *
     * @param pageCommand 分页和查询命令，包含页码、页面大小和可选的搜索文本。
     * @return 包含项目映射信息的分页资源对象。
     */
    @GetMapping
    public Ok<PageResource> list(@Valid PageCommand pageCommand) {
        // 当存在搜索文本时，根据项目名称查询项目映射
        if (pageCommand.getSearchText() != null) {
            Page<ProjectMap> byProjectName = projectMapRepository.findByProjectName(
                pageCommand.getSearchText(),
                PageRequest.of(pageCommand.getPage(), pageCommand.getSize())
            );
            return Ok.of(PageResource.of(byProjectName));
        } else {
            // 当没有搜索文本时，查询所有项目映射
            Page<ProjectMap> all = projectMapRepository.findAll(
                PageRequest.of(pageCommand.getPage(), pageCommand.getSize())
            );
            return Ok.of(PageResource.of(all));
        }
    }

    /**
     * 新增项目字典
     *
     * 此方法接收一个ProjectMap对象作为请求体，经过验证后，将其保存到数据库中。
     * 使用@Valid注解确保传入的项目映射对象符合验证规则，避免非法数据的存储。
     *
     * @param projectMap 经过验证的项目映射对象，包含需要添加到数据库的项目映射信息。
     * @return 返回一个空的Ok对象，表示操作成功。
     */
    @PostMapping
    public Ok add(@RequestBody @Valid ProjectMap projectMap) {
        projectMapRepository.save(projectMap);
        return Ok.empty();
    }

    /**
     * 删除项目字典
     *
     * 此方法接收一个项目映射的ID，根据ID删除对应的项目映射。
     *
     * @param id 待删除项目映射的ID。
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        projectMapRepository.deleteById(id);
    }

    /**
     * 更新项目字典
     *
     * 此方法接收一个ProjectMap对象作为请求体，经过验证后，将其更新到数据库中。
     * 使用@Valid注解确保传入的项目映射对象符合验证规则，避免非法数据的存储。
     *
     * @param projectMap 经过验证的项目映射对象，包含需要更新到数据库的项目映射信息。
     * @return 返回一个空的Ok对象，表示操作成功。
     */
    @PatchMapping("/{id}")
    public Ok update(@PathVariable Long id, @RequestBody @Valid ProjectMap projectMap) {
        projectMap.setId(id);
        projectMapRepository.save(projectMap);
        return Ok.empty();
    }



}

package com.origin.hangingpot.port;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.origin.hangingpot.domain.Project;
import com.origin.hangingpot.domain.success.Ok;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectControllerTest {
    @Resource
    ProjectController projectController;

    @Test
    void list() {
//        PageCommand pageCommand = new PageCommand(null,0,1);
//
//        Ok<PageResource> list = projectController.list(pageCommand);
//        System.out.println(list.toString());
    }

    @Test
    void add() {
        Project project = new Project();
        long id = IdUtil.getSnowflakeNextId();
        project.setId(id);
        project.setProjectName("test");
        project.setProjectDesc("test");
        project.setCreateTime(DateUtil.date());
        project.setUpdateTime(DateUtil.date());
        projectController.add(project);


    }

    @Test
    void delete() {
        projectController.delete(2L);
    }

    @Test
    void update() {
        Project project = new Project();
        project.setId(1L);
        project.setProjectName("test");
        project.setProjectDesc("test");
        project.setCreateTime(DateUtil.date());
        project.setUpdateTime(DateUtil.date());
        projectController.update(project);
    }
}
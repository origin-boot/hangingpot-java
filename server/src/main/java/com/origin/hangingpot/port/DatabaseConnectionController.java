package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.BaseDBInfo;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.Project;
import com.origin.hangingpot.domain.TableInfo;
import com.origin.hangingpot.domain.constants.DbSyncConstants;
import com.origin.hangingpot.domain.error.Error;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.db.DataSourceFactory;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.repository.ProjectRepository;
import com.origin.hangingpot.infrastructure.util.DBUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: YourName
 * @Date: 2024/6/3 17:54
 * @Description:
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dbs")
public class DatabaseConnectionController {
    final DatabaseConnectionRepository databaseConnectionRepository;
    final ProjectRepository projectRepository;

    /**
     * 分页获取所有数据库源信息
     */
    @GetMapping
    Ok<PageResource> list(@Valid PageCommand pageCommand) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();

        Page<DatabaseConnection> databaseConnections = databaseConnectionRepository.findAll(PageRequest.of(pageCommand.getPage(),pageCommand.getSize()));
        databaseConnections.get().forEach(databaseConnection -> {
            if(databaseConnection.getProjectId()==null){
                return;
            }
            Project project = new Project();
            project.setId(databaseConnection.getProjectId());
            Example<Project> example = Example.of(project, matcher);
            Optional<Project> one = projectRepository.findOne(example);
            one.ifPresent(value -> databaseConnection.setProjectName(value.getProjectName()));
        });
        return Ok.of(PageResource.of(databaseConnections));
    }

    /**
     * 新增数据源信息
     */
    @PostMapping
    Ok add(@Valid @RequestBody DatabaseConnection databaseConnection) throws Error {
        extracted(databaseConnection);

        databaseConnectionRepository.save(databaseConnection);
        return Ok.empty();
    }

    private void extracted(DatabaseConnection databaseConnection) throws Error {
        //如果是新增的数据源，并且项目id不为空，需要判断该项目是否已经存在数据源
        if(databaseConnection.getProjectId()!=null&& databaseConnection.getSourceType()!=null){
            List<DatabaseConnection> databaseConnections = databaseConnectionRepository.findByProjectIdAndSourceType(databaseConnection.getProjectId(), databaseConnection.getSourceType());
            if(databaseConnections.size()>0){
                throw new Error(201,"有项目已经绑定该数据源");
            }
        }
        //测试链接
        BaseDBInfo baseDbInfo = databaseConnection.getBaseDbInfo();
        //默认Mysql类型
        try {
            DataSourceFactory.getDruidDataSource(DbSyncConstants.TYPE_DB_MYSQL,baseDbInfo);
        }catch (Exception e){
            throw new Error(202,"数据库测试连接失败");
        }
    }

    /**
     * 删除数据源信息
     *
     */
    @DeleteMapping("/{id}")
    Ok delete(@PathVariable Long id) {
        databaseConnectionRepository.deleteById(id);
        return Ok.empty();
    }
    /**
     * 更新数据源信息
     */
    @PatchMapping
    Ok update(@Valid @RequestBody DatabaseConnection databaseConnection) throws Error {
        extracted(databaseConnection);
        databaseConnectionRepository.save(databaseConnection);
        return Ok.empty();
    }

    /**
     * 根据数据源id以及表名获取表信息
     */
    @GetMapping("meta")
    Ok<TableInfo> meta(@RequestParam Long id, @RequestParam String tableName) {
        Optional<DatabaseConnection> databaseConnection = databaseConnectionRepository.findById(id);
        if(databaseConnection.isPresent()){
            TableInfo metaInfo = DBUtils.getMetaInfo(databaseConnection.get(), tableName);
            return Ok.of(metaInfo);
        }else{
            return null;
        }
        
    }


}

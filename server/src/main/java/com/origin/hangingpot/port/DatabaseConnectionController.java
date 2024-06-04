package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.TableInfo;
import com.origin.hangingpot.domain.error.Error;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.infrastructure.util.DBUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @Author: YourName
 * @Date: 2024/6/3 17:54
 * @Description:
 **/
@RestController
@RequiredArgsConstructor
public class DatabaseConnectionController {
    final DatabaseConnectionRepository databaseConnectionRepository;

    /**
     * 分页获取所有数据库源信息
     */
    @GetMapping("/api/db/list")
    Ok<PageResource> list(@Valid PageCommand pageCommand) {
        Page<DatabaseConnection> databaseConnections = databaseConnectionRepository.findAll(PageRequest.of(pageCommand.getPage(),pageCommand.getSize()));
        return Ok.of(PageResource.of(databaseConnections));
    }

    /**
     * 添加
     */
    @PostMapping("/api/db/add")
    void add(@Valid @RequestBody DatabaseConnection databaseConnection) {
        databaseConnectionRepository.save(databaseConnection);
    }
    /**
     * 删除
     *
     */
    @DeleteMapping("/api/db/delete")
    void delete(@RequestParam Long id) {
        databaseConnectionRepository.deleteById(id);
    }
    /**
     * 更新
     */
    @PutMapping("/api/db/update")
    void update(@Valid @RequestBody DatabaseConnection databaseConnection) {
        databaseConnectionRepository.save(databaseConnection);
    }

    /**
     * 根据数据源id以及表名获取表信息
     */
    @GetMapping("/api/db/meta")
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

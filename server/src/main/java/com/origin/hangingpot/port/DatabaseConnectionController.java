package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.success.Ok;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}

package com.origin.hangingpot.infrastructure.repository;

import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatabaseConnectionRepository extends JpaRepository<DatabaseConnection, Long> {
    /**
     * 分页获取
     */
    @Override
    Page<DatabaseConnection> findAll(Pageable pageable);

    @Override
    List<DatabaseConnection> findAll();

}

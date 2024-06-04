package com.origin.hangingpot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: YourName
 * @Date: 2024/6/4 13:05
 * @Description:
 **/
@Data
public class TableInfo {

    private List<ColumnInfo> columns;
    private String remarks;
    private String tableName;
    private String type;

}



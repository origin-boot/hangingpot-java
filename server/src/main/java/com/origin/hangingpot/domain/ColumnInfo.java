package com.origin.hangingpot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnInfo {
    private String filedName;
    private String filedType;
    private String remarks;

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "filedName='" + filedName + '\'' +
                ", filedType='" + filedType + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}

package com.origin.hangingpot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: YourName
 * @Date: 2024/6/17 16:26
 * @Description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorItem {
    private String sql;
    private String ErrorMsg;
}

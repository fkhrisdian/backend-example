package com.be.sample.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultVO {

    private String message;
    private Object result;

    public ResultVO() { }
}

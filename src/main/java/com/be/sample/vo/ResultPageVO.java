package com.be.sample.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultPageVO extends ResultVO {

    private Integer pages;
    private Integer elements;

    public ResultPageVO() { }
}

package com.be.sample.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseVO implements Serializable {

    /**
     * Secure ID / UUID
     */
    private String id;
    private Integer version;
}

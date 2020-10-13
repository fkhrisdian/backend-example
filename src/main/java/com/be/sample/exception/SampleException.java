package com.be.sample.exception;

import com.be.sample.enums.StatusCode;
import lombok.Data;

@Data
public class SampleException extends RuntimeException {

    private StatusCode code = StatusCode.ERROR;

    public SampleException(String message) {
        super(message);
    }

    public SampleException(String message, StatusCode code) {
        super(message);
        this.code = code;
    }
}

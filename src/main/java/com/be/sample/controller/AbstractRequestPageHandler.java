package com.be.sample.controller;

import com.be.sample.exception.SampleException;
import com.be.sample.vo.ResultPageVO;
import com.be.sample.util.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class AbstractRequestPageHandler {

    public ResponseEntity<ResultPageVO> getResult() {
        ResultPageVO result = new ResultPageVO();
        try {
            return processRequest();
        } catch (SampleException e) {
            result.setMessage(e.getCode().name());
            result.setResult(e.getMessage());

            log.error("ERROR", e);
        }
        return RestUtil.getJsonResponse(result);
    }

    public abstract ResponseEntity<ResultPageVO> processRequest();
}

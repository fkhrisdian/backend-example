package com.be.sample.controller;
import com.be.sample.exception.SampleException;
import com.be.sample.vo.ResultPageVO;
import com.be.sample.vo.ResultVO;
import com.be.sample.enums.StatusCode;
import com.be.sample.util.Constants;
import com.be.sample.util.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Map;

@Slf4j
public abstract class AbstractRequestHandler {

    public ResponseEntity<ResultVO> getResult() {
        ResultVO result = new ResultVO();
        try {
            Object obj = processRequest();
            if (obj != null) {
                result.setMessage(StatusCode.OK.name());
                result.setResult(obj);
            }else {
                result.setMessage(StatusCode.OK.name());
                result.setResult(null);
            }
        } catch (SampleException e) {
            result.setMessage(e.getCode().name());
            result.setResult(e.getMessage());

            log.error("ERROR", e);
        }
        return RestUtil.getJsonResponse(result);
    }

    public abstract Object processRequest();

    public static ResponseEntity<ResultPageVO> constructListResult(Map<String, Object> pageMap) {
        ResultPageVO result = new ResultPageVO();
        try {
            Collection list = constructPageResult(pageMap, result);
            result.setResult(list);
        } catch (Exception e) {
            result.setMessage(e.getMessage());

            log.error("ERROR", e);
        }
        return RestUtil.getJsonResponse(result);
    }

    public static Collection constructPageResult(Map<String, Object> map, ResultPageVO result) {
        if (map == null) {
            result.setPages(0);
            result.setElements(0);
            result.setMessage(StatusCode.DATA_NOT_FOUND.name());
            return null;
        } else {
            Collection vos = (Collection) map.get(Constants.PageParameter.LIST_DATA);
            result.setPages(Integer.valueOf(map.get(Constants.PageParameter.TOTAL_PAGES).toString()));
            result.setElements(Integer.valueOf(map.get(Constants.PageParameter.TOTAL_ELEMENTS).toString()));
            result.setMessage(StatusCode.OK.name());
            return vos;
        }
    }
}

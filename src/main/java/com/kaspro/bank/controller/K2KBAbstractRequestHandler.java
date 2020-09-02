package com.kaspro.bank.controller;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.util.Constants;
import com.kaspro.bank.util.RestUtil;
import com.kaspro.bank.vo.GeneralResponse;
import com.kaspro.bank.vo.K2KBResultVO;
import com.kaspro.bank.vo.ResultPageVO;
import com.kaspro.bank.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

/**
 * Created by yukibuwana on 1/24/17.
 */

@Slf4j
public abstract class K2KBAbstractRequestHandler {

    public ResponseEntity<K2KBResultVO> getResult() {
        K2KBResultVO result = new K2KBResultVO();
        GeneralResponse response = new GeneralResponse();
        try {
            Object obj = processRequest();
            if (obj != null) {
                response.setResponse_status(true);
                response.setResponse_timestamp(new Timestamp(System.currentTimeMillis()).toString());
                response.setResponse_message("Success");
                response.setResponse_code("200");
                result.setGeneral_response(response);
                result.setData(obj);
            }else {
                response.setResponse_status(true);
                response.setResponse_timestamp(new Timestamp(System.currentTimeMillis()).toString());
                response.setResponse_message("Success");
                response.setResponse_code("000");
                result.setGeneral_response(response);
                result.setData(null);
            }
        } catch (NostraException e) {
            response.setResponse_status(false);
            response.setResponse_timestamp(new Timestamp(System.currentTimeMillis()).toString());
            response.setResponse_message(e.getMessage());
            response.setResponse_code("9999");
            result.setGeneral_response(response);
            result.setData(null);

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

package com.kaspro.bank.controller;

import com.kaspro.bank.services.RequestCardService;
import com.kaspro.bank.vo.K2KBResultVO;
import com.kaspro.bank.vo.RequestCardReqVO;
import com.kaspro.bank.vo.ResultVO;
import com.kaspro.bank.vo.UserReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/RequestCard")
public class RequestCardController {

    @Autowired
    RequestCardService service;

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<K2KBResultVO> add(@RequestBody final RequestCardReqVO vo) {
        log.info(vo.toString());
        K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.add(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail")
    @ResponseBody
    public ResponseEntity<K2KBResultVO> findDetail(@RequestParam(value="id", required = true) String id) {
        K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.getDetail(id);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Get")
    @ResponseBody
    public ResponseEntity<K2KBResultVO> findAll() {
        K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.getAll();
            }
        };
        return handler.getResult();
    }
}

package com.kaspro.bank.controller;

import com.kaspro.bank.services.TransferLimitService;
import com.kaspro.bank.vo.ResultVO;
import com.kaspro.bank.vo.TransferLimitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/TransferLimit")
public class TransferLimitController {

    @Autowired
    TransferLimitService transferLimitService;

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final TransferLimitVO vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return transferLimitService.add(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Update")
    @ResponseBody
    public ResponseEntity<ResultVO> update(@RequestBody final TransferLimitVO vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return transferLimitService.update(vo);
            }
        };
        return handler.getResult();
    }


    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Get")
    @ResponseBody
    public ResponseEntity<ResultVO> findAll() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return transferLimitService.findAll();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetTiers")
    @ResponseBody
    public ResponseEntity<ResultVO> findTiers() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return transferLimitService.findTiers();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail")
    @ResponseBody
    public ResponseEntity<ResultVO> findPartnerDetail(@RequestParam (value="tier", required = true) String tier) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return transferLimitService.findByTier(tier);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Delete")
    @ResponseBody
    public ResponseEntity<ResultVO> deleteTier(@RequestParam (value="tier", required = true) String tier) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return transferLimitService.deleteTier(tier);
            }
        };
        return handler.getResult();
    }
}

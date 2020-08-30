package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.PartnerToken;
import com.kaspro.bank.services.PartnerTokenService;
import com.kaspro.bank.vo.RegisterPartnerMemberVO;
import com.kaspro.bank.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/PartnerToken")
public class PartnerTokenController {

    @Autowired
    PartnerTokenService ptService;

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final PartnerToken vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return ptService.add(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Delete")
    @ResponseBody
    public ResponseEntity<ResultVO> delete(@RequestBody final Long vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return ptService.delete(vo);
            }
        };
        return handler.getResult();
    }
}

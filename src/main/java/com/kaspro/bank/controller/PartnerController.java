package com.kaspro.bank.controller;

import com.kaspro.bank.services.PartnerService;
import com.kaspro.bank.services.VirtualAccountService;
import com.kaspro.bank.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/partner")
public class PartnerController {

    @Autowired
    PartnerService partnerService;

    @Autowired
    VirtualAccountService virtualAccountService;

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Get")
    @ResponseBody
    public ResponseEntity<ResultVO> findAll() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return partnerService.findAll();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail")
    @ResponseBody
    public ResponseEntity<ResultVO> findPartnerDetail(@RequestParam (value="id", required = true) int id) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return partnerService.findDetail(id);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final RegisterPartnerVO vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return partnerService.add(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Update")
    @ResponseBody
    public ResponseEntity<ResultVO> update(@RequestBody final RegisterPartnerVO vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return partnerService.update(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/UpdateStatus")
    @ResponseBody
    public ResponseEntity<ResultVO> updateStatus(@RequestBody UpdateStatusVO vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return partnerService.updateStatus(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/notification")
    @ResponseBody
    public BNINotifResponseVO bniNotif(@RequestBody BNINotifVO vo) {
        log.info(vo.toString());
        return virtualAccountService.bniNotif(vo);
    }
}

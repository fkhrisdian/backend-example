package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.VirtualAccount;
import com.kaspro.bank.services.VirtualAccountService;
import com.kaspro.bank.vo.*;
import com.kaspro.bank.vo.Individual.IndividualUpdateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/VA")
public class VirtualAccountController {

    @Autowired
    VirtualAccountService virtualAccountService;

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/InquiryBilling")
    @ResponseBody
    public ResponseEntity<K2KBResultVO> inquiryBilling(@RequestBody final K2KBInquiryVAVO vo) {
        log.info(vo.toString());
        K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return virtualAccountService.InquiryVAInfo(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody VirtualAccount va) {
        log.info(va.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return virtualAccountService.add(va);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/BNINotif")
    @ResponseBody
    public BNINotifResponseVO bniNotif(@RequestBody BNINotifVO vo) {
        log.info(vo.toString());
        return virtualAccountService.bniNotif(vo);
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/BNIEncrypt")
    @ResponseBody
    public BNINotifResponseVO bniEncrypt(@RequestBody BNINotifPlainVO vo) {
        log.info(vo.toString());
        return virtualAccountService.encryptBNI(vo);
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Update")
    @ResponseBody
    public ResponseEntity<ResultVO> update(@RequestBody VirtualAccount vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return virtualAccountService.update(vo);
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
                return virtualAccountService.findAll();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail")
    @ResponseBody
    public ResponseEntity<ResultVO> findPartnerDetail(@RequestParam (value="partnerId", required = true) int partnerId) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return virtualAccountService.findByPartnerId(partnerId);
            }
        };
        return handler.getResult();
    }
}

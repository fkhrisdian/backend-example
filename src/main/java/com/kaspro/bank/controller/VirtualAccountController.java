package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.persistance.domain.VirtualAccount;
import com.kaspro.bank.services.VirtualAccountService;
import com.kaspro.bank.vo.ResultVO;
import com.kaspro.bank.vo.TransferLimitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/VA")
public class VirtualAccountController {

    @Autowired
    VirtualAccountService virtualAccountService;

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody VirtualAccount va) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return virtualAccountService.add(va);
            }
        };
        return handler.getResult();
    }

//    @RequestMapping(method = RequestMethod.POST,
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            value="/Update")
//    @ResponseBody
//    public ResponseEntity<ResultVO> update(@RequestBody final TransferLimitVO vo) {
//        AbstractRequestHandler handler = new AbstractRequestHandler() {
//            @Override
//            public Object processRequest() {
//                return transferLimitService.update(vo);
//            }
//        };
//        return handler.getResult();
//    }

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

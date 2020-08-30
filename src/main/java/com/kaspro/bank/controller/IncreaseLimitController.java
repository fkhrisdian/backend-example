package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.IncreaseLimit;
import com.kaspro.bank.services.IncreaseLimitService;
import com.kaspro.bank.services.TransferLimitService;
import com.kaspro.bank.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/IncreaseLimit")
public class IncreaseLimitController {

    @Autowired
    IncreaseLimitService service;

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final IncreaseLimitVO vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.add(vo);
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
                return service.getRequest();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail")
    @ResponseBody
    public ResponseEntity<ResultVO> findDetail(@RequestParam(value="id", required = true) String id) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.getRequestDetail(id);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/UpdateStatus")
    @ResponseBody
    public ResponseEntity<ResultVO> udpate(@RequestBody final ConfirmIncreaseLimitVO vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.updateStatus(vo);
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
//
//
//    @RequestMapping(method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            value="/Get")
//    @ResponseBody
//    public ResponseEntity<ResultVO> findAll() {
//        AbstractRequestHandler handler = new AbstractRequestHandler() {
//            @Override
//            public Object processRequest() {
//                return transferLimitService.findAll();
//            }
//        };
//        return handler.getResult();
//    }
//
//    @RequestMapping(method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            value="/GetTiers")
//    @ResponseBody
//    public ResponseEntity<ResultVO> findTiers() {
//        AbstractRequestHandler handler = new AbstractRequestHandler() {
//            @Override
//            public Object processRequest() {
//                return transferLimitService.findTiers();
//            }
//        };
//        return handler.getResult();
//    }
//
//    @RequestMapping(method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            value="/GetDetail")
//    @ResponseBody
//    public ResponseEntity<ResultVO> findPartnerDetail(@RequestParam (value="tier", required = true) String tier) {
//        AbstractRequestHandler handler = new AbstractRequestHandler() {
//            @Override
//            public Object processRequest() {
//                return transferLimitService.findByTier(tier);
//            }
//        };
//        return handler.getResult();
//    }
//
//    @RequestMapping(method = RequestMethod.DELETE,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            value="/Delete")
//    @ResponseBody
//    public ResponseEntity<ResultVO> deleteTier(@RequestParam (value="tier", required = true) String tier) {
//        AbstractRequestHandler handler = new AbstractRequestHandler() {
//            @Override
//            public Object processRequest() {
//                return transferLimitService.deleteTier(tier);
//            }
//        };
//        return handler.getResult();
//    }
}

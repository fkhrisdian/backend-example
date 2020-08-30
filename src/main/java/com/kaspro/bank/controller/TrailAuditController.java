package com.kaspro.bank.controller;

import com.kaspro.bank.services.TrailAuditService;
import com.kaspro.bank.services.TransferLimitService;
import com.kaspro.bank.vo.ResultVO;
import com.kaspro.bank.vo.TransferLimitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/AuditTrail")
public class TrailAuditController {

    @Autowired
    TrailAuditService taService;

//    @RequestMapping(method = RequestMethod.POST,
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            value="/Add")
//    @ResponseBody
//    public ResponseEntity<ResultVO> add(@RequestBody final TransferLimitVO vo) {
//        AbstractRequestHandler handler = new AbstractRequestHandler() {
//            @Override
//            public Object processRequest() {
//                return transferLimitService.add(vo);
//            }
//        };
//        return handler.getResult();
//    }

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


    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Get")
    @ResponseBody
    public ResponseEntity<ResultVO> findAuditTrail(@RequestParam (value="id", required = true) String id) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return taService.findByOwnerId(id);
            }
        };
        return handler.getResult();
    }
}

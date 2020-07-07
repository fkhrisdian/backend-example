package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.Individual;
import com.kaspro.bank.services.IndividualService;
import com.kaspro.bank.vo.IndividualVO;
import com.kaspro.bank.vo.RegisterPartnerMemberVO;
import com.kaspro.bank.vo.ResultVO;
import com.kaspro.bank.vo.UpdateStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/Individual")
public class IndividualController {

    @Autowired
    IndividualService iService;

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Get")
    @ResponseBody
    public ResponseEntity<ResultVO> findAll() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.findAll();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail")
    @ResponseBody
    public ResponseEntity<ResultVO> findPartnerDetail(@RequestParam(value="id", required = true) int id) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.getIndividualDetail(id);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final IndividualVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.registerIndividual(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Update")
    @ResponseBody
    public ResponseEntity<ResultVO> update(@RequestBody final IndividualVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.update(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE,
        value="/UpdateTier")
    @ResponseBody
    public ResponseEntity<ResultVO> updateTier(@RequestBody final IndividualVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.updateTier(vo);
            }
        };
        return handler.getResult();
    }

//    @RequestMapping(method = RequestMethod.POST,
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            value="/UpdateStatus")
//    @ResponseBody
//    public ResponseEntity<ResultVO> updateStatus(@RequestBody UpdateStatusVO vo) {
//        AbstractRequestHandler handler = new AbstractRequestHandler() {
//            @Override
//            public Object processRequest() {
//                return pmService.updateStatus(vo);
//            }
//        };
//        return handler.getResult();
//    }
}

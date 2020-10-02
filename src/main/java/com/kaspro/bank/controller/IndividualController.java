package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.Individual;
import com.kaspro.bank.services.IndividualService;
import com.kaspro.bank.vo.Individual.IndividualRegistrationVO;
import com.kaspro.bank.vo.Individual.IndividualReqVO;
import com.kaspro.bank.vo.Individual.IndividualResVO;
import com.kaspro.bank.vo.Individual.IndividualUpdateVO;
import com.kaspro.bank.vo.K2KBInquiryVAVO;
import com.kaspro.bank.vo.K2KBResultVO;
import com.kaspro.bank.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
            value="/Get2")
    @ResponseBody
    public ResponseEntity<ResultVO> findAll2() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.getIndividual();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetSorted")
    @ResponseBody
    public ResponseEntity<ResultVO> findAllSorted() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.findAllSorted();
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

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetLimited")
    @ResponseBody
    public ResponseEntity<ResultVO> findLimited(@RequestParam(value="limit", required = true) int limit) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.findAllSrotLimited(limit);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail2")
    @ResponseBody
    public ResponseEntity<K2KBResultVO> findPartnerDetail(@RequestBody final K2KBInquiryVAVO vo) {
        log.info(vo.toString());
        K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.k2kbGetDetail(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final IndividualRegistrationVO vo) {
        log.info(vo.toString());
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
            value="/Add2")
    @ResponseBody
    public ResponseEntity<K2KBResultVO> add2(@RequestBody final IndividualReqVO vo) {
        log.info(vo.toString());
        K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.add2(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Update2")
    @ResponseBody
    public ResponseEntity<K2KBResultVO> udpate2(@RequestBody final IndividualUpdateVO vo) {
        log.info(vo.toString());
        K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.k2kbUpdate(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Update")
    @ResponseBody
    public ResponseEntity<ResultVO> update(@RequestBody final IndividualRegistrationVO vo) {
        log.info(vo.toString());
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
    public ResponseEntity<ResultVO> updateTier(@RequestBody final IndividualRegistrationVO vo) {
        log.info(vo.toString());
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return iService.updateTier(vo);
            }
        };
        return handler.getResult();
    }
}

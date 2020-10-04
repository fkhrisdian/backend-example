package com.kaspro.bank.controller;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import com.kaspro.bank.persistance.domain.User;
import com.kaspro.bank.services.BlacklistMsisdnService;
import com.kaspro.bank.services.UserService;
import com.kaspro.bank.vo.BlacklistMsisdn.BlacklistMsisdnVO;
import com.kaspro.bank.vo.RegisterPartnerMemberVO;
import com.kaspro.bank.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/BlacklistMsisdn")
public class BlackListMsisdnController {
    @Autowired
    private BlacklistMsisdnService service;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/FileUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity saveUsers(@RequestHeader(value = "Authorization") String authorization,
                                    @RequestParam(value = "files") MultipartFile[] files) throws Exception {
        User user = userService.validateToken(authorization);
        if(user==null){
            throw new NostraException("Unauthorized", StatusCode.UNAUTHORIZED);
        }
        for (MultipartFile file : files) {
            service.saveBms(file, user.getUsername());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Get")
    @ResponseBody
    public ResponseEntity<ResultVO> findAll() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.findAllBms();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail")
    @ResponseBody
    public ResponseEntity<ResultVO> findDetail(@RequestParam(value="id", required = true) String id) {
        log.info(id);
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.findById(id);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/GetDetail2")
    @ResponseBody
    public ResponseEntity<ResultVO> findDetail2(@RequestParam(value="id", required = true) String id) {
        log.info(id);
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.findById2(id);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Delete")
    @ResponseBody
    public ResponseEntity<ResultVO> delete(@RequestParam(value="msisdn", required = true) String msisdn) {
        log.info(msisdn);
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.delete(msisdn);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestHeader(value = "Authorization") String authorization,
                                        @RequestBody final BlacklistMsisdnVO vo) {
        log.info(vo.toString());
        User user = userService.validateToken(authorization);
        if(user==null){
            throw new NostraException("Unauthorized", StatusCode.UNAUTHORIZED);
        }
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.add(vo, user.getUsername());
            }
        };
        return handler.getResult();
    }
}

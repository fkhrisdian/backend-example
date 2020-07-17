package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import com.kaspro.bank.persistance.domain.User;
import com.kaspro.bank.services.BlacklistMsisdnService;
import com.kaspro.bank.services.UserService;
import com.kaspro.bank.vo.RegisterPartnerMemberVO;
import com.kaspro.bank.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/BlacklistMsisdn")
public class BlackListMsisdnController {
    @Autowired
    private BlacklistMsisdnService service;

    @PostMapping(value = "/FileUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            service.saveBms(file);
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

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final BlacklistMsisdn vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.add(vo);
            }
        };
        return handler.getResult();
    }
}

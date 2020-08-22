package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import com.kaspro.bank.services.BlacklistMsisdnService;
import com.kaspro.bank.services.RoleService;
import com.kaspro.bank.vo.ResultVO;
import com.kaspro.bank.vo.RoleReqVO;
import com.kaspro.bank.vo.RoleResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/Role")
public class RoleController {
    @Autowired
    private RoleService service;

//    @PostMapping(value = "/FileUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
//    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
//        for (MultipartFile file : files) {
//            service.saveBms(file);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Get")
    @ResponseBody
    public ResponseEntity<ResultVO> findAll() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.getAllRole();
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Delete")
    @ResponseBody
    public ResponseEntity<ResultVO> delete(@RequestParam(value="id", required = true) String id) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.deleteRole(id);
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
                return service.getDetailRole(id);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final RoleReqVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.addRole(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Update")
    @ResponseBody
    public ResponseEntity<ResultVO> udpate(@RequestBody final RoleResVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.updateRole(vo);
            }
        };
        return handler.getResult();
    }
}

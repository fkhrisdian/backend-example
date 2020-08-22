package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.User;
import com.kaspro.bank.services.UserService;
import com.kaspro.bank.vo.*;
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
@RequestMapping("/api/v1/User")
public class UserController {
    @Autowired
    private UserService service;

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Get")
    @ResponseBody
    public ResponseEntity<ResultVO> findAll() {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.getUser();
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
                return service.getUserDetail(id);
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
                return service.deleteUser(id);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Add")
    @ResponseBody
    public ResponseEntity<ResultVO> add(@RequestBody final UserReqVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.add(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/RequestResetPassword")
    @ResponseBody
    public ResponseEntity<ResultVO> requestResetPassword(@RequestBody final RequestResetPasswordVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.resetRequest(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/ResetPassword")
    @ResponseBody
    public ResponseEntity<ResultVO> resetPassword(@RequestBody final ResetPasswordVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.resetPassword(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/ResetPassword/{token}")
    @ResponseBody
    public ResponseEntity<ResultVO> findResetPassword(@PathVariable("token") String token) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.findResetRequest(token);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Validate")
    @ResponseBody
    public ResponseEntity<ResultVO> validate(@RequestBody final LoginReqVO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.validateUser(vo);
            }
        };
        return handler.getResult();
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value="/Update")
    @ResponseBody
    public ResponseEntity<ResultVO> udpate(@RequestBody final UserRes2VO vo) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.update(vo);
            }
        };
        return handler.getResult();
    }

//    @PostMapping(value = "/users", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
//    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
//        for (MultipartFile file : files) {
//            service.saveUsers(file);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @GetMapping(value = "/users", produces = "application/json")
//    public CompletableFuture<ResponseEntity> findAllUsers() {
//        return  service.findAllUsers().thenApply(ResponseEntity::ok);
//    }
//
//
//    @GetMapping(value = "/getUsersByThread", produces = "application/json")
//    public  ResponseEntity getUsers(){
//        CompletableFuture<List<User>> users1=service.findAllUsers();
//        CompletableFuture<List<User>> users2=service.findAllUsers();
//        CompletableFuture<List<User>> users3=service.findAllUsers();
//        CompletableFuture.allOf(users1,users2,users3).join();
//        return  ResponseEntity.status(HttpStatus.OK).build();
//    }
}

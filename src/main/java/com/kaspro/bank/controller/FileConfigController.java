package com.kaspro.bank.controller;

import com.kaspro.bank.persistance.domain.FileConfig;
import com.kaspro.bank.persistance.repository.FileConfigRepository;
import com.kaspro.bank.services.FileConfigService;
import com.kaspro.bank.services.OGPEncryptionService;
import com.kaspro.bank.util.InitFileDB;
import com.kaspro.bank.util.InitFileDBHandler;
import com.kaspro.bank.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
public class FileConfigController {

    @Autowired
    private FileConfigService service;

    @Autowired
    private OGPEncryptionService ogpService;

    @PostMapping(value = "/FileConfig", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FileConfig add(@RequestBody FileConfig input){


        return service.add(input);
    }


    @GetMapping(value = "/api/v1/FileConfig/FindName/{prefix}", produces = "application/json")
    public ResponseEntity<ResultVO> findNameByPrefix(@PathVariable(value = "prefix") String prefix) throws Exception {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.findNameByPrefix(prefix);
            }
        };
        return handler.getResult();
    }

    @GetMapping(value = "/api/v1/FileConfig/FindConfig/{prefix}", produces = "application/json")
    public ResponseEntity<ResultVO> findConfigByPrefix(@PathVariable(value = "prefix") String prefix) throws Exception {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.findConfigByPrefix(prefix);
            }
        };
        return handler.getResult();
    }

    @GetMapping(value = "/FileConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileConfig> findAll(){

        return service.findAll();
    }


    @Scheduled(cron = "0 0/15 * * * *")
    @GetMapping(value = "/api/v1/FileConfigReload", produces = MediaType.APPLICATION_JSON_VALUE)
    public String reLoad(){
        List<FileConfig> listX = service.findAll();
        InitFileDB x  = InitFileDB.getInstance();
        for (FileConfig i : listX) {
            x.put(i.getParam_name(), i.getParam_value());
        }
        String result = "Reload Config ok";
        return result;
    }

    @GetMapping(value = "/FileConfigGet",produces = MediaType.APPLICATION_JSON_VALUE)
    public Object get(@RequestParam String Name){
        Object result = InitFileDBHandler.paramName(Name);
        log.info("Loading Param :" +result);
        return result;
    }

    @GetMapping(value = "/api/v1/FileConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileConfig> findAllConfig(){

        return service.findAll();
    }

    @GetMapping(value = "/api/v1/FileConfigGet",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultVO> getDetail(@RequestParam (value="id", required = true) int id) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return service.detail(id).getParam_value();
            }
        };
        return handler.getResult();
    }

    @PostMapping(value = "/api/v1/FileConfig/Update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FileConfig update(@RequestBody FileConfig input){
        return service.update(input);
    }

    @PostMapping(value = "/api/v1/FileConfig/CertificateUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity uploadCertificate(@RequestParam(value = "files") MultipartFile[] files,
                                            @RequestParam(value = "name") String name) throws Exception {
        for (MultipartFile file : files) {
            ogpService.uploadCertificate(file, name);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

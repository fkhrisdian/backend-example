package com.kaspro.bank.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaspro.bank.vo.CreateVAVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.net.www.http.HttpClient;

@Service
public class CreateVAService {

//    @Autowired
//    BniEncryption bniEncryption;
//
//    String cid = "513"; // from BNI, testing purpose
//    String key = "ffcff955e7a53ebf76cda9cd16232ac4"; // from BNI, testing purpose
//
//    public CreateVAVO createBNIVA(CreateVAVO vo){
//        CreateVAVO reqVO=new CreateVAVO();
//        ObjectMapper mapper = new ObjectMapper();
//        String data = "";
//        try {
//            data=bniEncryption.hashData(mapper.writeValueAsString(vo),cid,key);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        reqVO.setClient_id(vo.getClient_id());
//        reqVO.setData(data);
//
////        HttpClient   httpClient    = HttpClientBuilder.create().build();
//
//        return vo;
//    }
}

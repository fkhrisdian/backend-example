package com.kaspro.bank.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.PartnerRepository;
import com.kaspro.bank.persistance.repository.VirtualAccountRepository;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.vo.CreateVAResponseVO;
import com.kaspro.bank.vo.CreateVAVO;
import com.kaspro.bank.vo.EncCreateVAVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Slf4j
public class VirtualAccountService {

    @Autowired
    VirtualAccountRepository vaRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    HttpProcessingService httpProcessingService;

    BniEncryption bniEncryption;
    String cid = "513"; // from BNI, testing purpose
    String key = "ffcff955e7a53ebf76cda9cd16232ac4";

    Logger logger = LoggerFactory.getLogger(VirtualAccount.class);

    public VirtualAccount add(VirtualAccount va){

        List<String> listMsisdn=vaRepository.findMsisdn(va.getMsisdn());
        if(listMsisdn.size()>0){
            throw new NostraException("MSISDN already used by other Virtual Account", StatusCode.DATA_INTEGRITY);
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        VirtualAccount savedVA = new VirtualAccount();
        InitDB x  = InitDB.getInstance();
        String result = x.get("VA.Prefix");
        String endDate = x.get("VA.EndDate");
        String vaNumber ="";

        if(va.getMsisdn().length()>12){
            int start = va.getMsisdn().length()-12;
            vaNumber=va.getMsisdn().substring(start, va.getMsisdn().length());
            vaNumber=("000000000000"+vaNumber).substring(vaNumber.length());
        }else{
            vaNumber=va.getMsisdn();
            vaNumber=("000000000000"+vaNumber).substring(vaNumber.length());
        }

        vaNumber=result+vaNumber;
        savedVA.setOwnerID(va.getOwnerID());
        try {
            savedVA.setEndEffDate(new Date(df.parse(endDate).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        savedVA.setFlag(va.getFlag());
        savedVA.setStartEffDate(new Date(System.currentTimeMillis()));
        savedVA.setVa(vaNumber);
        savedVA.setStatus("ACTIVE");
        savedVA.setMsisdn(va.getMsisdn());
        savedVA=vaRepository.save(savedVA);

        return savedVA;
    }

    public VirtualAccount addPartnerMember(PartnerMember pm, String msisdn, DataPIC pic){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        VirtualAccount savedVA = new VirtualAccount();
        InitDB x  = InitDB.getInstance();
        String result = x.get("VA.Prefix");
        String endDate = x.get("VA.EndDate");
        String vaNumber ="";

        String partnerCode = ("000"+pm.getPartner().getPartnerCode()).substring(pm.getPartner().getPartnerCode().length());
        String partnerMemberCode = ("000000000"+pm.getPartnerMemberCode()).substring(pm.getPartnerMemberCode().length());

        vaNumber=result+partnerCode+partnerMemberCode;
        savedVA.setOwnerID(pm.getId());
        try {
            savedVA.setEndEffDate(new Date(df.parse(endDate).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        savedVA.setFlag("CPM");
        savedVA.setStartEffDate(new Date(System.currentTimeMillis()));
        savedVA.setVa(vaNumber);
        savedVA.setStatus("ACTIVE");
        savedVA.setMsisdn(msisdn);

        List<String> vas=vaRepository.findVAs(vaNumber);
        if(vas.size()>0){
            throw new NostraException("Virtual Account already exist",StatusCode.DATA_INTEGRITY);
        }

        CreateVAVO createVAVO = new CreateVAVO();
        createVAVO.setClient_id("513");
        createVAVO.setCustomer_email(pic.getEmail());
        createVAVO.setCustomer_name(pic.getName());
        createVAVO.setCustomer_phone(pic.getMsisdn());
        createVAVO.setDatetime_expired(endDate+"T00:00:00+07:00");
        createVAVO.setTrx_amount("0");
        createVAVO.setTrx_id(pic.getMsisdn());
        createVAVO.setVirtual_account(vaNumber);
        createVAVO.setDescription("Creatve VA "+vaNumber);
        createVAVO.setType("createBilling");
        createVAVO.setBilling_type("z");

        ObjectMapper obj = new ObjectMapper();
        String inputString="";
        try {
            inputString=obj.writeValueAsString(createVAVO);
            System.out.println(inputString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String data=bniEncryption.hashData(inputString, cid, key);
        EncCreateVAVO reqPost=new EncCreateVAVO();
        reqPost.setClient_id(cid);
        reqPost.setData(data);

        try {
            inputString=obj.writeValueAsString(reqPost);
            System.out.println(inputString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            String outputString=httpProcessingService.postUser("https://apibeta.bni-ecollection.com:8067/",inputString);
            Gson g = new Gson();
            CreateVAResponseVO resPost=g.fromJson(outputString,CreateVAResponseVO.class);
            if(resPost.getStatus().equals("000")){
                data=resPost.getData();
                resPost=g.fromJson(bniEncryption.parseData(data,cid,key),CreateVAResponseVO.class);

            }else {
                throw new NostraException(resPost.getMessage(),StatusCode.ERROR);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        savedVA=vaRepository.save(savedVA);

        return savedVA;
    }

    public VirtualAccount addIndividual(Individual individual){

        List<String> listMsisdn=vaRepository.findMsisdn(individual.getMsisdn());
        if(listMsisdn.size()>0){
            throw new NostraException("MSISDN already used by other Virtual Account", StatusCode.DATA_INTEGRITY);
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        VirtualAccount savedVA = new VirtualAccount();
        InitDB x  = InitDB.getInstance();
        String result = x.get("VA.Prefix");
        String endDate = x.get("VA.EndDate");
        String vaNumber ="";

        if(individual.getMsisdn().length()>12){
            int start = individual.getMsisdn().length()-12;
            vaNumber=individual.getMsisdn().substring(start, individual.getMsisdn().length());
            vaNumber=("000000000000"+vaNumber).substring(vaNumber.length());
        }else{
            vaNumber=individual.getMsisdn();
            vaNumber=("000000000000"+vaNumber).substring(vaNumber.length());
        }

        vaNumber=result+vaNumber;
        savedVA.setOwnerID(individual.getId());
        try {
            savedVA.setEndEffDate(new Date(df.parse(endDate).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        savedVA.setFlag("I");
        savedVA.setStartEffDate(new Date(System.currentTimeMillis()));
        savedVA.setVa(vaNumber);
        savedVA.setStatus("ACTIVE");
        savedVA.setMsisdn(individual.getMsisdn());

        CreateVAVO createVAVO = new CreateVAVO();
        createVAVO.setClient_id("513");
        createVAVO.setCustomer_email(individual.getEmail());
        createVAVO.setCustomer_name(individual.getName());
        createVAVO.setCustomer_phone(individual.getMsisdn());
        createVAVO.setDatetime_expired(endDate+"T00:00:00+07:00");
        createVAVO.setTrx_amount("0");
        createVAVO.setTrx_id(individual.getMsisdn());
        createVAVO.setVirtual_account(vaNumber);
        createVAVO.setDescription("Creatve VA "+vaNumber);
        createVAVO.setType("createBilling");
        createVAVO.setBilling_type("z");

        ObjectMapper obj = new ObjectMapper();
        String inputString="";
        try {
            inputString=obj.writeValueAsString(createVAVO);
            System.out.println(inputString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String data=bniEncryption.hashData(inputString, cid, key);
        EncCreateVAVO reqPost=new EncCreateVAVO();
        reqPost.setClient_id(cid);
        reqPost.setData(data);

        try {
            inputString=obj.writeValueAsString(reqPost);
            System.out.println(inputString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            String outputString=httpProcessingService.postUser("https://apibeta.bni-ecollection.com:8067/",inputString);
            Gson g = new Gson();
            CreateVAResponseVO resPost=g.fromJson(outputString,CreateVAResponseVO.class);
            if(resPost.getStatus().equals("000")){
                data=resPost.getData();
                resPost=g.fromJson(bniEncryption.parseData(data,cid,key),CreateVAResponseVO.class);

            }else {
                throw new NostraException(resPost.getMessage(),StatusCode.ERROR);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        savedVA=vaRepository.save(savedVA);

        return savedVA;
    }

    public VirtualAccount update(VirtualAccount va){
        List<VirtualAccount> listVA=vaRepository.findVA(va.getId());
        if(listVA.size()==0){
            throw new NostraException("Virtual Account Not Found", StatusCode.DATA_NOT_FOUND);
        }
        VirtualAccount savedVA=vaRepository.save(va);
        return savedVA;
    }


    public VirtualAccount findByPartnerId(int partnerId){
        VirtualAccount va = vaRepository.findByPartnerID(partnerId);
        return va;
    }

    public List<VirtualAccount> findAll(){
        return vaRepository.findAll();
    }
}

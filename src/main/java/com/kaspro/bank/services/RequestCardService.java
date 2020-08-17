package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.Individual;
import com.kaspro.bank.persistance.domain.RequestCard;
import com.kaspro.bank.persistance.repository.IndividualRepository;
import com.kaspro.bank.persistance.repository.RequestCardRepository;
import com.kaspro.bank.vo.RequestCardReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RequestCardService {
    @Autowired
    RequestCardRepository repository;

    @Autowired
    IndividualRepository iRepo;

    Logger logger = LoggerFactory.getLogger(RequestCard.class);

    @Transactional
    public RequestCard add(RequestCardReqVO vo){
        String msisdn=this.validateMsisdn(vo.getMsisdn());
        logger.info("Msisdn : "+msisdn);
        Individual individual= iRepo.findByMsisdn(msisdn);
        if(individual==null){
            throw new NostraException("Active Individual Account Not Found",StatusCode.DATA_NOT_FOUND);
        }
        RequestCard requestCard = this.convertVO(vo);
        try{
            RequestCard savedRC=repository.save(requestCard);
            return savedRC;
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new NostraException("Error during saving Request Card : "+e.getMessage(), StatusCode.ERROR);
        }
    }

    public RequestCard getDetail(String id){
        RequestCard result = repository.findByRequestID(id);
        if(result==null){
            throw new NostraException("Request Card Data Not Found", StatusCode.DATA_NOT_FOUND);
        }else{
            return result;
        }
    }

    public List<RequestCard> getAll(){
        List<RequestCard> result=repository.findAll();
        return result;
    }

    public RequestCard convertVO(RequestCardReqVO vo){
        RequestCard result=new RequestCard();
        result.setAddress(vo.getAddress());
        result.setCity(vo.getCity());
        result.setMsisdn(vo.getMsisdn());
        result.setProvince(vo.getProvince());
        result.setZipCode(vo.getZipCode());
        result.setName(vo.getName());
        return result;
    }

    public String validateMsisdn(String msisdn){
        if(msisdn.startsWith("+62")){
            return msisdn.replace("+","");

        }else if(msisdn.startsWith("08")){
            return "62"+msisdn.substring(1);
        }else if(msisdn.startsWith("62")){
            return msisdn;
        }else {
            throw new NostraException("Invalid MSISDN value",StatusCode.ERROR);
        }
    }
}

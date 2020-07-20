package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.IncreaseLimitRepository;
import com.kaspro.bank.persistance.repository.PartnerMemberRepository;
import com.kaspro.bank.persistance.repository.PartnerRepository;
import com.kaspro.bank.persistance.repository.TransferLimitRepository;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.vo.ConfirmIncreaseLimitVO;
import com.kaspro.bank.vo.KeyValuePairedVO;
import com.kaspro.bank.vo.TransferLimitVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Slf4j
public class IncreaseLimitService {

    @Autowired
    IncreaseLimitRepository repository;

    @Autowired
    PartnerRepository pRepo;

    @Autowired
    PartnerMemberRepository pmRepo;

    Logger logger = LoggerFactory.getLogger(IncreaseLimitService.class);

    @Transactional
    public IncreaseLimit add(IncreaseLimit vo){
        InitDB x = InitDB.getInstance();

        Partner partner=pRepo.findPartner(Integer.parseInt(vo.getPartnerId()));
        if(partner==null){
            throw new NostraException("Parnter ID is not found", StatusCode.DATA_NOT_FOUND);
        }

        PartnerMember partnerMember=pmRepo.findPartnerMember(Integer.parseInt(vo.getMemberId()));
        if(partnerMember==null){
            throw new NostraException("Parnter Member ID is not found", StatusCode.DATA_NOT_FOUND);
        }

        String dest=x.get("KasproBank.Destination");
        if(!dest.contains(vo.getDestination()+"|")){
            throw new NostraException("Invalid Destination", StatusCode.ERROR);
        }

        if(vo.getStartDate().after(vo.getEndDate())){
            throw new NostraException("Start Date exceeding End Date", StatusCode.ERROR);
        }

        if(vo.getAmount()==null||Long.parseLong(vo.getAmount())<=0){
            throw new NostraException("Invalid Amount", StatusCode.ERROR);
        }

        vo.setStatus("PENDING");
        vo.setPartnerName(partner.getName());
        vo.setMemberName(partnerMember.getName());
        IncreaseLimit saved=repository.save(vo);
        return saved;
    }

    public IncreaseLimit updateStatus(ConfirmIncreaseLimitVO vo){
        IncreaseLimit il=repository.findByReqId(vo.getId());
        if(il==null){
            throw new NostraException("Request not found",StatusCode.ERROR);
        }
        il.setStatus(vo.getStatus());
        il=repository.save(il);
        return il;
    }

    public List<IncreaseLimit> getRequest(){
        List<IncreaseLimit> result=repository.findAll();
        return result;
    }

    public IncreaseLimit getRequestDetail(String id){
        IncreaseLimit il=repository.findByReqId(id);
        if(il==null){
            throw new NostraException("Request not found",StatusCode.ERROR);
        }
        il=repository.save(il);
        return il;
    }

}

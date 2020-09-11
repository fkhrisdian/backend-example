package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IncreaseLimitService {

    @Autowired
    IncreaseLimitRepository repository;

    @Autowired
    PartnerRepository pRepo;

    @Autowired
    PartnerMemberRepository pmRepo;

    @Autowired
    EmailUtil emailUtil;

    Logger logger = LoggerFactory.getLogger(IncreaseLimitService.class);

    @Transactional
    public IncreaseLimit add(IncreaseLimitVO vo){
        InitDB x = InitDB.getInstance();
        String email=x.get("Email.Authorized");
        String email2=x.get("Email.Authorized2");
        String link=x.get("URL.IncreaseLimit");
        IncreaseLimit increaseLimit=new IncreaseLimit();

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

        try{
            if(Long.parseLong(vo.getAmount())<=0){
                throw new NostraException("Amount must be greater than 0",StatusCode.ERROR);
            }
        }catch (Exception e){
            throw new NostraException("Invalid value of Amount",StatusCode.ERROR);
        }

        Date startDate=Date.valueOf(vo.getRequestDate());
        Date endDate=this.addDays(Date.valueOf(vo.getRequestDate()),1);

        increaseLimit.setPartnerId(partner.getId().toString());
        increaseLimit.setPartnerName(partner.getName());
        increaseLimit.setMemberId(partnerMember.getId().toString());
        increaseLimit.setMemberName(partnerMember.getName());
        increaseLimit.setDestination(vo.getDestination());
        increaseLimit.setStartDate(startDate);
        increaseLimit.setEndDate(endDate);
        increaseLimit.setAmount(vo.getAmount());
        increaseLimit.setStatus("PENDING");

        IncreaseLimit saved=repository.save(increaseLimit);

        Map<String, Object> model = new HashMap<>();
        model.put("partnerMember",partnerMember.getName());
        model.put("link",link+""+saved.getId());
        emailUtil.sendEmail2(email,partnerMember.getName()+" Increase Daily Limit Request", "IncreaseDailyLimit.ftl",model);
        emailUtil.sendEmail2(email2,partnerMember.getName()+" Increase Daily Limit Request", "IncreaseDailyLimit.ftl",model);

        return saved;
    }

    public IncreaseLimit updateStatus(ConfirmIncreaseLimitVO vo){
        IncreaseLimit il=repository.findByReqId(vo.getId());
        if(il==null){
            throw new NostraException("Request not found",StatusCode.ERROR);
        }else if(!il.getStatus().equals("PENDING")){
            throw new NostraException("Status is already "+il.getStatus(),StatusCode.ERROR);
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
        Timestamp curTime= new Timestamp(System.currentTimeMillis());
        if(il==null){
            throw new NostraException("Request not found",StatusCode.ERROR);
        }else if(curTime.after(il.getEndDate())){
            il.setStatus("EXPIRED");
            il=repository.save(il);
        }

        return il;
    }

    public String checkIncreaseLimitResVO(String memberId, String dest){
        Timestamp currDate=new Timestamp(Calendar.getInstance().getTimeInMillis());
        IncreaseLimit il=repository.findByDest(memberId,dest);
        if(il==null){
            return null;
        }else{
            if(currDate.after(il.getStartDate())&&currDate.before(il.getEndDate())){
                logger.info("Additional Daily Limit = "+il.getAmount());
                return il.getAmount();
            }else {
                return null;
            }
        }
    }
    public Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return new Date(c.getTimeInMillis());
    }

}

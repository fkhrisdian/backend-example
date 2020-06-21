package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.DataPIC;
import com.kaspro.bank.persistance.domain.Individual;
import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.persistance.domain.VirtualAccount;
import com.kaspro.bank.persistance.repository.PartnerRepository;
import com.kaspro.bank.persistance.repository.VirtualAccountRepository;
import com.kaspro.bank.util.InitDB;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public VirtualAccount addIndividual(VirtualAccount va, Individual individual){

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

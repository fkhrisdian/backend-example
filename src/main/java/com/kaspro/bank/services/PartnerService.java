package com.kaspro.bank.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaspro.bank.converter.PartnerVOConverter;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
import com.kaspro.bank.vo.RegisterPartnerVO;
import com.kaspro.bank.vo.RegisterPartnerResponseVO;
import com.kaspro.bank.vo.TransferFeeVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;

@Service
@Slf4j
public class PartnerService {
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    LampiranRepository lampiranRepository;

    @Autowired
    TransferFeeRepository tfRepository;

    @Autowired
    DataPICRepository dataPICRepository;

    @Autowired
    VirtualAccountRepository vaRepository;

    @Autowired
    VirtualAccountService vaService;

    @Autowired
    AuditTrailService atService;

    @Autowired
    AuditTrailRepository atRepository;


    Logger logger = LoggerFactory.getLogger(PartnerService.class);

    public List<Partner> findAll(){
        List<Partner> partners = partnerRepository.findAll();
        return partners;
    }

    @Transactional
    public RegisterPartnerVO findDetail(int id){
        RegisterPartnerVO result = new RegisterPartnerVO();

        Partner partner=partnerRepository.findPartner(id);
        DataPIC dataPIC=dataPICRepository.findByPartnerID(id);
        List<Lampiran> listLampiran=lampiranRepository.findByPartnerID(id);
        List<TransferFee> transferFees=tfRepository.findByPartnerID(id);
        VirtualAccount va=vaRepository.findByPartnerID(id);
        List<AuditTrail> ats=atRepository.findByPartnerID(id);

        String tiers=partner.getTiers();
        String[] listTiers=tiers.split("\\|");

        result.setPartner(partner);
        result.setDataPIC(dataPIC);
        result.setListLampiran(listLampiran);
        result.setTransferFees(transferFees);
        result.setListTier(listTiers);
        result.setVirtualAccount(va.getVa());
        result.setAuditTrails(ats);

        return result;
    }

    @Transactional
    public RegisterPartnerVO add(RegisterPartnerVO vo){

        Date currDate = new Date(System.currentTimeMillis());
        String tiers="";
        for(String t : vo.getListTier()){
            tiers=tiers+t+"|";
        }

        logger.info("Starting insert Partner");
        Partner partner =vo.getPartner();
        logger.info("Inserting partner: "+partner.getName());
        logger.info("Tiers : "+tiers);
        partner.setTiers(tiers);
        Partner savedPartner=partnerRepository.save(partner);
        logger.info("Finished insert Partner");

        logger.info("Starting insert Data PIC");
        DataPIC dataPIC =vo.getDataPIC();
        dataPIC.setPartner(savedPartner);
        logger.info("Inserting Data PIC: "+dataPIC.getName());
        DataPIC savedPIC=dataPICRepository.save(dataPIC);
        logger.info("Finished insert Data PIC");

        logger.info("Starting insert Lampiran");
        List<Lampiran> listLampiran = vo.getListLampiran();
        List<Lampiran> savedLampirans= new ArrayList<>();
        for(Lampiran lampiran:listLampiran){
            logger.info("Inserting Lampiran: "+lampiran.getName());
            lampiran.setPartner(savedPartner);
            Lampiran savedLampiran = lampiranRepository.save(lampiran);
            savedLampirans.add(savedLampiran);
            logger.info("Finished insert Lampiran: "+lampiran.getName());

        }

        logger.info("Starting insert Transfer Fee");
        List<TransferFee> transferFees = vo.getTransferFees();
        List<TransferFee> savedTFS=new ArrayList<>();
        for(TransferFee tf:transferFees){
            logger.info("Inserting Transfer Fee: "+tf.getDestination());
            tf.setPartner(savedPartner);
            TransferFee savedTF=tfRepository.save(tf);
            savedTFS.add(savedTF);
            logger.info("Finished insert Transfer Fee: "+tf.getDestination());

        }

        logger.info("Starting insert Virtual Account");
        VirtualAccount va=vaService.add(dataPIC.getMsisdn(), savedPartner, "C");
        logger.info("Finished insert Virtual Account: "+va.getVa());

        RegisterPartnerVO savedVO = new RegisterPartnerVO();
        savedVO.setPartner(savedPartner);
        savedVO.setVirtualAccount(va.getVa());
        savedVO.setTransferFees(savedTFS);
        savedVO.setListLampiran(savedLampirans);
        savedVO.setDataPIC(savedPIC);
        savedVO.setListTier(vo.getListTier());

        ObjectMapper mapper = new ObjectMapper();
        String afterValue="";
        String beforeValue="";
        try {
            afterValue = mapper.writeValueAsString(savedVO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        logger.info("afterValue: "+afterValue);

        AuditTrail at = new AuditTrail();
        at.setStartDtm(currDate);
        at.setUserApp("System");
        at.setValueAfter(afterValue);
        at.setValueBefore(beforeValue);
        at.setPartner(savedPartner);
        atService.add(at);

        return vo;
    }
}

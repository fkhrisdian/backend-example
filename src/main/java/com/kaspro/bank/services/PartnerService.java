package com.kaspro.bank.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
import com.kaspro.bank.vo.RegisterPartnerVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
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
    TrailAuditService taService;

    @Autowired
    AuditTrailService atService;

    @Autowired
    AuditTrailRepository atRepository;

    @Autowired
    TransferLimitRepository tlRepository;


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
        List<TransferLimit> transferLimits=new ArrayList<>();

        String tiers=partner.getTiers();
        String[] listTiers=tiers.split("\\|");

        for(String t:listTiers){
            transferLimits.addAll(tlRepository.findByTier(t));
        }

        result.setPartner(partner);
        result.setDataPIC(dataPIC);
        result.setListLampiran(listLampiran);
        result.setTransferFees(transferFees);
        result.setListTier(listTiers);
        result.setTransferLimitList(transferLimits);

        return result;
    }

    @Transactional
    public RegisterPartnerVO add(RegisterPartnerVO vo){

        List<String> listAlias = partnerRepository.findAlias(vo.getPartner().getAlias());
        if(listAlias.size()>0){
            throw new NostraException("Alias already exist", StatusCode.DATA_INTEGRITY);
        }

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
        dataPIC.setOwnerID(savedPartner.getId());
        dataPIC.setFlag("CP");
        logger.info("Inserting Data PIC: "+dataPIC.getName());
        DataPIC savedPIC=dataPICRepository.save(dataPIC);
        logger.info("Finished insert Data PIC");

        logger.info("Starting insert Lampiran");
        List<Lampiran> listLampiran = vo.getListLampiran();
        List<Lampiran> savedLampirans= new ArrayList<>();
        for(Lampiran lampiran:listLampiran){
            logger.info("Inserting Lampiran: "+lampiran.getName());
            lampiran.setOwnerID(savedPartner.getId());
            lampiran.setFlag("CP");
            Lampiran savedLampiran = lampiranRepository.save(lampiran);
            savedLampirans.add(savedLampiran);
            logger.info("Finished insert Lampiran: "+lampiran.getName());

        }

        logger.info("Starting insert Transfer Fee");
        List<TransferFee> transferFees = vo.getTransferFees();
        List<TransferFee> savedTFS=new ArrayList<>();
        for(TransferFee tf:transferFees){
            logger.info("Inserting Transfer Fee: "+tf.getDestination());
            tf.setOwnerID(savedPartner.getId());
            TransferFee savedTF=tfRepository.save(tf);
            savedTFS.add(savedTF);
            logger.info("Finished insert Transfer Fee: "+tf.getDestination());

        }

        ObjectMapper mapper = new ObjectMapper();
        String afterValue="";
        String beforeValue="";

        RegisterPartnerVO savedVO = new RegisterPartnerVO();
        savedVO.setPartner(savedPartner);
        savedVO.setTransferFees(savedTFS);
        savedVO.setListLampiran(savedLampirans);
        savedVO.setDataPIC(savedPIC);
        savedVO.setListTier(vo.getListTier());

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
        at.setOwnerID(savedPartner.getId());
        atService.add(at);

        return savedVO;
    }

    @Transactional
    public RegisterPartnerVO update(RegisterPartnerVO vo){

        List<Partner> listPartner = partnerRepository.findListPartner(vo.getPartner().getId());
        if(listPartner.size()==0){
            throw new NostraException("Partner not found", StatusCode.DATA_NOT_FOUND);
        }

        Date currDate = new Date(System.currentTimeMillis());
        String tiers="";
        for(String t : vo.getListTier()){
            tiers=tiers+t+"|";
        }

        Partner partner =vo.getPartner();

        TrailAudit ta=new TrailAudit();
        ta.setStartDtm(currDate);
        ta.setUser("System");
        ta.setOwnerID(partner.getId().toString());

        logger.info("Starting update Tiers");

        if(!tiers.equals(listPartner.get(0).getTiers())){
            ta.setField("Tiers");
            ta.setValueBefore(listPartner.get(0).getTiers());
            ta.setValueAfter(tiers);
            partnerRepository.udpateTier(tiers,partner.getId());
            taService.add(ta);
        }
        Partner savedPartner=partnerRepository.findPartner(partner.getId());
        logger.info("Finished insert Partner");

        logger.info("Starting insert Data PIC");
        DataPIC savedPIC=dataPICRepository.findByPartnerID(savedPartner.getId());
        DataPIC dataPIC =vo.getDataPIC();
        if(!savedPIC.getMsisdn().equals(dataPIC.getMsisdn())){
            ta.setField("MSISDN");
            ta.setValueBefore(savedPIC.getMsisdn());
            ta.setValueAfter(dataPIC.getMsisdn());
            taService.add(ta);
        }
        if(!savedPIC.getName().equals(dataPIC.getName())){
            ta.setField("Nama");
            ta.setValueBefore(savedPIC.getName());
            ta.setValueAfter(dataPIC.getName());
            taService.add(ta);
        }
        if(!savedPIC.getAlamat().equals(dataPIC.getAlamat())){
            ta.setField("Alamat");
            ta.setValueBefore(savedPIC.getAlamat());
            ta.setValueAfter(dataPIC.getAlamat());
            taService.add(ta);
        }
        if(!savedPIC.getEmail().equals(dataPIC.getEmail())){
            ta.setField("Email");
            ta.setValueBefore(savedPIC.getEmail());
            ta.setValueAfter(dataPIC.getEmail());
            taService.add(ta);
        }
        if(!savedPIC.getKtp().equals(dataPIC.getKtp())){
            ta.setField("KTP");
            ta.setValueBefore(savedPIC.getKtp());
            ta.setValueAfter(dataPIC.getKtp());
            taService.add(ta);
        }
        if(!savedPIC.getNpwp().equals(dataPIC.getNpwp())){
            ta.setField("NPWP");
            ta.setValueBefore(savedPIC.getNpwp());
            ta.setValueAfter(dataPIC.getNpwp());
            taService.add(ta);
        }
        logger.info("Inserting Data PIC: "+dataPIC.getName());
        savedPIC=dataPICRepository.save(dataPIC);
        logger.info("Finished insert Data PIC");

        logger.info("Starting insert Lampiran");
        List<Lampiran> listLampiran = vo.getListLampiran();
        List<Lampiran> savedLampirans= new ArrayList<>();
        for(Lampiran lampiran:listLampiran){
            logger.info("Inserting Lampiran: "+lampiran.getName());
            Lampiran savedLampiran = lampiranRepository.findDetail(savedPartner.getId(),lampiran.getName());
            if(!savedLampiran.getUrl().equals(lampiran.getUrl())){
                ta.setField(savedLampiran.getName());
                ta.setValueBefore(savedLampiran.getUrl());
                ta.setValueAfter(lampiran.getUrl());
                savedLampiran = lampiranRepository.save(lampiran);
                taService.add(ta);
            }
            savedLampirans.add(savedLampiran);
            logger.info("Finished insert Lampiran: "+lampiran.getName());

        }

        logger.info("Starting insert Transfer Fee");
        List<TransferFee> transferFees = vo.getTransferFees();
        List<TransferFee> savedTFS=new ArrayList<>();
        for(TransferFee tf:transferFees){
            logger.info("Inserting Transfer Fee: "+tf.getDestination());
            TransferFee savedTF=tfRepository.findDetail(savedPartner.getId(), tf.getDestination());
            if(!savedTF.getFee().equals(tf.getFee())){
                ta.setField(tf.getDestination());
                ta.setValueBefore(savedTF.getFee().toString());
                ta.setValueAfter(tf.getFee().toString());
                savedTF = tfRepository.save(tf);
                taService.add(ta);
            }
            savedTFS.add(savedTF);
            logger.info("Finished insert Transfer Fee: "+tf.getDestination());

        }

//        ObjectMapper mapper = new ObjectMapper();
//        String afterValue="";
//        String beforeValue="";

        RegisterPartnerVO savedVO = new RegisterPartnerVO();
        savedVO.setPartner(savedPartner);
        savedVO.setTransferFees(savedTFS);
        savedVO.setListLampiran(savedLampirans);
        savedVO.setDataPIC(savedPIC);
        savedVO.setListTier(vo.getListTier());

//        try {
//            afterValue = mapper.writeValueAsString(savedVO);
//            beforeValue = mapper.writeValueAsString(vo);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            throw new NostraException("Failure while updating Audit Trail",StatusCode.ERROR);
//        }

//        logger.info("afterValue: "+afterValue);
//
//        AuditTrail at = new AuditTrail();
//        at.setStartDtm(currDate);
//        at.setUserApp("System");
//        at.setValueAfter(afterValue);
//        at.setValueBefore(beforeValue);
//        at.setOwnerID(savedPartner.getId());
//        atService.add(at);

        return savedVO;
    }
}

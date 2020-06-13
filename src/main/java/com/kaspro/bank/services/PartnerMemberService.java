package com.kaspro.bank.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
import com.kaspro.bank.vo.RegisterPartnerMemberVO;
import com.kaspro.bank.vo.RegisterPartnerVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PartnerMemberService {
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    PartnerMemberRepository pmRepository;

    @Autowired
    TransferInfoMemberRepository tiRepository;

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


    Logger logger = LoggerFactory.getLogger(PartnerMemberService.class);

    public List<PartnerMember> findAll(){
        List<PartnerMember> pms = pmRepository.findAll();
        return pms;
    }

    @Transactional
    public RegisterPartnerMemberVO findDetail(int id){
        RegisterPartnerMemberVO result = new RegisterPartnerMemberVO();

        PartnerMember partnerMember=pmRepository.findPartnerMember(id);
        DataPIC dataPIC=dataPICRepository.findByPartnerID(id);
        List<Lampiran> listLampiran=lampiranRepository.findByPartnerID(id);
        VirtualAccount va=vaRepository.findByPartnerID(id);
        List<AuditTrail> ats=atRepository.findByPartnerID(id);
        List<TransferInfoMember> tis=tiRepository.findByPartnerID(id);


        result.setPartnerMember(partnerMember);
        result.setDataPIC(dataPIC);
        result.setListLampiran(listLampiran);
        result.setVirtualAccount(va);
        result.setListTransferInfoMember(tis);
        result.setAuditTrails(ats);

        return result;
    }

    @Transactional
    public RegisterPartnerMemberVO add(RegisterPartnerMemberVO vo){

        String serviceName=vo.getServiceName();
        Date currDate = new Date(System.currentTimeMillis());
        String tiers="";

        logger.info("Starting insert Partner Member");
        PartnerMember partnerMember =vo.getPartnerMember();
        logger.info("Inserting partner: "+partnerMember.getName());
        PartnerMember savedPartnerMember=pmRepository.save(partnerMember);
        logger.info("Finished insert Partner");

        logger.info("Starting insert Data PIC");
        DataPIC dataPIC =vo.getDataPIC();
        dataPIC.setOwnerID(savedPartnerMember.getId());
        dataPIC.setFlag("CPM");
        logger.info("Inserting Data PIC: "+dataPIC.getName());
        DataPIC savedPIC=dataPICRepository.save(dataPIC);
        logger.info("Finished insert Data PIC");

        logger.info("Starting insert Lampiran");
        List<Lampiran> listLampiran = vo.getListLampiran();
        List<Lampiran> savedLampirans= new ArrayList<>();
        for(Lampiran lampiran:listLampiran){
            logger.info("Inserting Lampiran: "+lampiran.getName());
            lampiran.setOwnerID(savedPartnerMember.getId());
            lampiran.setFlag("CPM");
            Lampiran savedLampiran = lampiranRepository.save(lampiran);
            savedLampirans.add(savedLampiran);
            logger.info("Finished insert Lampiran: "+lampiran.getName());

        }

        logger.info("Starting insert Transfer Info Member");
        List<TransferInfoMember> tis = vo.getListTransferInfoMember();
        List<TransferInfoMember> savedTIS= new ArrayList<>();
        for(TransferInfoMember ti:tis){
            logger.info("Inserting Transfer Info Member: "+ti.getName());
            ti.setOwnerID(savedPartnerMember.getId());
            ti.setFlag("CPM");
            TransferInfoMember savedTI = tiRepository.save(ti);
            savedTIS.add(savedTI);
            logger.info("Finished insert Lampiran: "+ti.getName());

        }


        ObjectMapper mapper = new ObjectMapper();
        String afterValue="";
        String beforeValue="";
        VirtualAccount va=new VirtualAccount();

        logger.info("Starting insert Virtual Account");
        try{
            if(serviceName.equals("PARTNER_MEMBER_ADD")){
                va=vaService.add(dataPIC.getMsisdn(), savedPartnerMember.getId(), "CPM");
            }else if(serviceName.equals("PARTNER_MEMBER_MODIFY")){
                beforeValue = mapper.writeValueAsString(vo);
                VirtualAccount oldVA = vaRepository.findByPartnerID(savedPartnerMember.getId());
                if(oldVA.getMsisdn().equals(dataPIC.getMsisdn())){
                    //Do nothing
                }else{
                    va=vaService.add(dataPIC.getMsisdn(), savedPartnerMember.getId(), "CPM");
                    oldVA.setStatus("INACTIVE");
                    vaService.update(oldVA);
                }
            }
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        logger.info("Finished insert Virtual Account: "+va.getVa());

        RegisterPartnerMemberVO savedVO = new RegisterPartnerMemberVO();
        savedVO.setPartnerMember(savedPartnerMember);
        savedVO.setVirtualAccount(va);
        savedVO.setListLampiran(savedLampirans);
        savedVO.setDataPIC(savedPIC);
        savedVO.setListTransferInfoMember(savedTIS);

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
        at.setOwnerID(savedPartnerMember.getId());
        atService.add(at);

        return savedVO;
    }

//    @Transactional
//    public RegisterPartnerVO update(RegisterPartnerVO vo){
//
//        Date currDate = new Date(System.currentTimeMillis());
//        String tiers="";
//        for(String t : vo.getListTier()){
//            tiers=tiers+t+"|";
//        }
//
//        logger.info("Starting insert Partner");
//        Partner partner =vo.getPartner();
//        logger.info("Inserting partner: "+partner.getName());
//        logger.info("Tiers : "+tiers);
//        partner.setTiers(tiers);
//        Partner savedPartner=partnerRepository.save(partner);
//        logger.info("Finished insert Partner");
//
//        logger.info("Starting insert Data PIC");
//        DataPIC dataPIC =vo.getDataPIC();
//        dataPIC.setPartner(savedPartner);
//        logger.info("Inserting Data PIC: "+dataPIC.getName());
//        DataPIC savedPIC=dataPICRepository.save(dataPIC);
//        logger.info("Finished insert Data PIC");
//
//        logger.info("Starting insert Lampiran");
//        List<Lampiran> listLampiran = vo.getListLampiran();
//        List<Lampiran> savedLampirans= new ArrayList<>();
//        for(Lampiran lampiran:listLampiran){
//            logger.info("Inserting Lampiran: "+lampiran.getName());
//            lampiran.setPartner(savedPartner);
//            Lampiran savedLampiran = lampiranRepository.save(lampiran);
//            savedLampirans.add(savedLampiran);
//            logger.info("Finished insert Lampiran: "+lampiran.getName());
//
//        }
//
//        logger.info("Starting insert Transfer Fee");
//        List<TransferFee> transferFees = vo.getTransferFees();
//        List<TransferFee> savedTFS=new ArrayList<>();
//        for(TransferFee tf:transferFees){
//            logger.info("Inserting Transfer Fee: "+tf.getDestination());
//            tf.setPartner(savedPartner);
//            TransferFee savedTF=tfRepository.save(tf);
//            savedTFS.add(savedTF);
//            logger.info("Finished insert Transfer Fee: "+tf.getDestination());
//
//        }
//
//        logger.info("Starting insert Virtual Account");
//        VirtualAccount va=vaService.add(dataPIC.getMsisdn(), savedPartner, "C");
//        logger.info("Finished insert Virtual Account: "+va.getVa());
//
//        RegisterPartnerVO savedVO = new RegisterPartnerVO();
//        savedVO.setPartner(savedPartner);
//        savedVO.setVirtualAccount(va);
//        savedVO.setTransferFees(savedTFS);
//        savedVO.setListLampiran(savedLampirans);
//        savedVO.setDataPIC(savedPIC);
//        savedVO.setListTier(vo.getListTier());
//
//        ObjectMapper mapper = new ObjectMapper();
//        String afterValue="";
//        String beforeValue="";
//        try {
//            afterValue = mapper.writeValueAsString(savedVO);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        logger.info("afterValue: "+afterValue);
//
//        AuditTrail at = new AuditTrail();
//        at.setStartDtm(currDate);
//        at.setUserApp("System");
//        at.setValueAfter(afterValue);
//        at.setValueBefore(beforeValue);
//        at.setPartner(savedPartner);
//        atService.add(at);
//
//        return vo;
//    }
}

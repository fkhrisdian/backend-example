package com.kaspro.bank.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
import com.kaspro.bank.vo.KeyValuePairedVO;
import com.kaspro.bank.vo.RegisterPartnerVO;
import com.kaspro.bank.vo.UpdateStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Part;
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

    @Autowired
    PartnerMemberRepository pmRepository;

    @Autowired
    PartnerTokenRepository ptRepo;

    @Autowired
    PartnerTokenPointerRepository ptpRepo;

    @Autowired
    PartnerMemberTokenRepository pmtRepo;


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

        PartnerTokenPointer ptp = new PartnerTokenPointer();
        ptp=ptpRepo.save(ptp);

        PartnerToken pt =ptRepo.findPT(ptp.getId());
        try{
            ptRepo.delete(pt);
        }catch (Exception e){

        }finally {
            ptRepo.flush();
        }

        for(int i=1;i<100;i++){
            PartnerMemberToken pmt=new PartnerMemberToken();
            pmt.setPartnerCode(pt.getPartnerCode());
            pmt.setPartnerMemberCode(new Long(i));
            pmtRepo.saveAndFlush(pmt);

        }

        List<Partner> partners = partnerRepository.findAlias(vo.getPartner().getAlias());
        if(partners.size()>0){
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
        partner.setPartnerCode(pt.getPartnerCode().toString());
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

        Set<String> aSet = new HashSet<String>(Arrays.asList(vo.getListTier()));
        String oldTier=listPartner.get(0).getTiers();
        String[] oldTiers=oldTier.split("\\|");
        List<String> missingTiers = new ArrayList<String>();
        for(String tier:oldTiers){
            if(!aSet.contains(tier)){
                logger.info("Missing Tier: "+tier);
                missingTiers.add(tier);
            }
        }

        List<Integer> impactedPMS=new ArrayList<Integer>();
        if(missingTiers.size()>0){
            for (String missingT:missingTiers){
                impactedPMS=pmRepository.findUsedTier(missingT,vo.getPartner().getAlias());
                if(impactedPMS.size()>0){
                    throw new NostraException("Tier used by Partner Member(s)",StatusCode.ERROR);
                }
            }
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
            savedPIC.setMsisdn(dataPIC.getMsisdn());
        }
        if(!savedPIC.getName().equals(dataPIC.getName())){
            ta.setField("Nama");
            ta.setValueBefore(savedPIC.getName());
            ta.setValueAfter(dataPIC.getName());
            taService.add(ta);
            savedPIC.setName(dataPIC.getName());
        }
        if(!savedPIC.getAlamat().equals(dataPIC.getAlamat())){
            ta.setField("Alamat");
            ta.setValueBefore(savedPIC.getAlamat());
            ta.setValueAfter(dataPIC.getAlamat());
            taService.add(ta);
            savedPIC.setAlamat(dataPIC.getAlamat());
        }
        if(!savedPIC.getEmail().equals(dataPIC.getEmail())){
            ta.setField("Email");
            ta.setValueBefore(savedPIC.getEmail());
            ta.setValueAfter(dataPIC.getEmail());
            taService.add(ta);
            savedPIC.setEmail(dataPIC.getEmail());
        }
        if(!savedPIC.getKtp().equals(dataPIC.getKtp())){
            ta.setField("KTP");
            ta.setValueBefore(savedPIC.getKtp());
            ta.setValueAfter(dataPIC.getKtp());
            taService.add(ta);
            savedPIC.setKtp(dataPIC.getKtp());
        }
        if(!savedPIC.getNpwp().equals(dataPIC.getNpwp())){
            ta.setField("NPWP");
            ta.setValueBefore(savedPIC.getNpwp());
            ta.setValueAfter(dataPIC.getNpwp());
            taService.add(ta);
            savedPIC.setNpwp(dataPIC.getNpwp());
        }
        logger.info("Inserting Data PIC: "+dataPIC.getName());
        savedPIC=dataPICRepository.save(savedPIC);
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
                taService.add(ta);
                savedLampiran.setUrl(lampiran.getUrl());
                savedLampiran = lampiranRepository.save(savedLampiran);
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
            if(savedTF.getFee().compareTo(tf.getFee()) != 0){
                ta.setField(tf.getDestination());
                ta.setValueBefore(savedTF.getFee().toString());
                ta.setValueAfter(tf.getFee().toString());
                taService.add(ta);
                savedTF.setFee(tf.getFee());
                savedTF = tfRepository.save(savedTF);
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

    @Transactional
    public String updateStatus(UpdateStatusVO vo){
        List<Partner> partners = partnerRepository.findListPartner(vo.getId());
        if(partners.size()>0){
            partnerRepository.udpateStatus(vo.getStatus(), vo.getId());
        }else{
            throw new NostraException("Partner Does not Exist",StatusCode.DATA_NOT_FOUND);
        }
        return "Partner Status Has Been Updated";
    }
}

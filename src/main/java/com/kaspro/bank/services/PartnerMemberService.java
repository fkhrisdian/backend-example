package com.kaspro.bank.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.*;
import com.kaspro.bank.vo.RegisterPartnerMemberVO;
import com.kaspro.bank.vo.RegisterPartnerVO;
import com.kaspro.bank.vo.UpdateStatusVO;
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

    @Autowired
    TrailAuditService taService;

    @Autowired
    PartnerMemberTokenRepository pmtRepo;

    @Autowired
    TransferLimitRepository tlRepo;

    @Autowired
    UsageAccumulatorRepository uaRepo;


    Logger logger = LoggerFactory.getLogger(PartnerMemberService.class);

    public List<PartnerMember> findAll(){
        List<PartnerMember> pms = pmRepository.findAll();
        return pms;
    }

    @Transactional
    public RegisterPartnerMemberVO findDetail(int id){
        RegisterPartnerMemberVO result = new RegisterPartnerMemberVO();

        PartnerMember partnerMember=pmRepository.findPartnerMember(id);
        if(partnerMember==null){
            throw new NostraException("Partnet Member not found",StatusCode.DATA_NOT_FOUND);
        }
        DataPIC dataPIC=dataPICRepository.findByPartnerID(id);
        List<Lampiran> listLampiran=lampiranRepository.findByPartnerID(id);
        VirtualAccount va=vaRepository.findByPartnerID(id);
        List<TransferInfoMember> tis=tiRepository.findByPartnerID(id);


        result.setPartnerMember(partnerMember);
        result.setDataPIC(dataPIC);
        result.setListLampiran(listLampiran);
        result.setVirtualAccount(va);
        result.setListTransferInfoMember(tis);

        return result;
    }

    @Transactional
    public RegisterPartnerMemberVO add(RegisterPartnerMemberVO vo){


        List<Partner> partners=partnerRepository.findAlias(vo.getPartnerMember().getPartnerAlias());
        if(partners.size()==0){
            throw new NostraException("Partner Alias Does Not Exist",StatusCode.DATA_NOT_FOUND);
        }

        PartnerMemberToken pmt=pmtRepo.findMinId(partners.get(0).getPartnerCode());

        try{
            pmtRepo.delete(pmt);
        }finally {
            pmtRepo.flush();
        }

        List<String> names=pmRepository.findName(vo.getPartnerMember().getName());
        if(names.size()>0){
            throw new NostraException("Partner Member with same name already exist", StatusCode.DATA_INTEGRITY);
        }


        logger.info("Starting insert Partner Member");
        PartnerMember partnerMember =vo.getPartnerMember();
        logger.info("Inserting partner: "+partnerMember.getName());
        partnerMember.setPartner(partners.get(0));
        partnerMember.setPartnerMemberCode(pmt.getPartnerMemberCode().toString());
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
            if(savedTI.getName().equals("TierLimit")){
                List<TransferLimit> tls=tlRepo.findByTier(savedTI.getValue());
                for(TransferLimit tl:tls){
                    UsageAccumulator ua=new UsageAccumulator();
                    ua.setDestination(tl.getDestination());
                    ua.setOwnerId(savedPartnerMember.getId());
                    ua.setTier(tl.getTierType());
                    ua.setUsage("0");
                    uaRepo.save(ua);
                }
            }
            logger.info("Finished insert Transfer Info Member: "+ti.getName());

        }

        VirtualAccount savedVA=vaService.addPartnerMember(savedPartnerMember,savedPIC.getMsisdn(), savedPIC);

        logger.info("Finished insert Virtual Account: "+savedVA.getVa());

        RegisterPartnerMemberVO savedVO = new RegisterPartnerMemberVO();
        savedVO.setPartnerMember(savedPartnerMember);
        savedVO.setVirtualAccount(savedVA);
        savedVO.setListLampiran(savedLampirans);
        savedVO.setDataPIC(savedPIC);
        savedVO.setListTransferInfoMember(savedTIS);

        return savedVO;
    }

    @Transactional
    public RegisterPartnerMemberVO update(RegisterPartnerMemberVO vo){

        List<PartnerMember> pms=pmRepository.findListPartnerMember(vo.getPartnerMember().getId());
        if(pms.size()==0){
            throw new NostraException("Partner Member does not exist",StatusCode.DATA_NOT_FOUND);
        }

        Date currDate = new Date(System.currentTimeMillis());


        PartnerMember savedPartnerMember=pms.get(0);

        TrailAudit ta=new TrailAudit();
        ta.setStartDtm(currDate);
        ta.setUser("System");
        ta.setOwnerID(savedPartnerMember.getId().toString());

        logger.info("Starting insert Data PIC");
        DataPIC savedPIC=dataPICRepository.findByPartnerID(savedPartnerMember.getId());
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
            Lampiran savedLampiran = lampiranRepository.findDetail(savedPartnerMember.getId(),lampiran.getName());
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

        logger.info("Starting insert Transfer Info Member");
        List<TransferInfoMember> tis = vo.getListTransferInfoMember();
        List<TransferInfoMember> savedTIS= new ArrayList<>();
        for(TransferInfoMember ti:tis){
            logger.info("Inserting Transfer Info Member: "+ti.getName());
            TransferInfoMember savedTI=tiRepository.findDetail(savedPartnerMember.getId(),ti.getName());
            if(!savedTI.getValue().equals(ti.getValue())){
                ta.setField(ti.getName());
                ta.setValueBefore(savedTI.getValue());
                ta.setValueAfter(ti.getValue());
                taService.add(ta);
                savedTI.setValue(ti.getValue());
                savedTI = tiRepository.save(savedTI);
            }
            savedTIS.add(savedTI);
            logger.info("Finished insert Transfer Info Member: "+ti.getName());

        }

//        VirtualAccount va=new VirtualAccount();
//        va.setOwnerID(savedPartnerMember.getId());
//        va.setMsisdn(savedPIC.getMsisdn());
//        va.setFlag("CPM");
        VirtualAccount savedVA=vaRepository.findByPartnerID(savedPartnerMember.getId());
//        VirtualAccount oldVA = vaRepository.findByPartnerID(savedPartnerMember.getId());
//        if(oldVA != null && !oldVA.getMsisdn().equals(dataPIC.getMsisdn())){
//            logger.info("Starting insert Virtual Account");
//            savedVA=vaService.add(va);
//            oldVA.setStatus("INACTIVE");
//            ta.setField("Virtual Account");
//            ta.setValueBefore(oldVA.getVa());
//            ta.setValueAfter(savedVA.getVa());
//            vaService.update(oldVA);
//            taService.add(ta);
//            logger.info("Finished insert Virtual Account: "+va.getVa());
//        }

        RegisterPartnerMemberVO savedVO = new RegisterPartnerMemberVO();
        savedVO.setPartnerMember(savedPartnerMember);
        savedVO.setVirtualAccount(savedVA);
        savedVO.setListLampiran(savedLampirans);
        savedVO.setDataPIC(savedPIC);
        savedVO.setListTransferInfoMember(savedTIS);

        return savedVO;
    }

    @Transactional
    public String updateStatus(UpdateStatusVO vo){
        List<PartnerMember> partnerMembers = pmRepository.findListPartnerMember(vo.getId());
        if(partnerMembers.size()>0){
            pmRepository.udpateStatus(vo.getStatus(), vo.getId());
        }else{
            throw new NostraException("Partner Member Does not Exist",StatusCode.DATA_NOT_FOUND);
        }
        return "Partner Member Status Has Been Updated";
    }
}

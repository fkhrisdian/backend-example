package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.*;
import com.kaspro.bank.persistance.repository.IndividualRepository;
import com.kaspro.bank.persistance.repository.TransferLimitRepository;
import com.kaspro.bank.persistance.repository.UsageAccumulatorRepository;
import com.kaspro.bank.persistance.repository.VirtualAccountRepository;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.vo.Individual.IndividualRegistrationVO;
import com.kaspro.bank.vo.UpdateVAVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class IndividualService {

    @Autowired
    IndividualRepository iRepo;

    @Autowired
    VirtualAccountRepository vaRepository;

    @Autowired
    VirtualAccountService vaService;

    @Autowired
    TrailAuditService taService;

    @Autowired
    TransferLimitRepository tlRepo;

    @Autowired
    UsageAccumulatorRepository uaRepo;

    @Transactional
    public IndividualRegistrationVO registerIndividual(IndividualRegistrationVO vo){
        Individual existingIndividual=iRepo.findByMsisdn(vo.getIndividual().getMsisdn());
        if(existingIndividual!=null){
            throw new NostraException("MSISDN is used by other subscriber", StatusCode.DATA_INTEGRITY);
        }

        Individual savedIndividual=iRepo.save(vo.getIndividual());
        VirtualAccount va= new VirtualAccount();

        try{
            va=vaService.addIndividual(savedIndividual);
            savedIndividual.setVa(va.getVa());
            savedIndividual=iRepo.save(savedIndividual);
        }catch (NostraException ne){
            throw new NostraException(ne.getMessage(),StatusCode.ERROR);
        }

        List<TransferLimit> tls=tlRepo.findByTier(savedIndividual.getTier());
        for(TransferLimit tl:tls){
            UsageAccumulator ua=new UsageAccumulator();
            ua.setDestination(tl.getDestination());
            ua.setOwnerId(savedIndividual.getId());
            ua.setTier(tl.getTierType());
            ua.setUsage("0");
            uaRepo.save(ua);
        }

        IndividualRegistrationVO savedVO= new IndividualRegistrationVO();
        savedVO.setIndividual(savedIndividual);
        savedVO.setVirtualAccount(va);
        savedVO.setTransferLimits(tlRepo.findByTier(savedIndividual.getTier()));

        return savedVO;
    }

    public List<Individual> findAll(){
        List<Individual> list=iRepo.findAll();

        return list;
    }

    public IndividualRegistrationVO update(IndividualRegistrationVO individualVO){
        InitDB initDB=InitDB.getInstance();
        Individual vo=individualVO.getIndividual();
        Individual savedIndividual=iRepo.findIndividual(vo.getId());
        if(savedIndividual==null){
            throw new NostraException("Subscriber not found",StatusCode.DATA_NOT_FOUND);
        }

        TrailAudit ta=new TrailAudit();
        ta.setOwnerID(savedIndividual.getId().toString());
        ta.setUser("System");
        if(!vo.getAdditional_info().equals(savedIndividual.getAdditional_info())){
            ta.setField("Additional Info");
            ta.setValueBefore(savedIndividual.getAdditional_info());
            ta.setValueAfter(vo.getAdditional_info());
            taService.add(ta);
            savedIndividual.setAdditional_info(vo.getAdditional_info());
        }
        if(!vo.getAddress().equals(savedIndividual.getAddress())){
            ta.setField("Address");
            ta.setValueBefore(savedIndividual.getAddress());
            ta.setValueAfter(vo.getAddress());
            taService.add(ta);
            savedIndividual.setAddress(vo.getAddress());
        }
        if(!vo.getBirth_place().equals(savedIndividual.getBirth_place())){
            ta.setField("Birth Place");
            ta.setValueBefore(savedIndividual.getBirth_place());
            ta.setValueAfter(vo.getBirth_place());
            taService.add(ta);
            savedIndividual.setBirth_place(vo.getBirth_place());
        }
        if(!vo.getBirth_date().equals(savedIndividual.getBirth_date())){
            ta.setField("Birth Date");
            ta.setValueBefore(savedIndividual.getBirth_date());
            ta.setValueAfter(vo.getBirth_date());
            taService.add(ta);
            savedIndividual.setBirth_date(vo.getBirth_date());
        }
        if(!vo.getCity().equals(savedIndividual.getCity())){
            ta.setField("City");
            ta.setValueBefore(savedIndividual.getCity());
            ta.setValueAfter(vo.getCity());
            taService.add(ta);
            savedIndividual.setCity(vo.getCity());
        }
        if(!vo.getCountry_code().equals(savedIndividual.getCountry_code())){
            ta.setField("Country Code");
            ta.setValueBefore(savedIndividual.getCountry_code());
            ta.setValueAfter(vo.getCountry_code());
            taService.add(ta);
            savedIndividual.setCountry_code(vo.getCountry_code());
        }
        if(!vo.getEmail().equals(savedIndividual.getEmail())){
            ta.setField("Email");
            ta.setValueBefore(savedIndividual.getEmail());
            ta.setValueAfter(vo.getEmail());
            taService.add(ta);
            savedIndividual.setEmail(vo.getEmail());

            UpdateVAVO updateVAVO=new UpdateVAVO();
            updateVAVO.setClient_id(initDB.get("VA.ClientID"));
            updateVAVO.setCustomer_email(savedIndividual.getEmail());
            updateVAVO.setCustomer_name(savedIndividual.getName());
            updateVAVO.setCustomer_phone(savedIndividual.getMsisdn());
            updateVAVO.setDatetime_expired(initDB.get("VA.EndDate")+"T00:00:00+07:00");
            updateVAVO.setDescription("Change email to "+savedIndividual.getEmail());
            updateVAVO.setTrx_amount("0");
            updateVAVO.setTrx_id(individualVO.getVirtualAccount().getTrxId());
            updateVAVO.setType("updateBilling");
            vaService.updateVAInfo(updateVAVO);
        }
        if(!vo.getGender().equals(savedIndividual.getGender())){
            ta.setField("Gender");
            ta.setValueBefore(savedIndividual.getGender());
            ta.setValueAfter(vo.getGender());
            taService.add(ta);
            savedIndividual.setGender(vo.getGender());
        }
        if(!vo.getId_no().equals(savedIndividual.getId_no())){
            ta.setField("ID No");
            ta.setValueBefore(savedIndividual.getId_no());
            ta.setValueAfter(vo.getId_no());
            taService.add(ta);
            savedIndividual.setId_no(vo.getId_no());
        }
        if(!vo.getId_photo().equals(savedIndividual.getId_photo())){
            ta.setField("ID Photo");
            ta.setValueBefore(savedIndividual.getId_photo());
            ta.setValueAfter(vo.getId_photo());
            taService.add(ta);
            savedIndividual.setId_photo(vo.getId_photo());
        }
        if(!vo.getId_type().equals(savedIndividual.getId_type())){
            ta.setField("ID Type");
            ta.setValueBefore(savedIndividual.getId_type());
            ta.setValueAfter(vo.getId_type());
            taService.add(ta);
            savedIndividual.setId_type(vo.getId_type());
        }
        if(!vo.getMsisdn().equals(savedIndividual.getMsisdn())){
            ta.setField("MSISDN");
            ta.setValueBefore(savedIndividual.getMsisdn());
            ta.setValueAfter(vo.getMsisdn());
            taService.add(ta);
            savedIndividual.setMsisdn(vo.getMsisdn());
        }
        if(!vo.getName().equals(savedIndividual.getName())){
            ta.setField("Name");
            ta.setValueBefore(savedIndividual.getName());
            ta.setValueAfter(vo.getName());
            taService.add(ta);
            savedIndividual.setName(vo.getName());
        }
        if(!vo.getPhoto().equals(savedIndividual.getPhoto())){
            ta.setField("Photo");
            ta.setValueBefore(savedIndividual.getPhoto());
            ta.setValueAfter(vo.getPhoto());
            taService.add(ta);
            savedIndividual.setPhoto(vo.getPhoto());
        }
        if(!vo.getProvince().equals(savedIndividual.getProvince())){
            ta.setField("Province");
            ta.setValueBefore(savedIndividual.getProvince());
            ta.setValueAfter(vo.getProvince());
            taService.add(ta);
            savedIndividual.setProvince(vo.getProvince());
        }
        if(!vo.getZip_code().equals(savedIndividual.getZip_code())){
            ta.setField("ZIP Code");
            ta.setValueBefore(savedIndividual.getZip_code());
            ta.setValueAfter(vo.getZip_code());
            taService.add(ta);
            savedIndividual.setZip_code(vo.getZip_code());
        }
        if(!vo.getTier().equals(savedIndividual.getTier())){
            ta.setField("Tier");
            ta.setValueBefore(savedIndividual.getTier());
            ta.setValueAfter(vo.getTier());
            taService.add(ta);
            savedIndividual.setTier(vo.getTier());
        }

        VirtualAccount savedVA=new VirtualAccount();
        VirtualAccount oldVA = vaRepository.findByPartnerID(savedIndividual.getId());
        if(oldVA != null && !oldVA.getMsisdn().equals(savedIndividual.getMsisdn())){
            savedVA=vaService.addIndividual(savedIndividual);
            oldVA.setStatus("INACTIVE");
            ta.setField("Virtual Account");
            ta.setValueBefore(oldVA.getVa());
            ta.setValueAfter(savedVA.getVa());
            vaService.update(oldVA);
            taService.add(ta);
            savedIndividual.setVa(savedVA.getVa());
        }

        iRepo.save(savedIndividual);

        IndividualRegistrationVO savedVO= new IndividualRegistrationVO();
        savedVO.setVirtualAccount(savedVA);
        savedVO.setIndividual(savedIndividual);
        savedVO.setTransferLimits(tlRepo.findByTier(savedIndividual.getTier()));

        return savedVO;
    }

    public IndividualRegistrationVO updateTier(IndividualRegistrationVO individualVO) {
        Individual vo = individualVO.getIndividual();
        Individual savedIndividual = iRepo.findIndividual(vo.getId());
        if (savedIndividual == null) {
            throw new NostraException("Subscriber not found", StatusCode.DATA_NOT_FOUND);
        }

        TrailAudit ta = new TrailAudit();
        ta.setOwnerID(savedIndividual.getId().toString());
        ta.setUser("System");
        if(!vo.getTier().equals(savedIndividual.getTier())){
            ta.setField("Tier");
            ta.setValueBefore(savedIndividual.getTier());
            ta.setValueAfter(vo.getTier());
            taService.add(ta);
            savedIndividual.setTier(vo.getTier());
        }
        if(!vo.getEmail().equals(savedIndividual.getEmail())){
            ta.setField("Email");
            ta.setValueBefore(savedIndividual.getEmail());
            ta.setValueAfter(vo.getEmail());
            taService.add(ta);
            savedIndividual.setEmail(vo.getEmail());
        }
        iRepo.save(savedIndividual);
        return individualVO;
    }

    public IndividualRegistrationVO getIndividualDetail(int id){
        Individual individual=iRepo.findIndividual(id);
        if(individual==null){
            throw new NostraException("Subscriber not found",StatusCode.DATA_NOT_FOUND);
        }
        List<TransferLimit> tls=tlRepo.findByTier(individual.getTier());
        VirtualAccount va=vaRepository.findByPartnerID(individual.getId());
        IndividualRegistrationVO vo=new IndividualRegistrationVO();
        vo.setIndividual(individual);
        vo.setTransferLimits(tls);
        vo.setVirtualAccount(va);
        return vo;
    }
}

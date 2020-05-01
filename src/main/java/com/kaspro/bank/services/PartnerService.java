package com.kaspro.bank.services;

import com.kaspro.bank.converter.PartnerVOConverter;
import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.persistance.repository.PartnerRepository;
import com.kaspro.bank.vo.PartnerRequestVO;
import com.kaspro.bank.vo.PartnerResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PartnerService {
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    PartnerVOConverter partnerVOConverter;

    public List<PartnerResponseVO> findAll(){
        List<Partner> partners = partnerRepository.findAll();
        log.info("partner"+partners.size());
        List<PartnerResponseVO> result = new ArrayList<>();
        partnerVOConverter.transferListOfModelToListOfVO(partners, result);
        return result;
    }

    public String add(PartnerRequestVO vo){
        Partner partner = partnerVOConverter.transferVOToModel(vo,null);
        Partner saved = partnerRepository.save(partner);
        return saved.getSecureId();
    }
}

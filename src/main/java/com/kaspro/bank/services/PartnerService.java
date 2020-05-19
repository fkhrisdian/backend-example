package com.kaspro.bank.services;

import com.kaspro.bank.converter.PartnerVOConverter;
import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.persistance.domain.TransferFee;
import com.kaspro.bank.persistance.domain.TransferLimit;
import com.kaspro.bank.persistance.repository.PartnerRepository;
import com.kaspro.bank.persistance.repository.TransferFeeRepository;
import com.kaspro.bank.persistance.repository.TransferLimitRepository;
import com.kaspro.bank.vo.PartnerRequestVO;
import com.kaspro.bank.vo.PartnerResponseVO;
import com.kaspro.bank.vo.TransferFeeVO;
import com.kaspro.bank.vo.TransferLimitVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class PartnerService {
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    TransferLimitRepository tlRepository;

    @Autowired
    TransferFeeRepository tfRepository;

    @Autowired
    PartnerVOConverter partnerVOConverter;

    Logger logger = LoggerFactory.getLogger(PartnerService.class);

    public List<Partner> findAll(){
        List<Partner> partners = partnerRepository.findAll();
        return partners;
    }

    @Transactional
    public PartnerRequestVO findDetail(int id){
        PartnerRequestVO result = new PartnerRequestVO();

        Partner partner=partnerRepository.findPartner(id);
        result.setAddress(partner.getAddress());
        result.setEmail(partner.getEmail());
        result.setName(partner.getName());
        result.setNibSiupTdp(partner.getNibSipTdp());
        result.setNoAktaPendirian(partner.getNoAktaPendirian());
        result.setNpwp(partner.getNpwp());
        result.setStatus(partner.getStatus());
        result.setId(partner.getId().toString());
        result.setVersion(partner.getVersion());

        List<TransferFee> tfs = tfRepository.findByPartnerID(id);
        TransferFeeVO tfVO = new TransferFeeVO();
        for(TransferFee tf:tfs){
            if (tf.getDestination().equals("KASPROBANK")){
                tfVO.setKasproBank(tf.getFee());
            }else if (tf.getDestination().equals("KASPRO")){
                tfVO.setKaspro(tf.getFee());
            }else if (tf.getDestination().equals("BNI")){
                tfVO.setBni(tf.getFee());
            }else if (tf.getDestination().equals("OTHERBANK")){
                tfVO.setOtherBank(tf.getFee());
            }else if (tf.getDestination().equals("EMONEY")){
                tfVO.setEmoney(tf.getFee());
            }
        }

        List<String> tiers = tlRepository.findTiersByPartnerID(id);
        List<TransferLimitVO> tlVOs = new ArrayList<>();
        for(String tier:tiers){
            List<TransferLimit> tls = tlRepository.findByPartnerIDAndTier(id,tier);
            TransferLimitVO tlVO = new TransferLimitVO();
            tlVO.setType(tier);
            for(TransferLimit tl:tls){
                if (tl.getDestination().equals("KASPROBANK")){
                    tlVO.setKasproBank(tl.getTransactionLimit());
                }else if (tl.getDestination().equals("KASPRO")){
                    tlVO.setKaspro(tl.getTransactionLimit());
                }else if (tl.getDestination().equals("BNI")){
                    tlVO.setBni(tl.getTransactionLimit());
                }else if (tl.getDestination().equals("OTHERBANK")){
                    tlVO.setOtherBank(tl.getTransactionLimit());
                }else if (tl.getDestination().equals("EMONEY")){
                    tlVO.setEmoney(tl.getTransactionLimit());
                }
            }
            tlVOs.add(tlVO);
        }
        result.setTransferLimits(tlVOs);

        result.setTransferFee(tfVO);

        return result;
    }

    @Transactional
    public PartnerResponseVO add(PartnerRequestVO vo){
        //Partner insertion
        logger.info("Starting insert Partner");
        Partner partner = new Partner();
        partner.setName(vo.getName());
        partner.setAddress(vo.getAddress());
        partner.setEmail(vo.getEmail());
        partner.setNibSipTdp(vo.getNibSiupTdp());
        partner.setNoAktaPendirian(vo.getNoAktaPendirian());
        partner.setNpwp(vo.getNpwp());
        partner.setStatus("ACTIVE");
        logger.info("Inserting partner: "+partner.getName());
        Partner savedPartner=partnerRepository.save(partner);
        logger.info("Finished insert Partner");

        //TransferLimit insertion
        logger.info("Starting insert Transfer Limit");
        List<TransferLimitVO>tls=vo.getTransferLimits();
        for(TransferLimitVO tl : tls){
            TransferLimit transferLimit =new TransferLimit();
            transferLimit.setTierType(tl.getType());
            transferLimit.setPartner(savedPartner);

            //Save TransferLimit for KasproBank to Kaspro
            transferLimit.setDestination("KASPRO");
            transferLimit.setTransactionLimit(tl.getKaspro());
            logger.info("Inserting Transfer Limit: "+transferLimit.getDestination()+" Tier "+transferLimit.getTierType());
            tlRepository.save(transferLimit);

            //Save TransferLimit for KasproBank to KasproBank
            transferLimit.setDestination("KASPROBANK");
            transferLimit.setTransactionLimit(tl.getKasproBank());
            logger.info("Inserting Transfer Limit: "+transferLimit.getDestination()+" Tier "+transferLimit.getTierType());
            tlRepository.save(transferLimit);

            //Save TransferLimit for KasproBank to BNI
            transferLimit.setDestination("BNI");
            transferLimit.setTransactionLimit(tl.getBni());
            logger.info("Inserting Transfer Limit: "+transferLimit.getDestination()+" Tier "+transferLimit.getTierType());
            tlRepository.save(transferLimit);

            //Save TransferLimit for KasproBank to OTHERBANK
            transferLimit.setDestination("OTHERBANK");
            transferLimit.setTransactionLimit(tl.getOtherBank());
            logger.info("Inserting Transfer Limit: "+transferLimit.getDestination()+" Tier "+transferLimit.getTierType());
            tlRepository.save(transferLimit);

            //Save TransferLimit for KasproBank to eMoney
            transferLimit.setDestination("EMONEY");
            transferLimit.setTransactionLimit(tl.getEmoney());
            logger.info("Inserting Transfer Limit: "+transferLimit.getDestination()+" Tier "+transferLimit.getTierType());
            tlRepository.save(transferLimit);
        }
        logger.info("Finished insert TransferLimit");

        //TransferFee Insertion

        TransferFeeVO tf=vo.getTransferFee();
        TransferFee transferFee = new TransferFee();
        transferFee.setPartner(savedPartner);

        logger.info("Starting insert TransferFee");
        //Save TransferFee for KasproBank to KasproBank
        transferFee.setDestination("KASPROBANK");
        transferFee.setFee(tf.getKasproBank());
        logger.info("Inserting Transfer Fee "+transferFee.getDestination()+" with Value "+transferFee.getFee());
        tfRepository.save(transferFee);

        //Save TransferFee for KasproBank to Kaspro
        transferFee.setDestination("KASPRO");
        transferFee.setFee(tf.getKaspro());
        logger.info("Inserting Transfer Fee "+transferFee.getDestination()+" with Value "+transferFee.getFee());
        tfRepository.save(transferFee);

        //Save TransferFee for KasproBank to BNI
        transferFee.setDestination("BNI");
        transferFee.setFee(tf.getBni());
        logger.info("Inserting Transfer Fee "+transferFee.getDestination()+" with Value "+transferFee.getFee());
        tfRepository.save(transferFee);

        //Save TransferFee for KasproBank to OtherBank
        transferFee.setDestination("OTHERBANK");
        transferFee.setFee(tf.getOtherBank());
        logger.info("Inserting Transfer Fee "+transferFee.getDestination()+" with Value "+transferFee.getFee());
        tfRepository.save(transferFee);

        //Save TransferFee for KasproBank to eMoney
        transferFee.setDestination("EMONEY");
        transferFee.setFee(tf.getEmoney());
        logger.info("Inserting Transfer Fee "+transferFee.getDestination()+" with Value "+transferFee.getFee());
        tfRepository.save(transferFee);
        logger.info("Finished insert TransferFee");

        //Compose response
        PartnerResponseVO result=new PartnerResponseVO();
        result.setId(savedPartner.getSecureId());
        result.setServiceName(vo.getServiceName());
        result.setTransID(vo.getTransID());
        result.setStatus(0);
        result.setVersion(savedPartner.getVersion());
        logger.info("Success inserting partner with ID : "+result.getId());

        return result;
    }
}

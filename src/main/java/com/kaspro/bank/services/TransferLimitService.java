package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.TransferLimit;
import com.kaspro.bank.persistance.repository.TransferLimitRepository;
import com.kaspro.bank.vo.KeyValuePairedVO;
import com.kaspro.bank.vo.TransferLimitVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TransferLimitService {

    @Autowired
    TransferLimitRepository transferLimitRepository;

    Logger logger = LoggerFactory.getLogger(TransferLimit.class);

    @Transactional
    public TransferLimitVO add(TransferLimitVO transferLimitVO){

        String tier=transferLimitVO.getType();
        List<TransferLimit> transferLimitList=transferLimitRepository.findByTier(tier);

        if(transferLimitList.size()>0){
            throw new NostraException("Tier Already Exist", StatusCode.DATA_INTEGRITY);
        }

        List<KeyValuePairedVO> attributes=transferLimitVO.getAttributes();
        for(KeyValuePairedVO attribute:attributes){
            TransferLimit transferLimit=new TransferLimit();
            transferLimit.setTierType(tier);
            transferLimit.setDestination(attribute.getKey());
            transferLimit.setTransactionLimit(attribute.getValue());
            transferLimitRepository.save(transferLimit);
        }

        return transferLimitVO;
    }

    @Transactional
    public TransferLimitVO update(TransferLimitVO transferLimitVO){

        String tier=transferLimitVO.getType();
        List<TransferLimit> transferLimitList=transferLimitRepository.findByTier(tier);

        if(transferLimitList.size()==0){
            throw new NostraException("Tier not found", StatusCode.DATA_NOT_FOUND);
        }

        try {
            logger.info("Tier : " + tier);
            List<KeyValuePairedVO> attributes = transferLimitVO.getAttributes();
            for (KeyValuePairedVO attribute : attributes) {
                logger.info("Destination : " + attribute.getKey());
                logger.info("Transaction Limit : " + attribute.getValue());
                TransferLimit updatedTransferLimit = transferLimitRepository.findByTierAndDest(tier, attribute.getKey());
                updatedTransferLimit.setTransactionLimit(attribute.getValue());
                logger.info("Updated Transaction Limit : " + updatedTransferLimit.getId());
                transferLimitRepository.save(updatedTransferLimit);
            }
        }catch (NostraException e){
            throw new NostraException("Tier update is failed", StatusCode.ERROR);
        }

        return transferLimitVO;
    }

    @Transactional
    public List<TransferLimit> findByTier(String tier){

        List<TransferLimit> transferLimits=transferLimitRepository.findByTier(tier);

        return transferLimits;
    }

    @Transactional
    public List<TransferLimit> findAll(){

        List<TransferLimit> transferLimits=transferLimitRepository.findAll();

        return transferLimits;
    }

    public List<String> findTiers(){
        List<String> result=transferLimitRepository.findTiers();
        return result;
    }

}

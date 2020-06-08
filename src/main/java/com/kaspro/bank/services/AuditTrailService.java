package com.kaspro.bank.services;

import com.kaspro.bank.persistance.domain.AuditTrail;
import com.kaspro.bank.persistance.repository.AuditTrailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Service
public class AuditTrailService {

    @Autowired
    AuditTrailRepository auditTrailRepository;

    @Transactional
    public AuditTrail add(Date partitionKey, int trxId, String serviceName, String valueBefore, String valueAfter, String userApp, Date startDtm, Date endDtm){
        String state = "Error";
        AuditTrail auditTrail = new AuditTrail();

        auditTrail.setPartitionKey(partitionKey);
        auditTrail.setTrxId(trxId);
        auditTrail.setServiceName(serviceName);
        auditTrail.setValueBefore(valueBefore);
        auditTrail.setValueAfter(valueAfter);
        auditTrail.setUserApp(userApp);
        auditTrail.setEndDtm(endDtm);
        auditTrail.setEndDtm(endDtm);

        try {
            auditTrailRepository.save(auditTrail);
        }catch (Exception e){
            state = e.toString();
        }

        return auditTrail;
    }



}

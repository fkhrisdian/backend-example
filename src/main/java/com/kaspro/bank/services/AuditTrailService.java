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
    public AuditTrail add(AuditTrail auditTrail){
        AuditTrail savedAuditTrail = auditTrailRepository.save(auditTrail);
        return savedAuditTrail;
    }

}

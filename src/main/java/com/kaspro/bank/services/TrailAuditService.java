package com.kaspro.bank.services;

import com.kaspro.bank.persistance.domain.TrailAudit;
import com.kaspro.bank.persistance.repository.TrailAuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TrailAuditService {

    @Autowired
    TrailAuditRepository taRepository;

    public TrailAudit add(TrailAudit ta){
        TrailAudit savedTA = taRepository.save(ta);
        return savedTA;
    }

    public List<TrailAudit> findByOwnerId(String id){
        List<TrailAudit> listTA=taRepository.findByOwnerID(id);
        return listTA;
    }
}

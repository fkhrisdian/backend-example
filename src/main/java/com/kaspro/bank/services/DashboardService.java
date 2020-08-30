package com.kaspro.bank.services;

import com.kaspro.bank.persistance.repository.PartnerMemberRepository;
import com.kaspro.bank.persistance.repository.RequestCardRepository;
import com.kaspro.bank.persistance.repository.TransactionHistoryRepository;
import com.kaspro.bank.vo.DashboardResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DashboardService {

    @Autowired
    PartnerMemberRepository pmRepo;

    @Autowired
    TransactionHistoryRepository thRepo;

    @Autowired
    RequestCardRepository rcRepo;

    @Autowired
    GlobalHashmapService ghService;

    public DashboardResVO getDashboard(){
        DashboardResVO result = new DashboardResVO();
        Object [] objects=null;
        objects=ghService.getHashMap("getDashboard");

        if(objects!=null){
            log.info("Dashboard Hashmap Exist");
            long expired=(long)objects[8];
            long current=ghService.getTimeNow();

            if(expired>=current){
                log.info("Returning Existing Dashboard Hashmap");
                result.setTotalCashout(objects[0].toString());
                result.setTotalCashout7(objects[1].toString());
                result.setTotalCustomer(objects[2].toString());
                result.setTotalCustomer7(objects[3].toString());
                result.setTotalRequestDebitCard(objects[4].toString());
                result.setTotalRequestDebitCard7(objects[5].toString());
                result.setTotalTransaction(objects[6].toString());
                result.setTotalTransaction7(objects[7].toString());
                log.info(result.toString());
                return result;
            }else {
                log.info("Dashboard Hashmap Already Expired, Updating Dashboard Hashmap");
                result.setTotalCashout("0");
                result.setTotalCashout7("0");
                result.setTotalCustomer(pmRepo.findByLastDays("1"));
                result.setTotalCustomer7(pmRepo.findByLastDays("7"));
                result.setTotalRequestDebitCard(rcRepo.findByLastDays("1"));
                result.setTotalRequestDebitCard7(rcRepo.findByLastDays("7"));
                result.setTotalTransaction(thRepo.findByLastDays("1"));
                result.setTotalTransaction7(thRepo.findByLastDays("7"));
                log.info(result.toString());
                ghService.updateDashboardHashmap("getDashboard",result);
                return getDashboard();
            }
        } else {
            log.info("Initiation Dashboard Hashmap");
            putDashboard();
            return getDashboard();
        }
    }

    public void putDashboard(){
        DashboardResVO result = new DashboardResVO();

        result.setTotalCashout("0");
        result.setTotalCashout7("0");
        result.setTotalCustomer(pmRepo.findByLastDays("1"));
        result.setTotalCustomer7(pmRepo.findByLastDays("7"));
        result.setTotalRequestDebitCard(rcRepo.findByLastDays("1"));
        result.setTotalRequestDebitCard7(rcRepo.findByLastDays("7"));
        result.setTotalTransaction(thRepo.findByLastDays("1"));
        result.setTotalTransaction7(thRepo.findByLastDays("7"));
        log.info(result.toString());

        ghService.setDashboardHashmap("getDashboard",result);

    }
}

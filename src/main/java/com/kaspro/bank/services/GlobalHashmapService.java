package com.kaspro.bank.services;

import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.vo.DashboardResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;

public class GlobalHashmapService {

    private final HashMap<String, Object[]> globalMap;

    public GlobalHashmapService() {
        globalMap = new HashMap<>();
    }

    public void setHashMap(String key, Object[] obj) {
        globalMap.put(key, obj);
    }

    public void updaHashMap(String key, Object[] obj) {
        globalMap.replace(key, obj);
    }

    public void removeHashMap(String key) {
        globalMap.remove(key);
    }

    public Object[] getHashMap(String key) {
        return globalMap.get(key);
    }

    public boolean containsKey(String key) {
        return globalMap.containsKey(key);
    }

    public long getExpiredTime(int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTimeInMillis();
    }

    public long getTimeNow() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public void setDashboardHashmap(String id, DashboardResVO t) {
        InitDB initDB=InitDB.getInstance();
        Object[] objMap = new Object[9];
        objMap[0] = t.getTotalCashout();
        objMap[1] = t.getTotalCashout7();
        objMap[2] = t.getTotalCustomer();
        objMap[3] = t.getTotalCustomer7();
        objMap[4] = t.getTotalRequestDebitCard();
        objMap[5] = t.getTotalRequestDebitCard7();
        objMap[6] = t.getTotalTransaction();
        objMap[7] = t.getTotalTransaction7();
        objMap[8] = getExpiredTime(Integer.parseInt(initDB.get("Refresh.Dashboard")));
        setHashMap(id, objMap);
    }

    public void updateDashboardHashmap(String id, DashboardResVO t) {
        InitDB initDB=InitDB.getInstance();
        Object[] objMap = new Object[9];
        objMap[0] = t.getTotalCashout();
        objMap[1] = t.getTotalCashout7();
        objMap[2] = t.getTotalCustomer();
        objMap[3] = t.getTotalCustomer7();
        objMap[4] = t.getTotalRequestDebitCard();
        objMap[5] = t.getTotalRequestDebitCard7();
        objMap[6] = t.getTotalTransaction();
        objMap[7] = t.getTotalTransaction7();
        objMap[8] = getExpiredTime(Integer.parseInt(initDB.get("Refresh.Dashboard")));
        updaHashMap(id, objMap);
    }
}

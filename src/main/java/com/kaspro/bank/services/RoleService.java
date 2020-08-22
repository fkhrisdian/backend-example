package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.BlacklistMsisdn;
import com.kaspro.bank.persistance.domain.Role;
import com.kaspro.bank.persistance.domain.User;
import com.kaspro.bank.persistance.repository.BlacklistMsisdnRepository;
import com.kaspro.bank.persistance.repository.RoleRepository;
import com.kaspro.bank.persistance.repository.UserRepository;
import com.kaspro.bank.vo.RoleReqVO;
import com.kaspro.bank.vo.RoleResVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class RoleService {

    @Autowired
    RoleRepository rRepo;

    @Autowired
    UserRepository uRepo;

    Logger logger = LoggerFactory.getLogger(Role.class);

    public Role addRole(RoleReqVO vo){
        Role role = new Role();
        role.setName(vo.getName());
        String pages="";
        for(String page:vo.getPages()){
            pages=pages+page+"|";
        }
        role.setPages(pages);
        Role savedRole=rRepo.save(role);
        return savedRole;
    }

    public Role updateRole(RoleResVO vo){
        Role role = rRepo.findByRoleId(Integer.toString(vo.getId()));
        if(role==null){
            throw new NostraException("Role is not found", StatusCode.DATA_NOT_FOUND);
        }
        role.setName(vo.getName());
        String pages="";
        for(String page:vo.getPages()){
            pages=pages+page+"|";
        }
        role.setPages(pages);
        Role savedRole=rRepo.save(role);
        return savedRole;
    }

    public List<RoleResVO> getAllRole(){
        List<Role>roles=rRepo.findAll();
        List<RoleResVO> result= new ArrayList<>();
        for(Role role:roles) {
            String[] pages = role.getPages().split("\\|");
            RoleResVO vo = new RoleResVO();
            vo.setId(role.getId());
            vo.setName(role.getName());
            vo.setPages(pages);
            result.add(vo);
        }
        return result;
    }

    public RoleResVO getDetailRole(String id){
        Role role=rRepo.findByRoleId(id);
        if(role==null){
            throw new NostraException("Role is not found", StatusCode.DATA_NOT_FOUND);
        }
        RoleResVO vo= new RoleResVO();
        String[] pages = role.getPages().split("\\|");
        vo.setId(role.getId());
        vo.setName(role.getName());
        vo.setPages(pages);
        return vo;
    }

    public String deleteRole(String id){
        List<User> users= uRepo.findByRoleId(id);
        if(users.size()>0){
            throw new NostraException("Role is used by User(s). Please make sure role is not used by any User", StatusCode.ERROR);
        }else {
            Role role = rRepo.findByRoleId(id);
            if(role==null){
                throw new NostraException("Role Not Found",StatusCode.DATA_NOT_FOUND);
            }else {
                rRepo.delete(role);
                return "Role "+role.getName()+" is deleted.";
            }
        }
    }


}

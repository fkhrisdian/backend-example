package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.User;
import com.kaspro.bank.persistance.repository.UserRepository;
import com.kaspro.bank.vo.LoginReqVO;
import com.kaspro.bank.vo.UserReqVO;
import com.kaspro.bank.vo.UserResVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserService service;

    Object target;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    void sendEmail(String to, String subject, String text) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);

        msg.setSubject(subject);
        msg.setText(text);

        javaMailSender.send(msg);

    }

    public UserResVO add(UserReqVO vo){
        User existing=repository.findByUsername(vo.getUsername());
        if(existing!=null){
            throw new NostraException("Username already used. Please user another username",StatusCode.ERROR);
        }
        User user = new User();
        user.setUsername(vo.getUsername());
        user.setEmail(vo.getEmail());
        String roles="";
        for(String role:vo.getRoles()){
            roles=roles+role+("|");
        }
        user.setRoles(roles);
        PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(true)
                .useLower(true)
                .useUpper(true)
                .build();
        String password = passwordGenerator.generate(8);
        logger.info("Generated Password : "+password);
        String encodedString = Base64.getEncoder().encodeToString(password.getBytes());
        user.setPassword(encodedString);
        User savedUser=repository.save(user);

//        service.sendEmail(savedUser.getEmail(),"KasproBank Generated Password","Your generated password is "+password);

        UserResVO result=new UserResVO();
        result.setId(savedUser.getId().toString());
        result.setUsername(savedUser.getUsername());
        result.setEmail(savedUser.getEmail());
        result.setRoles(vo.getRoles());

        return result;
    }

    public UserResVO update(UserResVO vo){
        User user = repository.findByUserId(vo.getId());
        if(user==null){
            throw new NostraException("User not found", StatusCode.DATA_NOT_FOUND);
        }
        user.setUsername(vo.getUsername());
        user.setEmail(vo.getEmail());
        String roles="";
        for(String role:vo.getRoles()){
            roles=roles+role+("|");
        }
        user.setRoles(roles);
        User savedUser=repository.save(user);

        UserResVO result=new UserResVO();
        result.setId(savedUser.getId().toString());
        result.setUsername(savedUser.getUsername());
        result.setEmail(savedUser.getEmail());
        result.setRoles(vo.getRoles());

        return result;
    }

    public List<UserResVO> getUser(){
        List<User> users=repository.findAll();
        List<UserResVO> result=new ArrayList<>();

        for(User user:users){
            UserResVO vo = new UserResVO();
            vo.setId(user.getId().toString());
            vo.setUsername(user.getUsername());
            vo.setEmail(user.getEmail());
            String[] roles = user.getRoles().split("\\|");
            vo.setRoles(roles);
            result.add(vo);
        }
        return result;
    }

    public UserResVO getUserDetail(String id){
        User user=repository.findByUserId(id);
        if(user==null){
            throw new NostraException("User not found", StatusCode.DATA_NOT_FOUND);
        }
        UserResVO result=new UserResVO();

        result.setId(user.getId().toString());
        result.setUsername(user.getUsername());
        result.setEmail(user.getEmail());
        String[] roles = user.getRoles().split("\\|");
        result.setRoles(roles);

        return result;
    }

    public boolean validateUser(LoginReqVO vo){
        User user=repository.findByEmail(vo.getEmail());
        if(user==null){
            throw new NostraException("Invalid Email",StatusCode.ERROR);
        }else{
            byte[] decodedBytes = Base64.getDecoder().decode(user.getPassword());
            String password = new String(decodedBytes);
            if(!password.equals(vo.getPassword())){
                throw new NostraException("Invalid Password",StatusCode.ERROR);
            }else {
                return true;
            }
        }
    }

//    @Async
//    public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws Exception {
//        long start = System.currentTimeMillis();
//        List<User> users = parseCSVFile(file);
//        logger.info("saving list of users of size {}", users.size(), "" + Thread.currentThread().getName());
//        users = repository.saveAll(users);
//        long end = System.currentTimeMillis();
//        logger.info("Total time {}", (end - start),"ms");
//        return CompletableFuture.completedFuture(users);
//    }
//    @Async
//    public CompletableFuture<List<User>> findAllUsers(){
//        logger.info("get list of user by "+Thread.currentThread().getName());
//        List<User> users=repository.findAll();
//        return CompletableFuture.completedFuture(users);
//    }
//
//    private List<User> parseCSVFile(final MultipartFile file) throws Exception {
//        final List<User> users = new ArrayList<>();
//        try {
//            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//                String line;
//                while ((line = br.readLine()) != null) {
//                    final String[] data = line.split(",");
//                    final User user = new User();
//                    user.setName(data[0]);
//                    user.setEmail(data[1]);
//                    user.setGender(data[2]);
//                    users.add(user);
//                }
//                return users;
//            }
//        } catch (final IOException e) {
//            logger.error("Failed to parse CSV file {}", e);
//            throw new Exception("Failed to parse CSV file {}", e);
//        }
//    }

}

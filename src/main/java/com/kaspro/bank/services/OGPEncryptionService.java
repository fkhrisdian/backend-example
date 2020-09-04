package com.kaspro.bank.services;

import com.kaspro.bank.controller.FileConfigController;
import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.FileConfig;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.util.InitFileDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.sql.Blob;

@Slf4j
@Service
public class OGPEncryptionService {


  @Autowired
  FileConfigService fileConfigService;

  @Autowired
  FileConfigController fileConfigController;


  @Transactional
  public String encrypt(String data) {
    try {
      InitDB initDB=InitDB.getInstance();
      InitFileDB initFileDB=InitFileDB.getInstance();

      String ogpSignAlias = initDB.get("ogp.signature.alias");
      String ogpSignPassword=initDB.get("ogp.signature.password");
      Blob key= initFileDB.get("OGP.Certificate");

//      Object [] objects=null;
//      objects = ghService.getHashMap("OGP.Certificate");

      if(key==null){
        throw new NostraException("Certificate not found. Please upload Certificate first.");
      }else {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(key.getBinaryStream(), ogpSignPassword.toCharArray());
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(ogpSignAlias, ogpSignPassword.toCharArray());

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());

        return (new BASE64Encoder()).encode(signature.sign()).replace("\n", "").replace("\r", "");
      }

    } catch (Exception e) {
      log.warn("Got Exception " + e);
      throw new NostraException(e.getMessage(), StatusCode.ERROR);
    }
  }

  public String uploadCertificate(final MultipartFile file) throws Exception{
    InitFileDB initFileDB=InitFileDB.getInstance();
    Blob certificate=initFileDB.get("OGP.Certificate");

    if(certificate!=null){
      log.info("Update Certificate");
      Blob certi = new SerialBlob(convertFileContentToBlob(file));
      FileConfig fileConfig=fileConfigService.getByName("OGP.Certificate");
      fileConfig.setParam_value(certi);
      fileConfigService.update(fileConfig);
      fileConfigController.reLoad();
    } else {
      log.info("Initiation Certificate");
      Blob certi = new SerialBlob(convertFileContentToBlob(file));
      FileConfig fileConfig = new FileConfig();
      fileConfig.setParam_value(certi);
      fileConfig.setParam_name("OGP.Certificate");
      fileConfigService.add(fileConfig);
      fileConfigController.reLoad();
    }
    return "Success";
  }

  public static byte[] convertFileContentToBlob(MultipartFile file) throws IOException {
    // create file object
    // initialize a byte array of size of the file
    byte[] fileContent = new byte[(int) file.getSize()];
    FileInputStream inputStream = null;
    try {
      // create an input stream pointing to the file
      // read the contents of file into byte array
      file.getInputStream().read(fileContent);
    } catch (IOException e) {
      throw new IOException("Unable to convert file to byte array. " +
              e.getMessage());
    } finally {
      // close input stream
      if (inputStream != null) {
        inputStream.close();
      }
    }
    return fileContent;
  }
}

package com.kaspro.bank.services;

import com.kaspro.bank.util.InitDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

@Slf4j
@Service
public class OGPEncryptionService {

  String encrypt(String data) {
    try {
      InitDB initDB=InitDB.getInstance();
      String ogpCertPath=initDB.get("ogp.certification.path");
      String ogpSignAlias = initDB.get("ogp.signature.alias");
      String ogpSignPassword=initDB.get("ogp.signature.password");

      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(new FileInputStream(ogpCertPath), ogpSignPassword.toCharArray());
      PrivateKey privateKey = (PrivateKey) keyStore.getKey(ogpSignAlias, ogpSignPassword.toCharArray());

      Signature signature = Signature.getInstance("SHA256withRSA");
      signature.initSign(privateKey);
      signature.update(data.getBytes());

      return (new BASE64Encoder()).encode(signature.sign()).replace("\n", "").replace("\r", "");
    } catch (Exception e) {
      log.warn("Got Exception " + e);
      return null;
    }
  }
}

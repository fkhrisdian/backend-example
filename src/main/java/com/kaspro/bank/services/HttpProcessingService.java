package com.kaspro.bank.services;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.util.InitFileDB;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

@Service
@Slf4j
public class HttpProcessingService {

    public String postUser(String url, String body) throws IOException {
        String responseBody="";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            StringEntity stringEntity = new StringEntity(body);
            httpPost.setEntity(stringEntity);

            HttpResponse response;

            System.out.println("Executing request " + httpPost.getRequestLine());

            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(entity !=null){
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        }catch (Exception e){
            throw new NostraException(e.getMessage(), StatusCode.ERROR);
        }
        return responseBody;
    }

    public String kasproValidate(String msisdn) throws IOException {
        String responseBody="";
        InitDB initDB=InitDB.getInstance();
        String url=initDB.get("Kaspro.Validate.URL")+msisdn+"/validate";
        String token=initDB.get("Kaspro.Token");
        String certPsswd=initDB.get("Kaspro.Certificate.Password");
        log.info(certPsswd);
        InitFileDB initFileDB=InitFileDB.getInstance();
        Blob key=initFileDB.get("Kaspro.Certificate");

        KeyStore clientStore = null;
        try {
            clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(key.getBinaryStream(), certPsswd.toCharArray());

            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.useProtocol("TLS");

            sslContextBuilder.loadKeyMaterial(clientStore, certPsswd.toCharArray());
            sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());

            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept-Language", "EN");
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", token);


            HttpResponse response;

            System.out.println("Executing request " + httpGet.getRequestLine());

            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if(entity !=null){
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        }catch (Exception e){
            e.printStackTrace();
            throw new NostraException(e.getMessage(), StatusCode.ERROR);
        }
        return responseBody;
    }

    public String kasproPayu(String account) throws IOException {
        String responseBody="";
        InitDB initDB=InitDB.getInstance();
        String url=initDB.get("Kaspro.Payu.URL")+account+"/payu";
        String token=initDB.get("Kaspro.Token");
        String certPsswd=initDB.get("Kaspro.Certificate.Password");
        log.info(certPsswd);
        InitFileDB initFileDB=InitFileDB.getInstance();
        Blob key=initFileDB.get("Kaspro.Certificate");

        KeyStore clientStore = null;
        try {
            clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(key.getBinaryStream(), certPsswd.toCharArray());

            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.useProtocol("TLS");

            sslContextBuilder.loadKeyMaterial(clientStore, certPsswd.toCharArray());
            sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept-Language", "EN");
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", token);

            HttpResponse response;

            System.out.println("Executing request " + httpGet.getRequestLine());

            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if(entity !=null){
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        }catch (Exception e){
            throw new NostraException(e.getMessage(), StatusCode.ERROR);
        }
        return responseBody;
    }

    public String kasproCashIn(String body) throws IOException {
        String responseBody="";
        InitDB initDB=InitDB.getInstance();
        String url=initDB.get("Kaspro.CashIn.URL");
        String token=initDB.get("Kaspro.Token");
        String certPsswd=initDB.get("Kaspro.Certificate.Password");
        log.info(certPsswd);
        InitFileDB initFileDB=InitFileDB.getInstance();
        Blob key=initFileDB.get("Kaspro.Certificate");

        KeyStore clientStore = null;
        try {
            clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(key.getBinaryStream(), certPsswd.toCharArray());

            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.useProtocol("TLS");

            sslContextBuilder.loadKeyMaterial(clientStore, certPsswd.toCharArray());
            sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept-Language", "EN");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("token", token);
            StringEntity stringEntity = new StringEntity(body);
            httpPost.setEntity(stringEntity);

            HttpResponse response;

            System.out.println("Executing request " + httpPost.getRequestLine());

            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(entity !=null){
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        }catch (Exception e){
            throw new NostraException(e.getMessage(), StatusCode.ERROR);
        }
        return responseBody;
    }
}

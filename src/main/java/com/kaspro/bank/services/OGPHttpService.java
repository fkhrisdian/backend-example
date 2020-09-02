package com.kaspro.bank.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaspro.bank.util.InitDB;
import com.kaspro.bank.vo.ogp.OgpOauthTokenRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.ILoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class OGPHttpService {

  public String callHttpPost(String url, Object object) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String valueAsString = mapper.writeValueAsString(object);
      log.info("Request : "+valueAsString);
      return callHttpPost(url, MediaType.APPLICATION_JSON_VALUE, new StringEntity(valueAsString));
    } catch (JsonProcessingException e) {
      log.warn("Got Json Processing Exception " + e);
      return null;
    } catch (UnsupportedEncodingException e) {
      log.warn("Got Unsupported Encoding Exception " + e);
      return null;
    }
  }

  private String callHttpPost(String url, String mediaType, HttpEntity entity) {
    InitDB initDB=InitDB.getInstance();
    String ogpHostUrl=initDB.get("ogp.url.host");
    String ogpUsername=initDB.get("ogp.username");
    String ogpPassword=initDB.get("ogp.password");

    String header = "Basic " +
            Base64.getEncoder().encodeToString((ogpUsername + ":" + ogpPassword).getBytes());

    String completeUrl = ogpHostUrl +  url +
        (mediaType.equals(MediaType.APPLICATION_JSON_VALUE) ? "?access_token=" + getToken() : "");
    HttpPost httpPost = new HttpPost(completeUrl);
    httpPost.setHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
    httpPost.setHeader("Content-type", mediaType);
    if (mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
      httpPost.setHeader("Authorization", "Basic " +
          Base64.getEncoder().encodeToString((ogpUsername + ":" + ogpPassword).getBytes()));
    }
    httpPost.setEntity(entity);

    log.info("Calling {} with header{} and body {}", url, header, entity);
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      HttpEntity responseEntity = httpclient.execute(httpPost).getEntity();
      String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
      httpclient.close();
      log.info("Finish Calling with response {}", responseBody);
      return responseBody;
    } catch (IOException e) {
      log.warn("Got IO Exception " + e);
    }
    return null;
  }

  private String getToken() {
    InitDB initDB=InitDB.getInstance();
    String ogpTokenUrl=initDB.get("ogp.url.oauth.token");
    String responseBody = callHttpPost(ogpTokenUrl,
        MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        constructTokenFormEntity());
    log.info("Response : "+responseBody);

    Gson gsonSnakeCase = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    OgpOauthTokenRespVO oauthTokenRespVO = gsonSnakeCase.fromJson(responseBody, OgpOauthTokenRespVO.class);

    return oauthTokenRespVO.getAccessToken();
  }

  private UrlEncodedFormEntity constructTokenFormEntity() {
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("grant_type","client_credentials"));
    try {
      return new UrlEncodedFormEntity(params);
    } catch (UnsupportedEncodingException e) {
      log.warn("Got Unsupported Encoding Exception " + e);
      return null;
    }
  }
}

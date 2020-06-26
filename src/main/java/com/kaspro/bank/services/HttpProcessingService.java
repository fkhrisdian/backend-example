package com.kaspro.bank.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
        }
        return responseBody;
    }
}

package com.kaspro.bank.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

    public String kasproValidate(String msisdn) throws IOException {
        String responseBody="";
        String url="http://dev.kaspro.id/DCZ4DmJMPVKsX75/"+msisdn+"/validate";
        String token="WLu28cXFYvrdtQ7KFNxDUI3hpufmj+EbNknAEL9i7pfdjx69s/lnu3YSScaxUv+7Iere9Or5f1AvNC3rO8l+U3gkcU87vUrlHu6llGJeZiolpM2mD1ZePTlPyjVrArkmlK5Ui8vnGmu55anh2jq2Y4KD9HIj2FI8ENzfFqPX3/vmVH2e8ImkxsDuK1Ot+oH6BVxUKThhqcVPFfv3Qe52AA==";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
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
        }
        return responseBody;
    }

    public String kasproPayu(String account) throws IOException {
        String responseBody="";
        String url="http://dev.kaspro.id/DCZ4DmJMPVKsX75/"+account+"/payu";
        String token="WLu28cXFYvrdtQ7KFNxDUI3hpufmj+EbNknAEL9i7pfdjx69s/lnu3YSScaxUv+7Iere9Or5f1AvNC3rO8l+U3gkcU87vUrlHu6llGJeZiolpM2mD1ZePTlPyjVrArkmlK5Ui8vnGmu55anh2jq2Y4KD9HIj2FI8ENzfFqPX3/vmVH2e8ImkxsDuK1Ot+oH6BVxUKThhqcVPFfv3Qe52AA==";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
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
        }
        return responseBody;
    }

    public String kasproCashIn(String body) throws IOException {
        String responseBody="";
        String url="http://dev.kaspro.id/DCZ4DmJMPVKsX75/923733080012/kaspro/transfers";
        String token="WLu28cXFYvrdtQ7KFNxDUI3hpufmj+EbNknAEL9i7pfdjx69s/lnu3YSScaxUv+7Iere9Or5f1AvNC3rO8l+U3gkcU87vUrlHu6llGJeZiolpM2mD1ZePTlPyjVrArkmlK5Ui8vnGmu55anh2jq2Y4KD9HIj2FI8ENzfFqPX3/vmVH2e8ImkxsDuK1Ot+oH6BVxUKThhqcVPFfv3Qe52AA==";
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
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
        }
        return responseBody;
    }
}

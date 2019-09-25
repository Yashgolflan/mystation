/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.webservice;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
/**
 *
 * @author Omer
 */
public class HTTPRequest {
    private static final Logger log = LoggerFactory.getLogger(HTTPRequest.class);

    private String baseUrl;

    private Credentials usernamePasswordCredentials;

    public HTTPRequest(String baseURL){
        this.baseUrl = baseURL;
    }

    public void setUserNamePasswordCredentials(String user, String pass) {
        usernamePasswordCredentials = new UsernamePasswordCredentials(user, pass);
    }

    public InputStream getStream(String path, BasicNameValuePair... params) throws IOException {
        HttpClient client;
        if(usernamePasswordCredentials != null) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
            client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        }
        else {
            client = HttpClientBuilder.create().build();
        }

        HttpPost request = new HttpPost(baseUrl + path);
        request.setEntity(new UrlEncodedFormEntity(Arrays.asList(params)));

        HttpResponse response = client.execute(request);
        processServerError(response);

        return response.getEntity().getContent();
    }

    private void processServerError(HttpResponse response) {
        String message = null;
        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            try {
                message = EntityUtils.toString(response.getEntity());
            }
            catch (Exception ex) {
            }
            throw new RuntimeException("Failed: " + status + " - " + message);
        }
    }

}

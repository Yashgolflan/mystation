/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.tournament.impact.model;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * 
 * 
* We have our new leaderboard online, and with it comes the service to retrieve the data for a tournament.
       I have 3 tournaments published for my club ‘Scott Hrehirchuk’ here:

       http://impactts.ca/online/scotthrehirchuk/

       You can retrieve the tournament listing for a given club by accessing:
       http://impactts.ca/online/listing.php
       It is secured with http basic auth, where the username is the Impact licensed club name,
       And the password is that licensed club name concatenated with #Impact@2014!
       The password is then MD5 hashed and base64 encoded.
       In php, it is:
       base64_encode(md5($username . '#Impact@2014!', true));
       In C#: there is pointer in which there is one username which define as a scot

       byte[] asciiBytes = Encoding.ASCII.GetBytes(username + "#Impact@2014!");
       byte[] hashedBytes = MD5.Create().ComputeHash(asciiBytes);
       string hashedPassword = Convert.ToBase64String(hashedBytes);
       Not air-tight security, just a precaution to prevent clubs from accessing each other’s data.
       So you can access the club ‘Scott Hrehirchuk’ with password ‘dDsD7P2nGxiRtLbecIk+Ow==’
       Base64.encode(md5($username . '#Impact@2014!', true));

    URL = "http://impactts.ca/online/listing.php";
    username = "Scott Hrehirchuk";
    password = "dDsD7P2nGxiRtLbecIk+Ow==";

    byte[] asciiBytes = Base64.encode(username.getBytes()).getBytes();
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] hashedBytes = md.digest(asciiBytes);

    byte[] valueDecoded = Base64.decode(hashedBytes);
    String password = new String(valueDecoded);
    System.out.println("Decoded password is " + password);
    xmlHttpRequest();
    System.out.println(httpRequest());
 ** 
 */

/**
 *
 * @author Omer
 */
public class TournmentTest {

    String URL = "";
    String password = "";
    String username = "";
    
    String BASE_URL = "http://impactts.ca/online";

    String login = "/getlogin.php";    
    String leaderBoard = "/getleaderboard.php";
    String listing = "/listing.php";
    
    String LOGIN_URL = BASE_URL + login;
    String LEADERBOARD = BASE_URL + leaderBoard;
    String LISTING_URL = BASE_URL + listing;

    public static void main(String[] args) throws Exception {
        
        try {
            new TournmentTest().sendPost();
            
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void fetchTournament() {
    
        this.username = "Scott Hrehirchuk";
        this.password = "U3RheVByaW1l";
    }

     // HTTP POST request
	private void sendPost() throws Exception {
                
           this.username = "StayPrime";
           
           
//            JSONHTTPRequest  hTTPRequest = new JSONHTTPRequest(WebServicePath.BASE_URL_IMPACT);
            
//            hTTPRequest.createHTTPRequest(URL, params);
           
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest("StayPrime#Impact@2014!".getBytes());
            String pass = Base64.encodeBase64String(digest);
            System.out.println("pass:" + pass);
            String url = "http://impactts.ca/online/listing.php";

                DefaultHttpClient client = new DefaultHttpClient();                
                client.getCredentialsProvider().setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, pass)
                );
                
                
                HttpPost request = new HttpPost(url);

                ArrayList<NameValuePair> urlParameter = new ArrayList<NameValuePair>();
//                urlParameter.add(new BasicNameValuePair("TournamentGuid", "5d41df3e-0eef-4081-867a-7aa1c6242498"));
//                urlParameter.add(new BasicNameValuePair("RoundNo", "1"));
//                urlParameter.add(new BasicNameValuePair("Format", "JSON"));
//                urlParameter.add(new BasicNameValuePair("PlayerId", "69716"));
//                urlParameter.add(new BasicNameValuePair("Format", "JSON"));

                request.setEntity(new UrlEncodedFormEntity(urlParameter));
            
            
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            String reString = EntityUtils.toString(response.getEntity());

            System.err.println("Status code : " + status);
            System.err.println("Response : " + reString);
	}
     
}

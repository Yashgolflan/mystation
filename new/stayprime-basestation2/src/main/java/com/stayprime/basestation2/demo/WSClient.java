/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.demo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jersey REST client generated for REST resource:WebService [/Stayprime]<br>
 * USAGE:
 * <pre>
 *        WSClient client = new WSClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author stayprime
 */
public class WSClient {

    private static final Logger log = LoggerFactory.getLogger(WSClient.class);

    private HttpClient client;
    private static final String BASE_URI = "http://localhost:8080/BaseStationWebService/Rest/Stayprime/";

    public WSClient() {
        client = HttpClientBuilder.create().build();
    }

    public String fnbOrder(int cartNumber, String fnborder) {
        try {
            URIBuilder builder = new URIBuilder(BASE_URI + "fnb");
            addParamIfNotNull(builder, "cartNumber", String.valueOf(cartNumber));            
            addParamIfNotNull(builder, "fnborder", fnborder);
            return executeAndGetResponseString(builder.build());
        }
        catch (URISyntaxException ex) {
            log.error(ex.toString());
        }
        catch (IOException ex) {
            log.error(ex.toString());
        }

        return null;
    }

    public String scoreCard(int cartNumber, String playerName, String scores, String email) {
        try {
            URIBuilder builder = new URIBuilder(BASE_URI + "scorecard");
            addParamIfNotNull(builder, "cartNumber", String.valueOf(cartNumber));
            addParamIfNotNull(builder, "playerName", playerName);
            addParamIfNotNull(builder, "scores", scores);
            addParamIfNotNull(builder, "emailAddress", email);
            return executeAndGetResponseString(builder.build());
        }
        catch (URISyntaxException ex) {
            log.error(ex.toString());
        }
        catch (IOException ex) {
            log.error(ex.toString());
        }

        return null;
    }

    private void addParamIfNotNull(URIBuilder builder, String name, String value) {
        if (value != null) {
            builder.addParameter(name, value);
        }
//        if (paceSecondsLeft != null) {
//            resource = resource.queryParam("paceSecondsLeft", paceSecondsLeft);
//        }
//        if (status != null) {
//            resource = resource.queryParam("status", status);
//        }
//        if (cartNumber != null) {
//            resource = resource.queryParam("cartNumber", cartNumber);
//        }
//        if (clearStatus != null) {
//            resource = resource.queryParam("clearStatus", clearStatus);
//        }
//        if (longitude != null) {
//            resource = resource.queryParam("longitude", longitude);
//        }
//        if (latitude != null) {
//            resource = resource.queryParam("latitude", latitude);
//        }
//        if (angle != null) {
//            resource = resource.queryParam("angle", angle);
//        }
//        if (requestData != null) {
//            resource = resource.queryParam("requestData", requestData);
//        }
//        if (hole != null) {
//            resource = resource.queryParam("hole", hole);
//        }
//        resource = resource.path("loc");
//        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    private String executeAndGetResponseString(URI uri) throws IOException {
        log.debug(uri.toString());
        HttpGet request = new HttpGet(uri);
        //request.addHeader("User-Agent", "");

        HttpResponse httpResponse = client.execute(request);
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        log.info("{}", responseCode);
        String responseContent = IOUtils.toString(httpResponse.getEntity().getContent());
        return responseContent;
    }

//    public String sendCurrentLocation(String course, String paceSecondsLeft, String status, String cartNumber, String clearStatus, String longitude, String latitude, String angle, String requestData, String hole) throws ClientErrorException {
//        WebTarget resource = webTarget;
//        if (course != null) {
//            resource = resource.queryParam("course", course);
//        }
//        if (paceSecondsLeft != null) {
//            resource = resource.queryParam("paceSecondsLeft", paceSecondsLeft);
//        }
//        if (status != null) {
//            resource = resource.queryParam("status", status);
//        }
//        if (cartNumber != null) {
//            resource = resource.queryParam("cartNumber", cartNumber);
//        }
//        if (clearStatus != null) {
//            resource = resource.queryParam("clearStatus", clearStatus);
//        }
//        if (longitude != null) {
//            resource = resource.queryParam("longitude", longitude);
//        }
//        if (latitude != null) {
//            resource = resource.queryParam("latitude", latitude);
//        }
//        if (angle != null) {
//            resource = resource.queryParam("angle", angle);
//        }
//        if (requestData != null) {
//            resource = resource.queryParam("requestData", requestData);
//        }
//        if (hole != null) {
//            resource = resource.queryParam("hole", hole);
//        }
//        resource = resource.path("loc");
//        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
//    }
//
//    public String rounds(String startTime, String scoreCardId, String scores, String playerName, String date, String endTime, String roundId) throws ClientErrorException {
//        WebTarget resource = webTarget;
//        if (startTime != null) {
//            resource = resource.queryParam("startTime", startTime);
//        }
//        if (scoreCardId != null) {
//            resource = resource.queryParam("scoreCardId", scoreCardId);
//        }
//        if (scores != null) {
//            resource = resource.queryParam("scores", scores);
//        }
//        if (playerName != null) {
//            resource = resource.queryParam("playerName", playerName);
//        }
//        if (date != null) {
//            resource = resource.queryParam("date", date);
//        }
//        if (endTime != null) {
//            resource = resource.queryParam("endTime", endTime);
//        }
//        if (roundId != null) {
//            resource = resource.queryParam("roundId", roundId);
//        }
//        resource = resource.path("round");
//        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
//    }
//
//     public String writeOrder(String content) throws ClientErrorException {
//        WebTarget resource = webTarget;
//        if (content != null) {
//            resource = resource.queryParam("content", content);
//        }
//        resource = resource.path("write");
//        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
//     }
//
//
//    public void close() {
//        client.close();
//    }

    public static void main(String args[]) {
        WSClient wsClient = new WSClient();
        System.out.println(wsClient.scoreCard(1, "Ben", "", "observaelsol@gmail.com"));;
    }

}

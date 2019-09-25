/*
 * 
 */
package com.stayprime.tournament.golfgenius;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author benjamin
 */
public class GolfGeniusUtil {
    public static String callGgHtml(Executor executor, int siteId, String extId) throws IOException {
//        String link = "http://www.golfleaguegenius.com/v2tournaments/102801";//"{\"id\":" + extId + "}";
//        String url = "http://labwebapi.golflan.com/gg/html?tournament_id=" + 328391 + "&site_id=" + 29;
        String url = "http://labwebapi.golflan.com/gg/html?tournament_id=" + extId + "&site_id=" + siteId;
        org.apache.http.client.fluent.Request request =
                org.apache.http.client.fluent.Request.Post(url);
        HttpResponse response = executor.execute(request).returnResponse();
        return EntityUtils.toString(response.getEntity());
    }
}

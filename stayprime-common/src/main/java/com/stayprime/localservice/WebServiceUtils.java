/*
 *
 */

package com.stayprime.localservice;

import com.caucho.hessian.client.HessianProxyFactory;
import com.stayprime.demo.DemoUnitCommunication;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class WebServiceUtils {
    private static Logger log = LoggerFactory.getLogger(WebServiceUtils.class);

    public static CartUnitCommunication createCommunicationApi(String connectionString) {
        return createCommunicationApi(connectionString, false);
    }

    public static CartUnitCommunication createCommunicationApi(String connectionString, boolean logEnabled) {
//        log.info("Entering createCommunicationApi()");

        try {
            String webServiceUrl = connectionString + "localservice/sync";
            log.debug("webServiceUrl: " + webServiceUrl);

            HessianProxyFactory proxyFactory = new HessianProxyFactory();
            proxyFactory.setConnectTimeout(TimeUnit.SECONDS.toMillis(5));
            proxyFactory.setReadTimeout(TimeUnit.SECONDS.toMillis(5));
            return (CartUnitCommunication) proxyFactory.create(CartUnitCommunication.class, webServiceUrl);
        }
        catch (Exception ex) {
            log.error("Error creating communication api: " + ex);
            return null;
        }
    }

     public static DemoUnitCommunication createDemoCommunicationApi(String connectionString) {
         return createDemoCommunicationApi(connectionString, false);
     }

     public static DemoUnitCommunication createDemoCommunicationApi(String connectionString, boolean logEnabled) {
//        log.info("Entering createCommunicationApi()");

        try {
            String webServiceUrl = connectionString + "BaseStationWebService/CartUnitCommunication";
            log.debug("webServiceUrl" + webServiceUrl);

            HessianProxyFactory proxyFactory = new HessianProxyFactory();
            proxyFactory.setConnectTimeout(TimeUnit.SECONDS.toMillis(5));
            proxyFactory.setReadTimeout(TimeUnit.SECONDS.toMillis(5));
            return (DemoUnitCommunication) proxyFactory.create(DemoUnitCommunication.class, webServiceUrl);
        }
        catch (Exception ex) {
            log.error("Error creating communication api: " + ex);
            return null;
        }
    }

    public static void main(String args[]) {
        CartUnitCommunication api = createCommunicationApi("http://localhost:8080/");
        System.out.println("Current time: " + new Date(api.getCurrentTime()));
    }
}

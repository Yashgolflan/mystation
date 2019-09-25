/*
 * 
 */
package com.stayprime.cartapp.comm;

import com.stayprime.comm.gprs.request.RequestObserver;


/**
 *
 * @author benjamin
 */
public interface GolfCartMobileComm {

    public boolean sendAssistanceRequest(int requestType, RequestObserver ro);

}

/*
 * 
 */
package com.stayprime.comm.gprs.request;

import com.stayprime.golf.message.Message;
import com.stayprime.golf.message.Payload;

/**
 *
 * @author benjamin
 */
public interface RequestObserver<T extends Payload> {

    public void requestSent(Request<T> request);

    public void requestComplete(Request<T> request, Message p);

    public void requestCanceled(Request<T> request);

    public void requestFailed(Request<T> request);

}

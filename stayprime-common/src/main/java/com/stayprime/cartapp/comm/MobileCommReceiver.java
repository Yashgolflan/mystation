/*
 *
 */
package com.stayprime.cartapp.comm;

import com.stayprime.comm.encoder.Packet;
import com.stayprime.golf.course.CoursePinLocationsInfoDTO;
import com.stayprime.model.golf.HoleCartList;
import com.stayprime.tournament.comm.CartRoundInfo;

/**
 *
 * @author benjamin
 */
public interface MobileCommReceiver {

    public void cartsAheadReceived(HoleCartList list);

    public void textMessageReceived(String message, int timeout);

    public void preMessageReceived(int messageCode, int timeout);

    public void ackReceived(String message);

    public void commandReceived(int messageCode);

    public void roundInfoReceived(Packet<CartRoundInfo> roundInfo);
    
    public void pinLocationListRecieved(CoursePinLocationsInfoDTO coursePinLocationsInfoDTO);

}

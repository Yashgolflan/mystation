/*
 * 
 */
package com.stayprime.comm.socket;

import com.stayprime.comm.PacketReceiver;
import java.net.DatagramPacket;

/**
 *
 * @author benjamin
 */
public interface DatagramPacketReceiver extends PacketReceiver {
    public void packetReceived(DatagramPacket packet);
}

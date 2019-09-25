/*
 * 
 */
package com.stayprime.comm;

import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author benjamin
 */
public interface PacketComm extends PacketSender {
    public void start() throws Exception;
    public void stop();
    public void reconnect() throws Exception;
    public boolean getReady();
    public void setPacketReceiver(PacketReceiver packetReceiver);
    public String getStatusLine();
    public String getAddress();
    public int getPort();
    public void updateConfig(PropertiesConfiguration config);
}

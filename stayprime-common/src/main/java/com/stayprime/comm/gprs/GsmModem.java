/*
 * 
 */
package com.stayprime.comm.gprs;

import com.stayprime.comm.BytePacket;
import com.stayprime.comm.PacketReceiver;

/**
 *
 * @author benjamin
 */
public interface GsmModem {
    public boolean start() throws Exception;
    public boolean stop() throws Exception;
    public boolean reset() throws Exception;
    
    public boolean checkConnection() throws Exception;
    public boolean sendData(byte data[], int len) throws Exception;
    public void setPacketReceiver(PacketReceiver receiver);

    //GPRS Modem related:
    public void setApnConfig(String apn, String user, String pass);
    public void setForceOperator(String operator);
//    public void setForceNetworkId(String forceNetworkId);
//    public void setNetworkSpec(String networkSpec);
    public String getModemIMEI();
    public boolean isSimInserted();
    public String getOperator();
    public String getNetworkId();
    public int getSignalQuality();
    public String getSimIccid();
    public String getSimIMSI();
    public String getIPAddress();

    //Single connection related:
    public void setServerConfig(String server, String port);

    //Internal state related to SIM900:
    public GPRState getState();

    public String getStatusLine();
    
    public interface Observer {
	public void statusChanged(GsmModem module);
    }
}

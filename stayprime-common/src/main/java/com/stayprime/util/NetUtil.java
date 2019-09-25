/*
 * 
 */

package com.stayprime.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class NetUtil {

    public static SocketAddress getSocketAddress(String addr) {
        if (addr == null) {
            return null;
        }
        int ipStart = addr.indexOf('/') + 1;
        int colon = addr.indexOf(':');
        if (colon > 0) {
            String host = addr.substring(ipStart, colon);
            int port = NumberUtils.toInt(addr.substring(colon + 1));

            InetAddress inetAddr = getInetAddress(host);
            if (inetAddr != null && port > 0) {
                return new InetSocketAddress(inetAddr, port);
            }
        }
        return null;
    }

    public static InetAddress getInetAddress(String host) {
        try {
            return InetAddress.getByName(host);
        }
        catch (UnknownHostException ex) {
            return null;
        }
    }

    public static InetAddress getInet4Address(NetworkInterface networkInterface) {
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        while(inetAddresses.hasMoreElements()) {
            InetAddress ia = inetAddresses.nextElement();
            if(ia instanceof Inet4Address)
                return ia;
        }
        return null;
    }

}

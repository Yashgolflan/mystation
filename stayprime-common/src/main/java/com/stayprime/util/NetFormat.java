/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.util;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class NetFormat {
    public static String getIPAddressString(int ip[]) {
	StringBuilder ipString = new StringBuilder();
	for (int i = 0; i < ip.length; i++) {
	    byte b = (byte) ip[i];
	    ipString.append(Integer.toString(( b & 0xff )));

	    if(i < ip.length - 1)
		ipString.append('.');
	}

	return ipString.toString();
    }

    public static byte[] getIPAddressBytes(int ip) {
	byte b[] = new byte[4];
	b[0] = (byte) (ip >> 0x18 & 0xff);
	b[1] = (byte) (ip >> 0x10 & 0xff);
	b[2] = (byte) (ip >> 0x08 & 0xff);
	b[3] = (byte) (ip & 0xff);
	return b;
    }

    public static byte[] getIPAddressBytes(String ip) {
	String parts[] = StringUtils.splitByCharacterType(ip);

	if(parts.length != 7)
	    throw new IllegalArgumentException("Illegal IP address format: " + ip);

	byte b[] = new byte[4];

	for(int i = 0; i < 4; i++) {
	    b[i] = (byte) Integer.parseInt(parts[i*2]);
	}

	return b;
    }


    public static Integer getIPAddressNumber(byte b[]) {
	if(b == null)
	    return null;

	int ip = 0;
	int n = b.length > 4? 4 : b.length;

	for(int i = 0; i < n; i++)
	    ip |= ((int) b[n - i - 1] & 0xFF) << i*8;

	return ip;
    }

    public static String getMACAddressString(byte mac[]) {
	if(mac == null)
	    return null;
	
	StringBuilder macString = new StringBuilder();

	for (int i = 0; i < mac.length; i++) {
	    int b = mac[i];
	    macString.append(Integer.toHexString(b & 0xFF | 0x100).substring(1));

	    if(i < mac.length - 1)
		macString.append(':');
	}

	return macString.toString().toLowerCase();
    }

    public static byte[] getMACAddressBytes(long mac) {
	byte b[] = new byte[6];

	for(int i = 0; i < 6; i++) {
	    long shift = 0xFFFFFFFFFFFFl;
	    b[i] = (byte) ((mac & shift) >> (b.length - i - 1) * 8);
	}

	return b;
    }

    public static byte[] getMACAddressBytes(String mac) {
	String parts[] = mac.split(":");

	if(parts.length != 6)
	    throw new IllegalArgumentException("Illegal MAC address format: " + mac);
	
	byte b[] = new byte[6];

	for(int i = 0; i < 6; i++) {
	    b[i] = (byte) Integer.parseInt(parts[i], 16);
	}

	return b;
    }

    public static Long getMACAddressNumber(byte b[]) {
	if(b == null)
	    return null;
	
	long mac = 0;
	int n = b.length > 6? 6 : b.length;

	for(int i = 0; i < n; i++)
	    mac |= ((long) b[n - i - 1] & 0xFF) << i*8;

	return mac;
    }
}

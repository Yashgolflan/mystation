/*
 * 
 */
package com.stayprime.comm;

import com.stayprime.comm.encoder.EncodeUtil;

/**
 *
 * @author benjamin
 */
public class BytePacket {
    public static final byte ESCAPE_CHAR = (byte) 0xFF;
    
    private final byte packet[];
    private int length;

    public BytePacket() {
        this(new byte[EncodeUtil.maxPacketLength]);
    }

    public BytePacket(int size) {
        this(new byte[size]);
    }

    public BytePacket(byte[] packet) {
	this(packet, 0);
    }
    
    public BytePacket(byte[] packet, int length) {
	this.packet = packet;
	this.length = length;
    }
    
    public int getLength() {
	return length;
    }

    public void setLength(int length) {
	this.length = length;
    }

    public byte[] getPacket() {
	return packet;
    }

    public void set(int index, int data) {
	packet[index] = (byte) data;
    }

    @Override
    public String toString() {
	StringBuilder s = new StringBuilder("{");

	for(int i = 0; i < length; i++)
	    s.append(packet[i]).append(',');

	if(length > 0)
	    s.deleteCharAt(s.length() - 1);

	s.append('}');

	return s.toString();
    }
    
}

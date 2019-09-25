/*
 * 
 */
package com.stayprime.golf.message;

import com.stayprime.comm.encoder.PacketType;

/**
 *
 * @author benjamin
 */
public interface Payload {

    public PacketType getPacketType();

    public int encode(byte[] pack, int offset);

}

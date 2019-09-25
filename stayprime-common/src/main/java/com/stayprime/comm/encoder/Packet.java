/*
 * 
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.golf.message.Payload;

/**
 *
 * @author benjamin
 * @param <T> Payload class type
 */
public class Packet<T extends Payload> {
    private int siteId;
    private int packetType;
    private int cartNumber;
    private int messageCounter;
    private T payload;

    public Packet() {
    }

    public Packet(int siteId, int cartNumber, int messageCounter) {
        this.siteId = siteId;
        this.cartNumber = cartNumber;
        this.messageCounter = messageCounter;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }

    public int getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(int cartNumber) {
        this.cartNumber = cartNumber;
    }

    public int getMessageCounter() {
        return messageCounter;
    }

    public void setMessageCounter(int messageCounter) {
        this.messageCounter = messageCounter;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
        this.packetType = payload.getPacketType().id;
    }

    public void encode(BytePacket bytePacket) {
        byte[] pack = bytePacket.getPacket();
        int offset = EncodeUtil.encodeHeader(pack, 0, siteId, packetType, cartNumber, messageCounter);
        offset = payload.encode(pack, offset);
        bytePacket.setLength(offset);
    }

}

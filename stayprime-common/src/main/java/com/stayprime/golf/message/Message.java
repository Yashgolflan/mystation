/*
 * 
 */
package com.stayprime.golf.message;

import com.stayprime.comm.encoder.MessageEncoder;
import com.stayprime.comm.encoder.PacketType;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class Message implements Payload {
    private PacketType packetType;
    private int messageCode;
    private int[] details = ArrayUtils.EMPTY_INT_ARRAY;
    private String text = StringUtils.EMPTY;

    public Message(PacketType packetType, int messageCode) {
        this(packetType, messageCode, null);
    }

    public Message(PacketType packetType, int messageCode, String messageText) {
        this.packetType = packetType;
        this.messageCode = messageCode;
        this.text = messageText;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }

    public int[] getDetails() {
        return details;
    }

    public void setDetails(int ... details) {
        this.details = details;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? StringUtils.EMPTY : text;
    }

    @Override
    public PacketType getPacketType() {
        return packetType;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        return MessageEncoder.encodeMessage(pack, offset, messageCode, text, details);
    }

}

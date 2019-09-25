/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.hibernate.entities;

import com.stayprime.comm.Encodable;
import com.stayprime.comm.BytePacket;
import com.stayprime.comm.encoder.MessageEncoder;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author benjamin
 */
@Entity
public class OutgoingMessage implements Encodable {

    @Id @GeneratedValue
    private int id;

    private int toCart;

    private int type;

    private int code;

    private String text;

    private MessageStatus sendStatus;

    public OutgoingMessage() {
        sendStatus = new MessageStatus();
    }

    public OutgoingMessage(int toCart, int type, int code, String text) {
        this.toCart = toCart;
        this.type = type;
        this.code = code;
        this.text = text;
        sendStatus = new MessageStatus();
        sendStatus.setCreated(new Date());
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getToCart() {
        return toCart;
    }

    public void setToCart(int to) {
        this.toCart = to;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(MessageStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).toString();
    }

    @Override
    public BytePacket encode(int siteId) {
        String messageText = getText();
        BytePacket pack = new BytePacket(MessageEncoder.getMessageEncodedSize(messageText));
        MessageEncoder.encodeTextMessage(pack, siteId, getToCart(), getSendStatus().getMessageCounter(), 0, messageText);
        return pack;
    }

}

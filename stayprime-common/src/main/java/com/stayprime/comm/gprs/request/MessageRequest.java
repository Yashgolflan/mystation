/*
 * 
 */
package com.stayprime.comm.gprs.request;

import com.stayprime.comm.encoder.Packet;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.golf.message.Message;

/**
 *
 * @author benjamin
 */
public class MessageRequest extends Request<Message> {

    public MessageRequest(Packet<Message> packet) {
        super(packet);
    }

    @Override
    public boolean processAck(Packet<Message> p) {
        Message m = (Message) p.getPayload();

        if (p.getPacketType() == PacketType.ACK.id
                && m.getMessageCode() == packet.getPayload().getMessageCode()) {
            complete(p);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelMatching(Packet packet) {
        if (packet.getPayload() instanceof Message) {
            Message message = (Message) packet.getPayload();
            if (message.getMessageCode() == this.packet.getPayload().getMessageCode()) {
                cancel();
                return true;
            }
        }
        return false;
    }

}

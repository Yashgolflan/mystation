/*
 *
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.golf.message.Message;

/**
 *
 * @author benjamin
 */
public class MessageEncoder {

    /**
     * Encode reception acknowledgement message.
     * At the moment the type for predefined messages is their message code,
     * rather than their message type, this needs to be reviewed.
     *
     * @param packet BytePacket to store the encoded message
     * @param siteId
     * @param sourceId
     * @param messageCounter incremental identifier of the acknowledged message
     * @param type type of the acknowledged message
     * @param text extra text containing information about the acknowledgement
     * @param details extra bytes containing information about the acknowledgement
     * @return true if the encoding was successful
     */
    public static boolean encodeAck(BytePacket packet, int siteId, int sourceId,
            int messageCounter, int type, String text, int ... details) {

        byte pack[] = packet.getPacket();
        int offset = 0;

        offset = EncodeUtil.encodeHeader(pack, offset, siteId, PacketType.ACK.id, sourceId, messageCounter);

        offset = encodeMessage(pack, offset, type, text, details);

        packet.setLength(offset);
        return true;
    }

    public static Packet<Message> decodeAck(BytePacket packet) {
        return decodeMessage(packet, PacketType.ACK.id);
    }

    public static int getMessageEncodedSize(String text, int ... details) {
        return PacketType.headerLength + 2 + details.length + EncodeUtil.encodedLength(text, true);
    }

    public static boolean encodeTextMessage(BytePacket packet, int siteId, int sourceId, int messageCounter, int messageCode, String text) {
        return encodeMessage(packet, siteId, sourceId, PacketType.TEXT_MESSAGE.id, messageCounter, messageCode, text);
    }

    public static Packet<Message> decodeTextMessage(BytePacket packet) {
        return decodeMessage(packet, PacketType.TEXT_MESSAGE.id);
    }

    public static boolean encodePresetMessage(BytePacket packet, int siteId, int sourceId, int messageCounter, int messageCode) {
        return encodeMessage(packet, siteId, sourceId, PacketType.PRE_MSG.id, messageCounter, messageCode, null);
    }

    public static Packet<Message> decodePresetMessage(BytePacket packet) {
        return decodeMessage(packet, PacketType.PRE_MSG.id);
    }

    public static Packet<Message> encodeMessagePacket(BytePacket packet, int siteId, int sourceId, PacketType type, int messageCounter, int messageCode, String text) {
        boolean encoded = encodeMessage(packet, siteId, sourceId, type.id, messageCounter, messageCode, text);
        if (encoded) {
            Packet<Message> p = new Packet<Message>(siteId, sourceId, messageCounter);
            p.setPayload(new Message(type, messageCode, text));
            return p;
        }
        else {
            return null;
        }
    }

    /**
     * Encode general type message and message code.
     *
     * @param packet buffer for the encoded packet
     * @param siteId
     * @param sourceId sender id
     * @param type message type
     * @param messageCounter sequential message id
     * @param messageCode message code identifier
     * @return true if the message was encoded successfully
     */
    public static boolean encodeMessage(BytePacket packet, int siteId, int sourceId,
            int type, int messageCounter, int messageCode, String text) {

        byte pack[] = packet.getPacket();
        int offset = 0;

        offset = EncodeUtil.encodeHeader(pack, offset, siteId, type, sourceId, messageCounter);
        offset = encodeMessage(pack, offset, messageCode, text);
        packet.setLength(offset);
        return true;
    }

    public static int encodeMessage(byte pack[], int offset, int messageCode,
            String text, int ... details) {

        int off = offset;

        pack[off] = (byte) messageCode;
        off++;

        pack[off] = (byte) details.length;
        off++;

        for (int i = 0; i < details.length; i++) {
            pack[off] = (byte) details[i];
            off++;
        }

        off = EncodeUtil.encodeString(pack, off, text, true);

        return off;
    }

    /**
     * Decode general type message and message code.
     *
     * @param packet buffer for the encoded packet
     * @param type requested message type to decode
     * @return the decoded Message if the message type matches
     */
    public static Packet<Message> decodeMessage(BytePacket packet, int type) {
        byte pack[] = packet.getPacket();
        int offset = 0;

        Packet<Message> re = new Packet<Message>();
        offset = EncodeUtil.decodeHeader(pack, offset, re);

        if (re.getPacketType() != type) {
            return null;
        }

        Message message = new Message(PacketType.get(type), 0);
        offset = decodeMessage(pack, offset, message);
        re.setPayload(message);

        return re;
    }

    private static int decodeMessage(byte[] pack, int offset, Message message) {
        int off = offset;
        message.setMessageCode(pack[off]);
        off++;

        int detailsLength = EncodeUtil.decodeUnsigned(pack, off);
        off++;

        int[] details = new int[detailsLength];
        for (int i = 0; i < detailsLength; i++) {
            details[i] = pack[off];
            off++;
        }
        message.setDetails(details);

        String text = EncodeUtil.decodeString(pack, off, true);
        off += text.length() + 1;
        message.setText(text);

        return off;
    }
    
    
    /**
     * Check message for ranger request.
     *
     * @param bytePacket buffer for the encoded packets
     * @return true if request is of RangerRequest type
     */
    public static Boolean checkMessageForRangerRequest(BytePacket bytePacket) {
        Packet<Message> packet = checkMessageForRangerRequestHelper(bytePacket, PacketType.PRE_MSG.id);
        return packet.getPayload().getMessageCode() == PacketType.RANGER_REQUEST;
    }
    
    /**
     * Check message for ranger request helper.
     *
     * @param packet buffer for the encoded packet
     * @param type requested message type to decode
     * @return the decoded Message if the message type matches
     */
    private static Packet<Message> checkMessageForRangerRequestHelper(BytePacket packet, int type) {
        byte pack[] = packet.getPacket();
        int offset = 0;

        Packet<Message> re = new Packet<Message>();
        offset = EncodeUtil.decodeHeader(pack, offset, re);

        if (re.getPacketType() != type) {
            return null;
        }

        Message message = new Message(PacketType.get(type), 0);
        message.setMessageCode(pack[offset]);
        offset++;
        re.setPayload(message);

        return re;
    }
    
    

}

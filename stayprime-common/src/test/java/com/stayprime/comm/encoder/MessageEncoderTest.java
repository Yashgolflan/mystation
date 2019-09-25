/*
 * 
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import com.stayprime.golf.message.Message;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class MessageEncoderTest {
    
    public MessageEncoderTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testEncodeAck() {
        System.out.println("encodeAck");
//        BytePacket packet = null;
//        int sourceId = 0;
//        int messageId = 0;
//        int messageCode = 0;
//        String text = "";
//        boolean expResult = false;
//        boolean result = MessageEncoder.encodeAck(packet, sourceId, messageId, messageCode, text);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testDecodeAck() {
        System.out.println("decodeAck");
//        BytePacket packet = null;
//        TextMessage expResult = null;
//        TextMessage result = MessageEncoder.decodeAck(packet);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testEncodeTextMessage_5args() {
        System.out.println("encodeTextMessage");
//        BytePacket packet = null;
//        int sourceId = 0;
//        int messageId = 0;
//        int messageCode = 0;
//        String text = "";
//        boolean expResult = false;
//        boolean result = MessageEncoder.encodeTextMessage(packet, sourceId, messageId, messageCode, text);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testDecodeTextMessage_BytePacket() {
        System.out.println("decodeTextMessage");
//        BytePacket packet = null;
//        TextMessage expResult = null;
//        TextMessage result = MessageEncoder.decodeTextMessage(packet);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testEncodePresetMessage() {
        System.out.println("encodePresetMessage");
//        BytePacket packet = null;
//        int sourceId = 0;
//        int messageId = 0;
//        int messageCode = 0;
//        boolean expResult = false;
//        boolean result = MessageEncoder.encodePresetMessage(packet, sourceId, messageId, messageCode);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testDecodePresetMessage() {
        System.out.println("decodePresetMessage");
//        BytePacket packet = null;
//        Message expResult = null;
//        Message result = MessageEncoder.decodePresetMessage(packet);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testEncodeMessage() {
        System.out.println("encodeMessage");
        BytePacket packet = new BytePacket();
        int siteId = 100;
        int sourceId = 10;
        PacketType type = PacketType.PRE_MSG;
        int messageCounter = 0;
        int messageCode = PacketType.RANGER_REQUEST;
        String text = "Test Message";

        boolean encoded = MessageEncoder.encodeMessage(packet, siteId, sourceId, type.id, messageCounter, messageCode, text);
        assertTrue(encoded);

        Packet<Message> p = MessageEncoder.decodeMessage(packet, 0);
        assertNull(p); //Not encoded because the packet type doesn't match

        p = MessageEncoder.decodeMessage(packet, type.id);
        Message message = p.getPayload();
        assertEquals(siteId, p.getSiteId());
        assertEquals(type.id, p.getPacketType());
        assertEquals(sourceId, p.getCartNumber());
        assertEquals(messageCounter, p.getMessageCounter());
        assertEquals(messageCode, message.getMessageCode());
        assertEquals(text, message.getText());
    }

    @Test
    public void testDecodeMessage() {
        System.out.println("decodeMessage");
//        BytePacket packet = null;
//        int type = 0;
//        Message expResult = null;
//        Message result = MessageEncoder.decodeMessage(packet, type);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testEncodeTextMessage_6args() {
        System.out.println("encodeTextMessage");
//        BytePacket packet = null;
//        int sourceId = 0;
//        int type = 0;
//        int messageId = 0;
//        int messageCode = 0;
//        String text = "";
//        boolean expResult = false;
//        boolean result = MessageEncoder.encodeTextMessage(packet, sourceId, type, messageId, messageCode, text);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }

    @Test
    public void testDecodeTextMessage_BytePacket_int() {
//        System.out.println("decodeTextMessage");
//        BytePacket packet = null;
//        int type = 0;
//        TextMessage expResult = null;
//        TextMessage result = MessageEncoder.decodeTextMessage(packet, type);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
    }
    
}

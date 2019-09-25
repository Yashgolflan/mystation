/*
 * 
 */
package com.stayprime.comm;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author benjamin
 */
public class BytePacketUtilsTest {
    
    public BytePacketUtilsTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testWriteEscaped() throws Exception {
        System.out.println("writeEscaped");
        BytePacket a = new BytePacket();
        

        a.setLength(100);
        ByteArrayOutputStream out = new ByteArrayOutputStream(200);

        for (int i = 0; i < a.getLength(); i++) {
            byte[] packet = a.getPacket();
            packet[i] = (byte) i;
            BytePacketUtils.writeEscaped(out, packet[i]);
        }

        byte[] array = out.toByteArray();
        BytePacket b = new BytePacket(array, array.length);

        System.out.println("Testing escape");
        assertFalse(compare(a, b));
        BytePacketUtils.unescapePacket(b);
        System.out.println("Testing unescape");
        assertTrue(compare(a, b));
    }

    @Test
    public void testEscapePacket() {
        System.out.println("escapePacket");
        BytePacket a = new BytePacket();
        BytePacket b = new BytePacket();

        a.setLength(100);
        for (int i = 0; i < a.getLength(); i++) {
            a.getPacket()[i] = (byte) i;
        }
        
        BytePacketUtils.escapePacket(a, b);
        System.out.println("Testing escape");
        assertFalse(compare(a, b));
        BytePacketUtils.unescapePacket(b);
        System.out.println("Testing unescape");
        assertTrue(compare(a, b));
    }

    private boolean compare(BytePacket a, BytePacket b) {
        boolean equals = a.getLength() == b.getLength();
        for (int i = 0; i < a.getLength(); i++) {
            if (a.getPacket()[i] != b.getPacket()[i]) {
                System.out.println("Index " + i + ", " + a.getPacket()[i] + " != " + b.getPacket()[i]);
                equals = false;
                break;
            }
        }

        return equals;
    }
    
}

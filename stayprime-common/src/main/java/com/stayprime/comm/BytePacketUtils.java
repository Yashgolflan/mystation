/*
 *
 */
package com.stayprime.comm;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author benjamin
 */
public class BytePacketUtils {

    public static void writeEscaped(OutputStream out, byte b) throws IOException {
	if(b == 26 || b == 27 || b == BytePacket.ESCAPE_CHAR) {
	    out.write(BytePacket.ESCAPE_CHAR);
	    out.write(~b);
	}
	else
	    out.write(b);
    }

    public static void escapePacket(BytePacket data, BytePacket escaped) {
        escaped.setLength(escapePacket(data.getPacket(), escaped.getPacket(), data.getLength()));
    }

    public static void unescapePacket(BytePacket data) {
        data.setLength(unescapePacket(data.getPacket(), 0, data.getLength()));
    }

    public static int escapePacket(byte[] data, byte[] escaped, int len) {
        int length = len;
	for(int i = 0, j = 0; i < len; i++, j++) {
            byte b = data[i];

            if (b == 26 || b == 27 || b == BytePacket.ESCAPE_CHAR) {
		escaped[j++] = (byte) BytePacket.ESCAPE_CHAR;
		escaped[j] = (byte) ~b;

		length++;
	    }
            else {
               escaped[j] = b;
            }
	}

        return length;
    }

    public static int unescapePacket(byte[] data, int offset, int len) {
        int length = len;
	for(int i = 0, j = 0; i < length; i++, j++) {
            byte b = data[j];

            if (b == BytePacket.ESCAPE_CHAR) {
                byte next = data[j + 1];
		data[i] = (byte) ~next;

                j++;
		length--;
	    }
            else {
               data[i] = b;
            }
	}

        return length;
    }
}

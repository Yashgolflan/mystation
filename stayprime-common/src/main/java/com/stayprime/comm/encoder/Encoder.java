/*
 * 
 */
package com.stayprime.comm.encoder;

import com.stayprime.geo.Coordinates;
import com.stayprime.model.golf.Position;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class Encoder {
    private byte[] pack;

    private int offset;

    private boolean success = true;

    public Encoder(byte[] pack, int offset) {
        this.pack = pack;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isSuccess() {
        return success;
    }

    public Encoder encodeHeader(int siteId, int packetType, int cartNumber, int messageCounter) {
        if (EncodeUtil.isValidCartNumber(cartNumber) == false) {
            success = false; //Fail if the cart number is not valid for encoding
        }
        else {
            offset = EncodeUtil.encodeHeader(pack, offset, siteId, packetType, cartNumber, messageCounter);
        }
        return this;
    }

    public Encoder decodeHeader(Packet re) {
        return decodeHeader(re, null);
    }

    public Encoder decodeHeader(Packet re, PacketType type) {
        offset = EncodeUtil.decodeHeader(pack, offset, re);
        if (type != null && type.test(re.getPacketType()) == false) {
            success = false;
        }
        return this;
    }

    public Encoder encodeString(String string, boolean longStr) {
        offset = EncodeUtil.encodeString(pack, offset, string, longStr);
        return this;
    }

    public Encoder encodeString(String string, int maxLength, boolean longStr) {
        offset = EncodeUtil.encodeString(pack, offset, string, maxLength, longStr);
        return this;
    }

    public String decodeString(boolean longStr) {
        String s = EncodeUtil.decodeString(pack, offset, longStr);
        offset += EncodeUtil.encodedLength(s, longStr);
        return s;
    }

    public int encodeListLength(List list, int maxListSize) {
        int size = (list == null ? 0 : list.size());
        int len = EncodeUtil.encodeLength(pack, offset, size, maxListSize);
        offset++;
        return len;
    }

    public Encoder encodeByte(int b) {
        pack[offset] = (byte) b;
        offset++;
        return this;
    }

    public Encoder encodeUnsigned(int i, int invalidValue) {
        EncodeUtil.encodeUnsigned(pack, offset, i, invalidValue);
        offset++;
        return this;
    }

    /**
     * Encode an unsigned byte, use invalidValue if number is invalid.
     * @param n the number to encode
     * @param invalidValue the value if n is out of unsigned byte bounds
     * @return this encoder for linking operations
     */
    public Encoder encodeUnsigned(Integer n, int invalidValue) {
        int value = (n == null? invalidValue : n);
        EncodeUtil.encodeUnsigned(pack, offset, value, invalidValue);
        offset++;
        return this;
    }

    public int decodeUnsigned() {
        int u = EncodeUtil.decodeUnsigned(pack, offset);
        offset++;
        return u;
    }
    
    
    public Encoder encodePositionRelativeToOrigin(Position origin, Position pos) {
        // use EncodeUtil to encode the position of pin w.r.t the origin of the site i.e. topLeftCorner
        EncodeUtil.encodeRelativePosition(pack, offset, origin, pos);
        offset += 4;
        return this;
    }
    
    public Position decodePositionRelativeToOrigin(Position origin) {
        Position position = EncodeUtil.decodeRelativePosition(pack, offset, origin, null);
        offset += 4;
        return position;
    }

}

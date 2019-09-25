/*
 *
 */
package com.stayprime.comm.encoder;

import com.stayprime.model.golf.Position;
import com.stayprime.util.BitwiseUtil;
import com.stayprime.util.MathUtil;
import java.awt.geom.Point2D;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

/**
 *
 * @author benjamin
 */
public class EncodeUtil {

    public static final int maxPacketLength = 2048;
    public static final int bigPacketLength = 16384;

    public static final short SHORT_NULL = -32768;
    public static final short SHORT_MIN = -32767;
    public static final short SHORT_MAX = 32767;
    public static final IntRange COORD_RANGE = new IntRange(SHORT_MIN + 1, SHORT_MAX - 1);
    public static final IntRange SHORT_RANGE = new IntRange(SHORT_MIN, SHORT_MAX);

    public static final byte BYTE_NULL = -128;
    public static final byte BYTE_MIN = -127;
    public static final byte BYTE_MAX = 127;
    public static final int UBYTE_MAX = 255;
    public static final IntRange BYTE_RANGE = new IntRange(BYTE_MIN, BYTE_MAX);
    public static final IntRange UBYTE_RANGE = new IntRange(0, UBYTE_MAX);

    public static final IntRange byteRange = new IntRange(0, 255);
    public static final IntRange nibbleRange = new IntRange(0, 15);

    public static boolean isValidUnsignedByte(int i) {
        return UBYTE_RANGE.containsInteger(i);
    }

    public static boolean isValidCartNumber(int cartNumber) {
        return cartNumber > 0 && cartNumber <= UBYTE_RANGE.getMaximumInteger();
    }

    public static int encodeHeader(byte[] pack, int startOffset, int siteId, int packetType, int cartNumber, int messageCounter) {
        int offset = startOffset;
        pack[offset] = (byte) siteId;
        offset++;

        pack[offset] = (byte) packetType;
        offset++;

        pack[offset] = (byte) cartNumber;
        offset++;

        pack[offset] = (byte) messageCounter;
        offset++;

        return offset;
    }

    public static int decodeHeader(byte[] pack, int startOffset, Packet re) {
        int offset = startOffset;
        re.setSiteId(EncodeUtil.decodeUnsigned(pack, offset));
        offset++;

        re.setPacketType(pack[offset]);
        offset++;

        re.setCartNumber(EncodeUtil.decodeUnsigned(pack, offset));
        offset++;

        re.setMessageCounter(pack[offset]);
        offset++;

        return offset;
    }

    public static void encodeSpeed(float speed, byte[] dest, int startOffset) {
        dest[startOffset] = (byte) Math.round(speed);
    }

    public static float decodeSpeed(byte[] dest, int startOffset) {
        return dest[startOffset];
    }

    public static void encodeBearing(double heading, byte[] dest, int startOffset) {
        if (Double.isNaN(heading) || Double.isInfinite(heading))
            dest[startOffset] = (byte) BYTE_NULL;
        else
            dest[startOffset] = (byte) (Math.round(MathUtil.normalizeAngle(heading)) / 2);
    }

    public static float decodeBearing(byte[] dest, int startOffset) {
        if (dest[startOffset] == BYTE_NULL)
            return 0; //Should return NaN?
        else
            return dest[startOffset] * 2.0F;
    }

    public static void encodeRelativePosition(byte[] buffer, int startOffset, Point2D origin, Point2D point) {
        boolean valid = origin != null && point != null;
        if (valid) {
        encodeRelativePosition(buffer, startOffset, origin.getX(), origin.getY(), point.getX(), point.getY(), valid);
    }
        else {

        }
    }

    public static void encodeRelativePosition(byte[] buffer, int startOffset, Position origin, Position point) {
        boolean valid = origin != null && point != null;
        encodeRelativePosition(buffer, startOffset, origin.getX(), origin.getY(), point.getX(), point.getY(), valid);
    }

    public static void encodeRelativePosition(byte[] buffer, int startOffset, Point2D origin, Position point) {
        boolean valid = origin != null && point != null;
        encodeRelativePosition(buffer, startOffset, origin.getX(), origin.getY(), point.getX(), point.getY(), valid);
    }

    /**
     * Decodes position relative to origin from a byte array. If a destination
     * Point2D object is passed, it sets it's latitude and longitude instead of
     * creating a new Coordinates object. If the resulting coordinates are offset
     * the decoder's range the passed Coordinates object is set as (NaN,NaN),
     * and null is returned.
     *
     * @param buffer the encoding destination byte buffer
     * @param startOffset the encoding buffer start startOffset
     * @param x0 x of origin to which the position is relative
     * @param y0 x of origin to which the position is relative
     * @param x x position to encode
     * @param y y position to encode
     * @param valid defines if the position is valid or not
     */
    public static void encodeRelativePosition(byte[] buffer, int startOffset, double x0, double y0, double x, double y, boolean valid) {
        boolean val = valid;
        if (val) {
            val &= Double.isNaN(x) == false && Double.isInfinite(x) == false;
            val &= Double.isNaN(y) == false && Double.isInfinite(y) == false;
        }

        if (val) {
            BitwiseUtil.setBytes(encodeRelativeCoord(x0, x), 2, buffer, startOffset);
            BitwiseUtil.setBytes(encodeRelativeCoord(y0, y), 2, buffer, startOffset + 2);
        }
        else {
            encodeInvalidPosition(buffer, startOffset);
        }
    }

    public static void encodeInvalidPosition(byte[] buffer, int startOffset) {
            BitwiseUtil.setBytes(SHORT_NULL, 2, buffer, startOffset);
            BitwiseUtil.setBytes(SHORT_NULL, 2, buffer, startOffset + 2);
    }

    public static short encodeRelativeCoord(double origin, double coord) {
        double total = coord - origin;
        double minutes = total * 60;
        long shiftedMinutes = Math.round(minutes * 1000);
        if (shiftedMinutes >= SHORT_MAX)
            return SHORT_MAX;
        else if (shiftedMinutes <= SHORT_MIN)
            return SHORT_MIN;
        else
            return (short) shiftedMinutes;
    }

    /**
     * Decodes position relative to origin from a byte array. If a destination
     * Point2D object is passed, it sets it's latitude and longitude instead of
     * creating a new Point2D object. If the resulting coordinates are offset the
     * decoder's range the passed point object is set as (0, 0), and null is
     * returned.
     *
     * @param origin the origin to which the position is relative
     * @param point the destination Point2D object, to avoid allocating new
     * objects
     * @param buffer the decoding source byte buffer
     * @param startOffset the decoding start startOffset
     * @return the passed coordinates object set to the resulting coordinates,
     * or null if the result was invalid
     */
    public static Position decodeRelativePosition(byte[] buffer, int startOffset, Position origin, Position point) {
        Position dest = point;
        short longMinutes = BitwiseUtil.getShort(buffer, startOffset);
        short latMinutes = BitwiseUtil.getShort(buffer, startOffset + 2);

        if (origin == null || !COORD_RANGE.containsInteger(longMinutes) || !COORD_RANGE.containsInteger(latMinutes)) {
            if (dest != null) {
                dest.setLocation(0, 0);
            }
            return null;
        }
        else {
            double lon = decodeRelativeCoord(origin.getX(), longMinutes);
            double lat = decodeRelativeCoord(origin.getY(), latMinutes);

            if (dest == null) {
                dest = new Position(lon, lat);
            }
            else {
                dest.setLocation(lon, lat);
            }
            return dest;
        }
    }

    public static double decodeRelativeCoord(double origin, short encoded) {
        if (encoded <= SHORT_MIN)
            return Double.NEGATIVE_INFINITY;
        else if (encoded >= SHORT_MAX)
            return Double.POSITIVE_INFINITY;
        else {
            double degrees = encoded / 1000.0 / 60.0;
            return origin + degrees;
        }
    }

    public static void encodeHoleNumber(byte[] dest, int startOffset, int holeNumber) {
        dest[startOffset] = (byte) holeNumber; //Encode/decode as unsigned
    }

    public static int decodeHoleNumber(byte[] src, int startOffset) {
        return BitwiseUtil.getUnsigned(src[startOffset]);
    }
    
    public static void encodeBatteryStatus(byte[] dest, int startOffset, int battStatus) {
        dest[startOffset] = (byte) battStatus; //Encode/decode as unsigned
    }

    public static int decodeBatteryStatus(byte[] src, int startOffset) {
        return BitwiseUtil.getUnsigned(src[startOffset]);
    }

    public static void encodeDeviceType(byte[] dest, int startOffset, int cartType) {
        dest[startOffset] = (byte) cartType; //Encode/decode as unsigned
    }

    public static int decodeDeviceType(byte[] src, int startOffset) {
        return BitwiseUtil.getUnsigned(src[startOffset]);
    }

    public static void encodePaceOfPlaySeconds(byte[] dest, int startOffset, Integer seconds) {
        if (seconds == null)
            dest[startOffset] = (byte) BYTE_NULL;
        else
            dest[startOffset] = (byte) getPaceOfPlayMinutes(seconds);
    }

    public static int decodePaceOfPlaySeconds(byte[] src, int startOffset) {
        int minutes = src[startOffset];

        if (minutes == BYTE_NULL || !BYTE_RANGE.containsInteger(minutes)) {
            return BYTE_NULL;
        }
        else {
            return minutes * 60;
        }
    }

    public static int getPaceOfPlayMinutes(int seconds) {
        int minutes = seconds / 60;

        if (BYTE_RANGE.containsInteger(minutes)) {
            return minutes;
        }
        else {
            return BYTE_NULL;
        }
    }

    public static void encodePacketCount(byte[] buffer, int startOffset, int index, int count) {
        if (!nibbleRange.containsInteger(index) || !nibbleRange.containsInteger(count)) {
            throw new IllegalArgumentException("Index or total packet count out of range");
        }

        buffer[startOffset] = (byte) ((count << 4) | index);
    }

//    public static void decodePacketCount(byte[] buffer, int startOffset, MultiPacketMessage msg) {
//        byte count = buffer[startOffset];
//        msg.setMessageCount(BitwiseUtil.getUnsigned((byte) ((count >> 4) & 0x0f)));
//        msg.setMessageIndex(BitwiseUtil.getUnsigned((byte) (count & 0x0f)));
//    }
    public static int encodeLength(byte buffer[], int startOffset, int length, int maxLength) {
        int len = Math.min(length, maxLength);
        buffer[startOffset] = (byte) len;
        return len;
    }

    public static void encodeByte(byte[] pack, int startOffset, int number) {
        pack[startOffset] = (byte) number;
    }

    /**
     * Encode an unsigned byte, use invalidValue if number is invalid.
     * @param pack the packet to encode into
     * @param startOffset offset
     * @param number the number to encode
     * @param invalidValue the value if number is out of unsigned byte bounds
     * @return true if number was valid
     */
    public static boolean encodeUnsigned(byte[] pack, int startOffset, int number, int invalidValue) {
        if (isValidUnsignedByte(number)) {
            pack[startOffset] = (byte) number;
            return true;
        }
        else {
            pack[startOffset] = (byte) invalidValue;
            return false;
        }
    }

    public static int decodeUnsigned(byte buffer[], int startOffset) {
        return BitwiseUtil.getUnsigned(buffer[startOffset]);
    }

    public static int encodeShort(byte[] buffer, int startOffset, int number, int invalid) {
        if (SHORT_RANGE.containsInteger(number)) {
            BitwiseUtil.setBytes(number, 2, buffer, startOffset);
            return number;
        }
        else {
            BitwiseUtil.setBytes(invalid, 2, buffer, startOffset);
            return invalid;
        }
    }

    public static int decodeShort(byte buffer[], int startOffset) {
        return BitwiseUtil.getShort(buffer, startOffset);
    }

    public static int encodeString(byte[] pack, int startOffset, String string, boolean big) {
        return encodeString(pack, startOffset, string, bigPacketLength - startOffset - 1, big);
    }

    public static int encodeString(byte[] pack, int startOffset, String string, int maxLength) {
        return encodeString(pack, startOffset, string, maxLength, false);
    }

    public static int encodeString(byte[] pack, int startOffset, String string, int maxLength, boolean longString) {
        int offset = startOffset;
        if (StringUtils.isEmpty(string)) {
            pack[offset] = 0; //Zero string length
            offset += longString? 2 : 1;
        }
        else {
            byte[] bytes = string.getBytes(Charset.forName("ISO-8859-1"));
            int max = Math.min(maxLength, longString? SHORT_MAX : UBYTE_MAX);
            int stringLength = Math.min(max, bytes.length);
            if (longString) {
                encodeShort(pack, offset, stringLength, 0);
                offset += 2;
            }
            else {
                pack[offset] = (byte) stringLength;
                offset++;
            }
            System.arraycopy(bytes, 0, pack, offset, stringLength);
            offset += stringLength;
        }
        return offset;
    }

    public static String decodeString(byte[] pack, int startOffset) {
        return decodeString(pack, startOffset, false);
    }

    public static String decodeString(byte[] pack, int startOffset, boolean longString) {
        int offset = startOffset;
        int length;
        if (longString) {
            length = decodeShort(pack, offset);
            offset += 2;
        }
        else {
            length = decodeUnsigned(pack, offset);
            offset++;
        }

        try {
            return new String(pack, offset, length, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException ex) {
            return StringUtils.EMPTY;
        }
    }

    public static int encodedLength(String str, boolean longStr) {
        return (longStr? 2 : 1) + (str == null ? 0 : str.length());
    }
}

/*
 * 
 */

package com.stayprime.util;

/**
 *
 * @author benjamin
 */
public class BitwiseUtil {
    public static void setBytes(long value, int count, byte dest[], int offset) {
	for(int i = 0; i < count; i++) {
	    dest[i + offset] = (byte) ((value >> (count - 1 - i) * 8) & 0xFF);
	}
    }

    public static long getValue(int count, byte src[], int offset) {
	long l = 0;
	for(int i = 0; i < count; i++) {
	    l |= ( ((long) src[i + offset] & 0xFF) << (count - 1 - i) * 8);
	}
	return l;
    }

    public static long getLong(byte src[], int offset) {
	return getValue(8, src, offset);
    }

    public static int getInt(byte src[], int offset) {
	return (int) getValue(4, src, offset);
    }

    public static short getShort(byte src[], int offset) {
	return (short) getValue(2, src, offset);
    }

    public static short getUnsigned(byte b) {
	return (short) (b & 0xFF);
    }
}

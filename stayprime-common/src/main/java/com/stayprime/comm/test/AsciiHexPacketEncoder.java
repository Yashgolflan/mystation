/*
 *
 */
package com.stayprime.comm.test;

import com.stayprime.comm.BytePacket;
import com.stayprime.comm.encoder.CartPacketEncoder;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public class AsciiHexPacketEncoder implements CartPacketEncoder {
    private CartPacketEncoder encoder;
    private BytePacket packet;

    public AsciiHexPacketEncoder(CartPacketEncoder encoder) {
	this.encoder = encoder;
	packet = new BytePacket(new byte[1024]);
    }

    @Override
    public void setId(int id) {
	encoder.setId(id);
    }

    @Override
    public void reset() {
	encoder.reset();
    }

    @Override
    public boolean isPacketReady() {
	return encoder.isPacketReady();
    }

    @Override
    public boolean encodePosition(Point2D position, float bearing) {
	return encoder.encodePosition(position, bearing);
    }

    @Override
    public boolean encodeGameStatus(int playingHoleNumber, int paceOfPlaySeconds) {
	return encoder.encodeGameStatus(playingHoleNumber, paceOfPlaySeconds);
    }

    public boolean encodeCartAheadRequest(int holeNumber) {
	return encoder.encodeCartAheadRequest(holeNumber);
    }

    @Override
    public BytePacket getPacket() {
	BytePacket p = encoder.getPacket();
	byte[] b = p.getPacket();

	if(p == null)
	    return null;

	packet.setLength(p.getLength()*3);

	int offset = 0;
	int len = p.getLength();

	for(int i = 0; i < len; i++) {
	    String hex = Integer.toHexString(b[offset + i]);
	    int hexLen = hex.length();

	    if(hexLen == 1)
		packet.set(i*3, '0');
	    else
		packet.set(i*3, hex.charAt(hexLen - 2));

	    packet.set(i*3 + 1, hex.charAt(hexLen - 1));
	    packet.set(i*3 + 2, ' ');
	}

	packet.set(p.getLength()*3 - 1, '\n');

	return packet;
    }

    public boolean encodeCartStatus(int status) {
        return encoder.encodeCartStatus(status);
    }

}

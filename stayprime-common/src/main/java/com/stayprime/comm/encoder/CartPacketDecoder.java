/*
 * 
 */
package com.stayprime.comm.encoder;

import com.stayprime.comm.BytePacket;
import java.awt.geom.Point2D;

/**
 *
 * @author benjamin
 */
public interface CartPacketDecoder {
    public void reset();
    public void decodePacket(BytePacket bytePacket);

    public int getId();

    public boolean isPositionDecoded();
    public Point2D getPosition();
    public float getBearing();

    public boolean isGameStatusDecoded();
    public int getHoleNumber();
    public int getPaceOfPlay();

    public boolean isPreMessageDecoded();
    public int getPreMessageId();

    public boolean isCartAheadDecoded();
    public int getCartAheadHoleNumber();
    
    public boolean isCartStatusDecoded();
    public int getCartAppMode();
}

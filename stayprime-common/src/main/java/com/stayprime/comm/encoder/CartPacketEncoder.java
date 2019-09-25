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
public interface CartPacketEncoder {

    public void reset();

    public void setId(int id);

    public boolean encodePosition(Point2D position, float bearing);

    public boolean encodeGameStatus(int playingHoleNumber, int paceOfPlaySeconds);
    
    public boolean encodeCartAheadRequest(int holeNumber);

    public boolean encodeCartStatus(int status); 

    public boolean isPacketReady();

    public BytePacket getPacket();

}

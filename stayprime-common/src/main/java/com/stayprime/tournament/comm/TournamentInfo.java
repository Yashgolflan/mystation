/*
 * 
 */
package com.stayprime.tournament.comm;

import com.stayprime.comm.encoder.EncodeUtil;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.golf.message.Payload;

/**
 *
 * @author benjamin
 */
public class TournamentInfo implements Payload {

    private String extId;

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.TMT_REQUEST;
    }

    @Override
    public int encode(byte[] pack, int offset) {
        return EncodeUtil.encodeString(pack, offset, extId, false);
    }

}

/*
 * 
 */
package com.stayprime.golf.message;

import com.stayprime.model.golf.HoleCartList;

/**
 *
 * @author benjamin
 */
public class GolfCartListMessage {
    private HoleCartList golfCartList;

    public GolfCartListMessage() {
    }

    public void setGolfCartList(HoleCartList golfCartList) {
        this.golfCartList = golfCartList;
    }

    public HoleCartList getGolfCartList() {
        return golfCartList;
    }

}

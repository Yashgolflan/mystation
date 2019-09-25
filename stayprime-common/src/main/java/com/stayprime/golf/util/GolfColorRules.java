/*
 * 
 */
package com.stayprime.golf.util;

import com.stayprime.model.golf.GolfCart;
import com.stayprime.model.golf.GolfRound;
import java.awt.Color;

/**
 *
 * @author benjamin
 */
public class GolfColorRules {
    private Color notPlaying;
    private Color onPace;
    private Color behindPaceCaution;
    private Color behindPaceWarning;

    public GolfColorRules() {
        this(0);
    }

    public GolfColorRules(int baseBrightness) {
        notPlaying = Color.white;
        onPace = new Color(baseBrightness, 255, baseBrightness);
        behindPaceCaution = new Color(255, 200, baseBrightness);
        behindPaceWarning = new Color(255, baseBrightness, baseBrightness);
    }

    public Color getColor(GolfCart cart) {
        GolfRound round = cart.getGolfRound();

        if (round == null || round.isStarted() == false) {
            return notPlaying;
        }
        else if(round.getPaceOfPlay() < -600) {
            return behindPaceWarning;
        }
        else if(round.getPaceOfPlay() < 0) {
            return behindPaceCaution;
        }
        else {
            return onPace;
        }
    }
}

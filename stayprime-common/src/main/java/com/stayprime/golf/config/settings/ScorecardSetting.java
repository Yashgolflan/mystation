/*
 * 
 */
package com.stayprime.golf.config.settings;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class ScorecardSetting extends Setting {
    private Mode scorecardMode = Mode.disable;
    private boolean scorecardEmail;
    private boolean scorecardPrint;

    public ScorecardSetting(String key) {
        super(key);
    }

    public ScorecardSetting(String key, String value) {
        super(key, value);
    }

    public void setScorecard(String scorecard) {
        //Unset boolean options
        scorecardEmail = false;
        scorecardPrint = false;

        if (scorecard == null || Mode.disable.name().equals(scorecard)) {
            super.set(Mode.disable.name());
            scorecardMode = Mode.disable;
        }
        else {
            try {
                String[] scSplit = StringUtils.split(scorecard, ',');
                Mode mode = Mode.disable;
                for (String opt: scSplit) {
                    if (opt.equals(Mode.auto.name())) {
                        mode = Mode.enable(mode);
                    }
                    if (opt.equals(Mode.enable.name())) {
                        mode = Mode.enable(mode);
                    }
                    else if (opt.equals(ScorecardOptions.email.name())) {
                        scorecardEmail = true;
                    }
                    else if (opt.equals(ScorecardOptions.print.name())) {
                        scorecardPrint = true;
                    }
                }
                scorecardMode = mode;
                set(scorecard);
            }
            catch (Exception ex) {
            }
        }
    }

    public Mode getScorecardMode() {
        return scorecardMode;
    }

    public boolean isScorecardEmail() {
        return scorecardEmail;
    }

    public boolean isScorecardPrint() {
        return scorecardPrint;
    }

}

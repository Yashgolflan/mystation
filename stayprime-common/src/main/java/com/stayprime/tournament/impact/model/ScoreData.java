package com.stayprime.tournament.impact.model;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class ScoreData {
    private String tournamentId;
    private String RoundNo;
    private String EntryId;
    private String PlayerOrTeamId;
    private int scores[];
    private String Rev;

    public ScoreData() {
        scores = new int[18];
    }

    /**
     *
     * @return The RoundNo
     */
    public String getRoundNo() {
        return RoundNo;
    }

    /**
     *
     * @param RoundNo The RoundNo
     */
    public void setRoundNo(String RoundNo) {
        this.RoundNo = RoundNo;
    }

    /**
     *
     * @return The EntryId
     */
    public String getEntryId() {
        return EntryId;
    }

    /**
     *
     * @param EntryId The EntryId
     */
    public void setEntryId(String EntryId) {
        this.EntryId = EntryId;
    }

    /**
     *
     * @return The PlayerOrTeamId
     */
    public String getPlayerOrTeamId() {
        return PlayerOrTeamId;
    }

    /**
     *
     * @param PlayerOrTeamId The PlayerOrTeamId
     */
    public void setPlayerOrTeamId(String PlayerOrTeamId) {
        this.PlayerOrTeamId = PlayerOrTeamId;
    }

    public int getScore(int i) {
        return scores[i];
    }

    public void setScore(int i, int score) {
        scores[i] = score;
    }

    public String getHole1() {
        return Integer.toString(scores[0]);
    }

    public void setHole1(String Hole1) {
        this.scores[0] = NumberUtils.toInt(Hole1);
    }

    public String getHole2() {
        return Integer.toString(scores[1]);
    }

    public void setHole2(String Hole2) {
        this.scores[1] = NumberUtils.toInt(Hole2);
    }

    public String getHole3() {
        return Integer.toString(scores[2]);
    }

    public void setHole3(String Hole3) {
        this.scores[2] = NumberUtils.toInt(Hole3);
    }

    public String getHole4() {
        return Integer.toString(scores[3]);
    }

    public void setHole4(String Hole4) {
        this.scores[3] = NumberUtils.toInt(Hole4);
    }

    public String getHole5() {
        return Integer.toString(scores[4]);
    }

    public void setHole5(String Hole5) {
        this.scores[4] = NumberUtils.toInt(Hole5);
    }

    public String getHole6() {
        return Integer.toString(scores[5]);
    }

    public void setHole6(String Hole6) {
        this.scores[5] = NumberUtils.toInt(Hole6);
    }

    public String getHole7() {
        return Integer.toString(scores[6]);
    }

    public void setHole7(String Hole7) {
        this.scores[6] = NumberUtils.toInt(Hole7);
    }

    public String getHole8() {
        return Integer.toString(scores[7]);
    }

    public void setHole8(String Hole8) {
        this.scores[7] = NumberUtils.toInt(Hole8);
    }

    public String getHole9() {
        return Integer.toString(scores[8]);
    }

    public void setHole9(String Hole9) {
        this.scores[8] = NumberUtils.toInt(Hole9);
    }

    public String getHole10() {
        return Integer.toString(scores[9]);
    }

    public void setHole10(String Hole10) {
        this.scores[9] = NumberUtils.toInt(Hole10);
    }

    public String getHole11() {
        return Integer.toString(scores[10]);
    }

    public void setHole11(String Hole11) {
        this.scores[10] = NumberUtils.toInt(Hole11);
    }

    public String getHole12() {
        return Integer.toString(scores[11]);
    }

    public void setHole12(String Hole12) {
        this.scores[11] = NumberUtils.toInt(Hole12);
    }

    public String getHole13() {
        return Integer.toString(scores[12]);
    }

    public void setHole13(String Hole13) {
        this.scores[12] = NumberUtils.toInt(Hole13);
    }

    public String getHole14() {
        return Integer.toString(scores[13]);
    }

    public void setHole14(String Hole14) {
        this.scores[13] = NumberUtils.toInt(Hole14);
    }

    public String getHole15() {
        return Integer.toString(scores[14]);
    }

    public void setHole15(String Hole15) {
        this.scores[14] = NumberUtils.toInt(Hole15);
    }

    public String getHole16() {
        return Integer.toString(scores[15]);
    }

    public void setHole16(String Hole16) {
        this.scores[15] = NumberUtils.toInt(Hole16);
    }

    public String getHole17() {
        return Integer.toString(scores[16]);
    }

    public void setHole17(String Hole17) {
        this.scores[16] = NumberUtils.toInt(Hole17);
    }

    public String getHole18() {
        return Integer.toString(scores[17]);
    }

    public void setHole18(String Hole18) {
        this.scores[17] = NumberUtils.toInt(Hole18);
    }    
    /**
     *
     * @return The Rev
     */
    public String getRev() {
        return Rev;
    }

    /**
     *
     * @param Rev The Rev
     */
    public void setRev(String Rev) {
        this.Rev = Rev;
    }
    
    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Override
    public String toString() {
        return "ScoreData{" + "RoundNo=" + RoundNo+ ", EntryId=" + EntryId
                + ", PlayerOrTeamId=" + PlayerOrTeamId + ", Rev=" + Rev
                + "," + StringUtils.join(ArrayUtils.toObject(scores), ',')
                + '}';
    }

    public int getCount() {
        return 18;
    }
    
}

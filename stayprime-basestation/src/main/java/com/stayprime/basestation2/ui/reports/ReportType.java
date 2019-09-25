/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.ui.reports;

/**
 *
 * @author Omer
 */
public enum ReportType {

    FNB(1),
    ROUNDS(2),
    SCORECARDS(3);

    public final int id;

    ReportType(int id) {
        this.id = id;
    }

    public int ReportType() {
        return this.id;
    }

}



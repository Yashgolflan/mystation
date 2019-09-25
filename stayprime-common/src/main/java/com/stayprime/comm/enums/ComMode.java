/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.comm.enums;

/**
 *
 * @author Omer
 */
public enum ComMode {
    
    MOBILE(1);
    //PRINTER(2);
    
    public final int id;

    ComMode(int id) {
        this.id = id;
    }

    public int getComMode() {
        return this.id;
    }
}

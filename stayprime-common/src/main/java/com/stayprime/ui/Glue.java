/*
 * 
 */

package com.stayprime.ui;

import java.awt.Dimension;
import javax.swing.Box;

/**
 *
 * @author benjamin
 */
public class Glue extends Box.Filler {

    public Glue() {
	super(new Dimension(0,0), new Dimension(0,0), new Dimension(Short.MAX_VALUE, 0));
	setLayout(null);
    }
    
}

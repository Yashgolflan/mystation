/*
 * 
 */

package com.stayprime.basestation2.ui.custom;

import java.awt.ComponentOrientation;
import javax.swing.Icon;

/**
 *
 * @author benjamin
 */
public class JXTitledSeparator extends org.jdesktop.swingx.JXTitledSeparator{
    private boolean init = false;

    public JXTitledSeparator() {
        super();
        init = true;
    }

    public JXTitledSeparator(String title) {
        super(title);
        init = true;
    }

    public JXTitledSeparator(String title, int horizontalAlignment) {
        super(title, horizontalAlignment);
        init = true;
    }

    public JXTitledSeparator(String title, int horizontalAlignment, Icon icon) {
        super(title, horizontalAlignment, icon);
        init = true;
    }

    @Override
    public ComponentOrientation getComponentOrientation() {
        if(!init)
            return ComponentOrientation.UNKNOWN;
        else
            return super.getComponentOrientation();
    }

}

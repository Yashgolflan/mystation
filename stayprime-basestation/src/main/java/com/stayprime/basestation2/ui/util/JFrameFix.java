/*
 * 
 */
package com.stayprime.basestation2.ui.custom;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author benjamin
 */
public class JFrameFix extends JFrame {

    public JFrameFix(String title) {
        super(title);
    }

    @Override
    public void setExtendedState(final int state) {
        if (state == MAXIMIZED_BOTH) {
            setLocation(0, 0);
            super.setExtendedState(state);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrameFix.super.setExtendedState(state);
                }
            });
        }
    }

}

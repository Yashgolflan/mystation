/*
 * 
 */

package com.stayprime.basestation2.instance;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.instance.ApplicationInstanceListener;
import java.awt.EventQueue;
import java.awt.Frame;
import javax.swing.JFrame;

/**
 *
 * @author benjamin
 */
public class InstanceListener implements ApplicationInstanceListener {

    public InstanceListener() {
    }

    @Override
    public void newInstanceCreated() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame mainFrame = BaseStation2App.getApplication().getMainFrame();
                if (mainFrame.getState() == Frame.ICONIFIED)
                    mainFrame.setExtendedState(mainFrame.getExtendedState() & ~Frame.ICONIFIED);
                mainFrame.toFront();
            }
        });
    }
    
}

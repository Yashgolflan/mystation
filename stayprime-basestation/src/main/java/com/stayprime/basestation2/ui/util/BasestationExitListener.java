/*
 * 
 */
package com.stayprime.basestation2.ui.util;

import com.stayprime.basestation2.BaseStation2App;
import java.util.EventObject;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;

/**
 *
 * @author benjamin
 */
public class BasestationExitListener implements Application.ExitListener {

    @Override
    public boolean canExit(EventObject eo) {
        JFrame frame = BaseStation2App.getApplication().getMainFrame();
        String title = "Exit Confirmation";
        String msg = "Do you really want to close this Application?";
        int confirm = JOptionPane.showConfirmDialog(frame, msg, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return confirm == JOptionPane.YES_OPTION;
    }

    @Override
    public void willExit(EventObject eo) {
        BaseStation2App.getApplication().stop();
    }
    
}

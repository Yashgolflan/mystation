/*
 * 
 */

package com.stayprime.ui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;

/**
 *
 * @author benjamin
 */
public class SwingAction extends AbstractAction implements Action {
    private Action action;

    public SwingAction(Action action) {
	this.action = action;
	super.putValue(NAME, action.getDisplayName());
	super.putValue(ACTION_COMMAND_KEY, action.getName());
    }

    public void actionPerformed(ActionEvent e) {
	performAction();
    }

    public String getName() {
	return action.getName();
    }

    public String getDisplayName() {
	return action.getDisplayName();
    }

    public void performAction() {
	action.performAction();
    }

}

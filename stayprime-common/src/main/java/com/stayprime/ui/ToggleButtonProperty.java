/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui;

/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

import java.awt.Component;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import org.jdesktop.application.session.PropertySupport;
import org.jdesktop.application.session.SplitPaneState;
import org.jdesktop.application.session.WindowProperty;

/**
 * A {@code sessionState} property for JSplitPane.
 * <p>
 * This class defines how the session state for {@code JSplitPanes}
 * is {@link WindowProperty#getSessionState saved} and
 * and {@link WindowProperty#setSessionState restored} in
 * terms of a property called {@code sessionState}.  The
 * JSplitPane's {@code dividerLocation} is saved and restored
 * if its {@code orientation} hasn't changed.
 * <p>
 * {@code SplitPaneProperty} is registered for {@code
 * JSplitPane.class} by default, so this class applies to
 * JSplitPane and any subclass of JSplitPane.  One can
 * override the default with the {@link org.jdesktop.application.SessionStorage#putProperty putProperty}
 * method.
 *
 * @see SplitPaneState
 * @see org.jdesktop.application.SessionStorage#save
 * @see org.jdesktop.application.SessionStorage#restore
 */
public class ToggleButtonProperty implements PropertySupport {

    private void checkComponent(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("null component");
        }
        if (!(component instanceof JToggleButton)) {
            throw new IllegalArgumentException("invalid component");
        }
    }

    /**
     * Returns a {@link SplitPaneState SplitPaneState} object
     * for {@code JSplitPane c}.  If the split pane's
     * {@code dividerLocation} is -1, indicating that either
     * the divider hasn't been moved, or it's been reset,
     * then return null.
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code Component c}
     * isn't a non-null {@code JSplitPane}.
     *
     * @param c the {@code JSplitPane} whose dividerLocation will
     *     recoreded in a {@code SplitPaneState} object.
     * @return the {@code SplitPaneState} object
     * @see #setSessionState
     * @see SplitPaneState
     */
    @Override
    public Object getSessionState(Component c) {
        checkComponent(c);
        JToggleButton p = (JToggleButton) c;
        return new ToggleButtonState(p.isSelected());
    }

    /**
     * Restore the {@code JSplitPane's} {@code dividerLocation}
     * property if its {@link JSplitPane#getOrientation orientation}
     * has not changed.
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code c} is
     * not a {@code JSplitPane} or if {@code state} is non-null
     * but not an instance of {@link SplitPaneState}.
     *
     * @param c the JSplitPane whose state is to be restored
     * @param state the {@code SplitPaneState} to be restored
     * @see #getSessionState
     * @see SplitPaneState
     */
    @Override
    public void setSessionState(Component c, Object state) {
        checkComponent(c);
        if (state == null) return;
        if (state instanceof ToggleButtonState) {
            JToggleButton b = (JToggleButton) c;
            ToggleButtonState tbs = (ToggleButtonState) state;
            b.setSelected(tbs.isSelected());
        }
    }
}

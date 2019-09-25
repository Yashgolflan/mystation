/*
 * 
 */
package com.stayprime.basestation2.ui.util;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationAction;
import org.jdesktop.application.ApplicationContext;

/**
 *
 * @author benjamin
 */
public class ApplicationUtil {

    public static ApplicationAction getAction(Object actionsClass, String actionName) {
        ApplicationContext context = Application.getInstance().getContext();
        return (ApplicationAction) context.getActionMap(actionsClass).get(actionName);
    }

}

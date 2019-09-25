/*
 * 
 */

package com.stayprime.basestation2.util;

import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation2.ui.modules.OnCourseAdsStorageTasks;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Application;

/**
 *
 * @author benjamin
 * @param <T> type of the retrieved object, if any.
 */
public abstract class ApplicationTask<T> extends org.jdesktop.application.Task<T, Void> {
    private static final Logger log = LoggerFactory.getLogger(OnCourseAdsStorageTasks.class);
    private final String className = getClass().getSimpleName();
    private Exception exception;
    private T result;

    public ApplicationTask() {
        super(Application.getInstance());
        log.debug("Creating task " + className);
    }

    @Override
    protected T doInBackground() {
        log.debug(className + ".doInBackground()");
        try {
            return performTask();
        }
        catch (Exception ex) {
            RuntimeException re = new RuntimeException(className + ": " + ex, ex);
            log.error(re.toString());
            exception = ex;
        }
        return null;
    }

    @Override
    protected void succeeded(T result) {
        log.debug(className + ".succeeded()");
        if (exception != null) {
//            TaskDialogs.showException(exception);
            NotificationPopup.showErrorPopup(null);
        }
        else {
            this.result = result;
            succeeded();
        }
    }

    public T getResult() {
        return result;
    }

    protected abstract T performTask();

    protected abstract void succeeded();

}

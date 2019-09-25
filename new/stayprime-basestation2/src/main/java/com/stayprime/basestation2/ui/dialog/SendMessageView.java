/*
 * 
 */
package com.stayprime.basestation2.ui.dialog;

/**
 *
 * @author benjamin
 */
public interface SendMessageView {
    public void setActions(SendMessageActions actions);
    public void setup();
    public void setCartNumber(int cartNumber);
    public void setVisible(boolean visible);
    public void showException(Exception ex);
}

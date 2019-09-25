/*
 * 
 */

package com.aeben.golfcourse.cart;

import com.stayprime.geo.Coordinates;

/**
 *
 * @author benjamin
 */
public interface ObservablePositionInfo extends PositionInfo {

    public void addPositionObserver(Observer observer);

    public void removePositionObserver(Observer observer);

    //Observer interface implementations:
    //StatusUpdateScheduler.CommUpdaterPositionObserver set the radioComm location
    //CartMonitor: set cart status and control cart properties and actions on EDT
    //DiagnosticPanelPresentation: update cart status, only when it's shown
    //GameInfoPresentationImpl: update distances and labels
    //MainGamePresentationImpl: show or hide full screen ads
    //MainGamePanel.PositionObserverImpl: repaint map, auto zoom
    public static interface Observer {
	public void positionUpdated(PositionInfo position, Coordinates oldPosition);
    }

}

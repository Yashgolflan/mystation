package com.aeben.golfcourse.cart;

import com.stayprime.geo.Coordinates;
import com.stayprime.device.gps.PositioningSystem;

public final class NullPositioningSystem implements PositioningSystem {
    public NullPositioningSystem() {
    }

    public boolean isPositionValid() {
	return false;
    }

    public boolean isPositionGood() {
        return false;
    }

    public Coordinates getCoordinates() {
	return null;
    }

    public float getSpeed() {
	return 0f;
    }

    public float getHeading() {
	return 0f;
    }

    public void addPositionObserver(ObservablePositionInfo.Observer observer) {
    }

    public void removePositionObserver(ObservablePositionInfo.Observer observer) {
    }

    public void start() {
    }

    public void stop() {
    }

    public void reset() {
    }
}

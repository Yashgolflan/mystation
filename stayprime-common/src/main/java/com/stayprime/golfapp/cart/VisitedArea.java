/*
 * 
 */

package com.stayprime.golfapp.cart;

import com.stayprime.golf.objects.BasicArea;

/**
 *
 * @author benjamin
 * @param <T>
 */
public final class VisitedArea<T extends BasicArea> {
    private final T area;
    private long lastVisited = 0;

    public VisitedArea(T area, long lastVisited) {
	this.area = area;
	this.lastVisited = lastVisited;
    }

    public T getArea() {
	return area;
    }

    public long getLastVisited() {
	return lastVisited;
    }

    public void setLastVisited(long time) {
	lastVisited = time;
    }

}

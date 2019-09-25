/*
 * 
 */
package com.stayprime.golf.objects;

/**
 *
 * @author benjamin
 */
public enum GeomType {
    POINT(1),
    POLY(2),
    POLYLINE(3),
    POINTLIST(4);

    public final int id;

    private GeomType(int id) {
        this.id = id;
    }

}

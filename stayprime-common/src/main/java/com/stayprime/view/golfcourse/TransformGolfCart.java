/*
 * 
 */
package com.stayprime.view.golfcourse;

import com.stayprime.model.golf.GolfCart;
import com.stayprime.model.golf.Position;
import com.stayprime.view.TransformView;
import com.stayprime.view.map.MapView;

/**
 *
 * @author benjamin
 */
public class TransformGolfCart extends GolfCart {
    private TransformView transformFactory;
    private MapView mapView;

    public TransformGolfCart(TransformView t, MapView m, Integer cartNumber, Position position, double heading) {
        super();
        setNumber(cartNumber);
        setPosition(position);
        setHeading(heading);
        this.transformFactory = t;
        this.mapView = m;
    }

    @Override
    public double getHeading() {
        Double r = transformFactory.getRotation();
        return super.getHeading() - Math.toDegrees(mapView.getMapAngle() + (r == null? 0 : r));
    }

}

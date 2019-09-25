/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.geo;

import com.stayprime.golf.course.objects.ObjectType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Transient;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author benjamin
 */
public class BasicMapImage {
    private static final int topLeft = 0;
    private static final int topRight = 1;
    private static final int bottomLeft = 3;
    private static final int bottomRight = 2;

    @Column
    private String mapImage;

    @Transient
    private List<Coordinates> shape;

    public BasicMapImage() {
        setMapCorners(null);
    }

    public BasicMapImage(String imagePath, Coordinates topLeft, Coordinates topRight,
            Coordinates bottomLeft, Coordinates bottomRight) {
        this.mapImage = imagePath;
	shape = new ArrayList<Coordinates>(4);
	shape.add(topLeft);
	shape.add(topRight);
	shape.add(bottomRight);
	shape.add(bottomLeft);
    }

    public BasicMapImage(BasicMapImage map) {
        if (map != null) {
            this.mapImage = map.getImageAddress();
            shape = new ArrayList<Coordinates>(4);
            shape.add(map.getTopLeft());
            shape.add(map.getTopRight());
            shape.add(map.getBottomRight());
            shape.add(map.getBottomLeft());
        }
    }

    @Column @Access(AccessType.PROPERTY)
    protected String getMapCorners() {
        return StringUtils.join(shape, ';');
    }

    protected void setMapCorners(String map_corners) {
        shape = new ArrayList<Coordinates>(4);
        if (StringUtils.isNotEmpty(map_corners)) {
            String[] s = StringUtils.split(map_corners, ';');

            for (int i = 0; i < 4; i++) {
                if (s.length > i && StringUtils.isNotBlank(s[i]))  {
                    shape.add(new Coordinates(s[i]));
                }
                else {
                    shape.add(null);
                }
            }
        }
    }

    public String getImageAddress() {
        return mapImage;
    }

    public void setImageAddress(String imageAddress) {
	this.mapImage = imageAddress;
    }

    public Coordinates getTopLeft() {
        return shape.get(topLeft);
    }

    public void setTopLeft(Coordinates c) {
	shape.set(topLeft, c);
    }

    public Coordinates getTopRight() {
        return shape.get(topRight);
    }

    public void setTopRight(Coordinates c) {
	shape.set(topRight, c);
    }

    public Coordinates getBottomLeft() {
        return shape.get(bottomLeft);
    }

    public void setBottomLeft(Coordinates c) {
	shape.set(bottomLeft, c);
    }

    public Coordinates getBottomRight() {
        return shape.get(bottomRight);
    }

    public void setBottomRight(Coordinates c) {
	shape.set(bottomRight, c);
    }

    public boolean hasFourCornerCoordinates() {
        return getTopLeft() != null
                && getTopRight() != null
                && getBottomLeft() != null
                && getBottomRight() != null;
    }

    public boolean contains(Coordinates c) {
        return c != null && contains(c.latitude, c.longitude);
    }

    public boolean contains(double lat, double lon) {
	return hasFourCornerCoordinates()
		&& CoordinateCalculations.shapeContains(shape, lat, lon);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 37 * hash + (this.mapImage != null ? this.mapImage.hashCode() : 0);
	hash = 37 * hash + (getTopLeft() != null ? getTopLeft().hashCode() : 0);
	hash = 37 * hash + (getTopRight() != null ? getTopRight().hashCode() : 0);
	hash = 37 * hash + (getBottomLeft() != null ? getBottomLeft().hashCode() : 0);
	hash = 37 * hash + (getBottomRight() != null ? getBottomRight().hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof BasicMapImage) {
            BasicMapImage map = (BasicMapImage) o;
            return ObjectUtils.equals(map.getImageAddress(), getImageAddress())
                    && ObjectUtils.equals(map.getTopLeft(), getTopLeft())
                    && ObjectUtils.equals(map.getTopRight(), getTopRight())
                    && ObjectUtils.equals(map.getBottomLeft(), getBottomLeft())
                    && ObjectUtils.equals(map.getBottomRight(), getBottomRight());
        }
        else
            return false;
    }

    public Integer getId() {
        return 0;
    }

    public String getName() {
        return null;
    }

    public ObjectType getType() {
        return ObjectType.UNKNOWN;
    }

    public String getMapCoordinates() {
        if (hasFourCornerCoordinates()) {
            return StringUtils.join(shape, ';');
        }
        return null;
    }
}

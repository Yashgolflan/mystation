/*
 * 
 */
package com.stayprime.view.golfcourse;

import com.stayprime.golf.course.GolfCourse;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.Site;
import com.stayprime.golf.course.objects.ObjectType;
import com.stayprime.golf.objects.AbstractFeature;
import com.stayprime.golf.objects.BasicArea;
import com.stayprime.view.TransformView;
import com.stayprime.view.map.MapView;
import java.awt.Color;

/**
 *
 * @author benjamin
 */
public class GolfMapView extends GolfObjectsView {
    private Site golfClub;
    private Color[] colors;
    private Color nullFillColor;
    private Color nullStrokeColor;

    public GolfMapView(MapView mapView, TransformView transformView) {
        super(mapView, transformView);

        golfRenderer.loadPinFlagImage();
        initColors();
    }

    /**
     * Set the current hole map and recalculate all the objects positions.
     * If the site is null it will clear the map and the objects.
     * @param golfClub the current hole map.
     */
    public void setGolfClub(Site golfClub) {
        this.golfClub = golfClub;

        if (golfClub != null) {
            setMap(golfClub.getMapImage());
        }
        else {
            setMap(null);
        }

        setCourseObjects();
    }

    /**
     * Load all the hole objects and convert them to transform view objects.
     * If the hole is null it will clear the objects.
     */
    private void setCourseObjects() {
        shapes.clear();

        if(golfClub != null && isValidMap()) {
            for(BasicArea e: golfClub.getClubhouseZones()) {
                addAreaPolygon(e.getShape(), Color.yellow, Color.yellow);
            }
            for(BasicArea e: golfClub.getWarningZones()) {
                addAreaPolygon(e.getShape(), Color.red, Color.red);
            }

            for (GolfCourse course: golfClub.getCourses()) {
//                course.getFeatures();
                for (GolfHole hole: course.getHoles()) {
                    for (AbstractFeature f: hole.getFeaturesList()) {
                        addAreaPolygon(f.getShape(), getFillColor(f), getStrokeColor(f));
                    }
                }
            }
        }
    }

    private void initColors() {
        colors = new Color[ObjectType.maxTypeCount];
        nullFillColor = new Color(0x22888888, true);
        nullStrokeColor = new Color(0x888888);
    }

    private Color getStrokeColor(AbstractFeature f) {
        ObjectType type = f.getType();
        if (type != null) {
            return type.getDefaultColor();
        }
        return nullStrokeColor;
    }

    private Color getFillColor(AbstractFeature f) {
        ObjectType type = f.getType();
        if (type != null) {
            int id = type.getId();
            if (colors[id] == null) {
                Color c = type.getDefaultColor();
                colors[id] = new Color(c.getRGB() & 0x00ffffff | 0x44000000, true);
            }
            return colors[id];
        }
        return nullFillColor;
    }
    
}

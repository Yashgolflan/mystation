/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.util;

import com.stayprime.geo.Coordinates;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class XmlLoaderUtil {

    public static String getShapeString(List<Coordinates> shapeCoordinates) {
        String shapeString = null;
        if (shapeCoordinates != null) {
            StringBuilder builder = new StringBuilder();
            for (Coordinates coord : shapeCoordinates) {
                builder.append(coord.toString());
                builder.append(';');
            }
            shapeString = builder.toString();
        }
        return shapeString;
    }

    public static List<Coordinates> getShape(String shapeString) {
        List<Coordinates> shape = null;
        if (StringUtils.isNotBlank(shapeString)) {
            String[] coords = shapeString.split(";");
            shape = new ArrayList<Coordinates>(coords.length);
            for (String coord : coords) {
                shape.add(new Coordinates(coord.trim()));
            }
        }
        else {
            shape = Collections.EMPTY_LIST;
        }
        return shape;
    }

    public static Color getColor(String colorValue, String alphaValue) {
        if (colorValue != null) {
            //Only load the color, not the alpha value.
            //Alpha will be set globally for all zones.
            int color = NumberUtils.toInt(colorValue);
            return new Color(color);
            //	    boolean hasAlpha = (color & 0xFF000000) != 0;
            //
            //	    if(alphaValue != null) {
            //		float shapeAlpha = NumberUtils.toFloat(alphaValue, 1f);
            //		color = color & 0xffffff | (int)(shapeAlpha*255) << 24;
            //		hasAlpha = true;
            //	    }
            //
            //	    return new Color(color, hasAlpha);
        }
        return null;
    }
    
}

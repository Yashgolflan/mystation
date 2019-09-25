/*
 * 
 */

package com.stayprime.golf.objects;

import com.stayprime.geo.CoordinateCalculations;
import com.stayprime.geo.Coordinates;
import com.stayprime.golf.course.GolfHole;
import com.stayprime.golf.course.objects.ObjectType;
import com.stayprime.util.ConfigUtil;
import com.stayprime.util.gson.Exclude;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
//@Embeddable
//@Entity
@Table(name = "golf_hole_feature")
public class AbstractFeature {
    @Id @GeneratedValue
    private Integer featureId;

    @Exclude
    @ManyToOne
    @JoinColumn(name = "hole_id")
    private GolfHole golfHole;

    private String name;

    @Enumerated(EnumType.STRING)
    private ObjectType type;

    @Enumerated(EnumType.STRING)
    private GeomType geometryType;

    @Transient
    protected Properties properties;

    @Transient
    private Color color;

    @Transient
    private List<Coordinates> shape;

    public AbstractFeature() {
        this(ObjectType.UNKNOWN, GeomType.POLY, null);
    }

    public AbstractFeature(ObjectType type, GeomType geomType, String name) {
        this.type = type;
        this.geometryType = geomType;
	this.name = name;
        this.properties = new Properties();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectType getType() {
        return type;
    }

    public GeomType getGeomType() {
        return geometryType;
    }

    public boolean contains(Coordinates coord) {
	if(shape == null)
	    return false;
	else
	    return CoordinateCalculations.shapeContains(shape, coord);
    }

    public List<Coordinates> getShape() {
	return shape;
    }

    public void setShape(List<Coordinates> shape) {
	this.shape = shape;
    }

    public Coordinates getLocation() {
	return CollectionUtils.isEmpty(shape)? null : shape.get(0);
    }

    public void setLocation(Coordinates location) {
        setShape(Collections.singletonList(location));
    }

    public Color getColor() {
	return color;
    }

    public void setColor(Color color) {
	this.color = color;
        if (color != null) {
            properties.setProperty("color", String.valueOf(color.getRGB()));
        }
        else {
            properties.remove("color");
        }
    }

    @Column(name="geometry", length=1024) @Lob
    @Access(AccessType.PROPERTY)
    public String getGeometry() {
	StringBuilder geom = new StringBuilder();
        if (shape != null) {
            for (Coordinates c: shape) {
                geom.append(c.toString()).append(";");
            }
            return geom.toString();
        }
        else {
            return null;
        }
    }

    public void setGeometry(String geom) {
        try {
            setShape(Coordinates.listFromString(geom));
        }
        catch (Exception ex) {
            setShape(null);
        }
    }

    @Access(AccessType.PROPERTY)
    @Column(name="properties")
    public String getProperties() {
	return ConfigUtil.getPropertiesString(properties);
    }

    public void setProperties(String props) {
        this.properties = ConfigUtil.loadPropertiesString(props);
        setProperties();
    }

    public void set(AbstractFeature e) {
	setName(e.getName());
	setShape(new ArrayList(e.getShape()));
        setProperties(e.getProperties());
    }

    private void setProperties() {
        setColorFromProperties();
    }

    private void setColorFromProperties() {
        String colorProperty = properties.getProperty("color");
        setColor(NumberUtils.isNumber(colorProperty)?
                new Color(NumberUtils.toInt(colorProperty), true) : null);
    }

    protected void setPropertyString(String key, Object value) {
        if (value != null) {
            properties.setProperty(key, value.toString());
        }
        else {
            properties.remove(key);
        }
    }

    protected List<Coordinates> getCoordListFromProperties(String prop) {
        try {
            return Coordinates.listFromString(properties.getProperty(prop));
        }
        catch (Exception ex) {
            return null;
        }
    }

}

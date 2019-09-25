package com.aeben.golfclub;

import com.stayprime.util.gson.Exclude;
import java.awt.Color;

public class TeeBox extends GolfCourseObject {

    public String distanceToHole;
    public Color color;
    public String name;
    public Integer id;
    @Exclude
    public HoleDefinition hole;
    public static final GolfCourseObject.ObjectType type = GolfCourseObject.ObjectType.TEE_BOX;

    public TeeBox(String name, Integer id, HoleDefinition hole) {
        this.name = name;
        this.id = id;
        this.hole = hole;
    }

    public TeeBox(Integer id, HoleDefinition hole, String name, Color color, String distanceToHole) {
        this.name = name;
        this.id = id;
        this.hole = hole;
        this.distanceToHole = distanceToHole;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public ObjectType getType() {
        return type;
    }

    public GolfCourseObject getParentObject() {
        return hole;
    }

    public TeeBox clone() {
        TeeBox teeBox = new TeeBox(name, id, hole);
        teeBox.color = this.color;
        teeBox.distanceToHole = this.distanceToHole;
        teeBox.name = this.name;
        return teeBox;
    }
}

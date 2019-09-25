package com.stayprime.hibernate.entities;
// Generated Sep 17, 2014 5:18:02 PM by Hibernate Tools 4.3.1


import javax.persistence.Column;
import javax.persistence.Id;

/**
 * CourseObjects generated by hbm2java
 */
public class CourseObjects  implements java.io.Serializable {
     private int id;
     private int type;
     private String name;
     private String location;
     private String image;
     private String imageCenter;
     private Double imageMeterWidth;
     private Integer imageMinPixelWidth;
     private String shape;
     private Double shapeAlpha;
     private Boolean showDistance;
     private Boolean showName;
     private Boolean showShape;
     private Boolean showCoordinate;
     private Boolean closedShape;
     private Integer color;
     private String hitMessage;
     private Boolean staticObject;

    public CourseObjects() {
    }

	
    public CourseObjects(int id, int type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
    public CourseObjects(int id, int type, String name, String location, String image, String imageCenter, Double imageMeterWidth, Integer imageMinPixelWidth, String shape, Double shapeAlpha, Boolean showDistance, Boolean showName, Boolean showShape, Boolean showCoordinate, Boolean closedShape, Integer color, String hitMessage, Boolean staticObject) {
       this.id = id;
       this.type = type;
       this.name = name;
       this.location = location;
       this.image = image;
       this.imageCenter = imageCenter;
       this.imageMeterWidth = imageMeterWidth;
       this.imageMinPixelWidth = imageMinPixelWidth;
       this.shape = shape;
       this.shapeAlpha = shapeAlpha;
       this.showDistance = showDistance;
       this.showName = showName;
       this.showShape = showShape;
       this.showCoordinate = showCoordinate;
       this.closedShape = closedShape;
       this.color = color;
       this.hitMessage = hitMessage;
       this.staticObject = staticObject;
    }
   
     @Id 

    
    @Column(name="id", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    
    @Column(name="type", nullable=false)
    public int getType() {
        return this.type;
    }
    
    public void setType(int type) {
        this.type = type;
    }

    
    @Column(name="name", nullable=false, length=64)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="location", length=64)
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    
    @Column(name="image")
    public String getImage() {
        return this.image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }

    
    @Column(name="image_center", length=32)
    public String getImageCenter() {
        return this.imageCenter;
    }
    
    public void setImageCenter(String imageCenter) {
        this.imageCenter = imageCenter;
    }

    
    @Column(name="image_meter_width", precision=22, scale=0)
    public Double getImageMeterWidth() {
        return this.imageMeterWidth;
    }
    
    public void setImageMeterWidth(Double imageMeterWidth) {
        this.imageMeterWidth = imageMeterWidth;
    }

    
    @Column(name="image_min_pixel_width")
    public Integer getImageMinPixelWidth() {
        return this.imageMinPixelWidth;
    }
    
    public void setImageMinPixelWidth(Integer imageMinPixelWidth) {
        this.imageMinPixelWidth = imageMinPixelWidth;
    }

    
    @Column(name="shape")
    public String getShape() {
        return this.shape;
    }
    
    public void setShape(String shape) {
        this.shape = shape;
    }

    
    @Column(name="shape_alpha", precision=22, scale=0)
    public Double getShapeAlpha() {
        return this.shapeAlpha;
    }
    
    public void setShapeAlpha(Double shapeAlpha) {
        this.shapeAlpha = shapeAlpha;
    }

    
    @Column(name="show_distance")
    public Boolean getShowDistance() {
        return this.showDistance;
    }
    
    public void setShowDistance(Boolean showDistance) {
        this.showDistance = showDistance;
    }

    
    @Column(name="show_name")
    public Boolean getShowName() {
        return this.showName;
    }
    
    public void setShowName(Boolean showName) {
        this.showName = showName;
    }

    
    @Column(name="show_shape")
    public Boolean getShowShape() {
        return this.showShape;
    }
    
    public void setShowShape(Boolean showShape) {
        this.showShape = showShape;
    }

    
    @Column(name="show_coordinate")
    public Boolean getShowCoordinate() {
        return this.showCoordinate;
    }
    
    public void setShowCoordinate(Boolean showCoordinate) {
        this.showCoordinate = showCoordinate;
    }

    
    @Column(name="closed_shape")
    public Boolean getClosedShape() {
        return this.closedShape;
    }
    
    public void setClosedShape(Boolean closedShape) {
        this.closedShape = closedShape;
    }

    
    @Column(name="color")
    public Integer getColor() {
        return this.color;
    }
    
    public void setColor(Integer color) {
        this.color = color;
    }

    
    @Column(name="hit_message")
    public String getHitMessage() {
        return this.hitMessage;
    }
    
    public void setHitMessage(String hitMessage) {
        this.hitMessage = hitMessage;
    }

    
    @Column(name="static_object")
    public Boolean getStaticObject() {
        return this.staticObject;
    }
    
    public void setStaticObject(Boolean staticObject) {
        this.staticObject = staticObject;
    }




}



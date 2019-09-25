/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.golf.course;

import com.stayprime.geo.Coordinates;

/**
 * This is the pinLocation data transfer object class
 * It contains a holeID, courseID and pinLocation.
 * Its a part of the CoursePinLocationsInfoDTO object
 * @author sarthak
 */
public class PinLocationDTO {
    
    // hole the pinLocation belongs to
    private int holeId;
    
    // course the pinLocation is part of
    private int courseId;
    
    // location coordinates of pin
    private Coordinates coordinate;
    
    public PinLocationDTO() {}
    
    public PinLocationDTO(int holeId, int courseId, Coordinates coordinate) {
        this.holeId = holeId;
        this.courseId = courseId;
        this.coordinate = coordinate;
    }
    
    public PinLocationDTO(PinLocationDTO pinLocationDTO) {
        this.holeId = pinLocationDTO.getHoleId();
        this.courseId = pinLocationDTO.getCourseId();
        this.coordinate = new Coordinates(this.getCoordinate());
    }

    // toString method
    @Override
    public String toString() {
        return "PinLocationDTO{" + "holeId=" + holeId + ", courseId=" + courseId + ", coordinate=" + coordinate + '}';
    }

    
    // standard getters and setters
    
    public int getHoleId() {
        return holeId;
    }

    public void setHoleId(int holeId) {
        this.holeId = holeId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Coordinates getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinates coordinate) {
        this.coordinate = coordinate;
    }
    
    
    
}


package com.stayprime.tournament.impact.model;

import java.util.Map;

public class Course {

    private String Id;
    private String Name;
    /**
     * 
     * @return
     *     The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * 
     * @param Id
     *     The Id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    public Course withId(String Id) {
        this.Id = Id;
        return this;
    }

    /**
     * 
     * @return
     *     The Name
     */
    public String getName() {
        return Name;
    }

    /**
     * 
     * @param Name
     *     The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    public Course withName(String Name) {
        this.Name = Name;
        return this;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.oncourseads;

/**
 *
 * @author benjamin
 */
public class Category {
    public int id;
    public String name;
    public String description;

    public Category(int id, String name, String description) {
	this.id = id;
	this.name = name;
	this.description = description;
    }

    @Override
    public boolean equals(Object o) {
	if(o instanceof Category)
	    return id == ((Category)o).id;
	else if(o instanceof Integer)
	    return id == ((Integer)o);
	
	return false;
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 41 * hash + this.id;
	return hash;
    }

    @Override
    public String toString() {
	return name;
    }
}

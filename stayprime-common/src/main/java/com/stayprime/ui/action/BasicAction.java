/*
 * 
 */

package com.stayprime.ui.action;

/**
 *
 * @author benjamin
 */
public class BasicAction implements Action {
    private String name;
    private String displayName;

    public BasicAction() {
	this(null, null);
    }

    public BasicAction(String name, String displayName) {
	this.name = name;
	this.displayName = displayName;
    }

    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    @Override
    public void performAction() {
    }

}

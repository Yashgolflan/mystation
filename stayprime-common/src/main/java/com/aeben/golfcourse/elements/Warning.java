/*
 * 
 */

package com.aeben.golfcourse.elements;

/**
 *
 * @author benjamin
 */
public class Warning {
    private String message;
    private String subMessage;
    private WarningLevel level;
    private CourseAction action;

    public Warning(WarningLevel level, String message, String subMessage, CourseAction action) {
	this.level = level;
	this.message = message;
	this.subMessage = subMessage;
	this.action = action;
    }
    
    public WarningLevel getLevel() {
	return level;
    }

    public String getMessage() {
	return message;
    }

    public String getSubMessage() {
	return subMessage;
    }

    public CourseAction getAction() {
	return action;
    }

}

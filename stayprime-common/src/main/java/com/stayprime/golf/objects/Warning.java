/*
 * 
 */

package com.stayprime.golf.objects;

/**
 *
 * @author benjamin
 */
public class Warning {
    private String message;
    private String subMessage;
    private WarningLevel level;

    public Warning(WarningLevel level, String message, String subMessage) {
	this.level = level;
	this.message = message;
	this.subMessage = subMessage;
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

}

package com.stayprime.basestation2.util;

/**
 * 
 * @author Omer
 */
public class Parameter extends NameValuePair {
    
    public Parameter() {
        super();
    }
    
    public Parameter(String name, String value) {
        super(name, value);
    }
    
    @Override public Parameter clone() {
        return new Parameter(getName(), getValue());
    }
}
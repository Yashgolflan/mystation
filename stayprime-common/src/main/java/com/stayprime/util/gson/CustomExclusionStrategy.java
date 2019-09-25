/*
 * 
 */
package com.stayprime.util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class CustomExclusionStrategy implements ExclusionStrategy {

    private Class classToExclude;

    public CustomExclusionStrategy() {
        this(null);
    }

    public CustomExclusionStrategy(Class classToExclude) {
        this.classToExclude = classToExclude;
    }

    // This method is called for all fields. if the method returns false the
    // field is excluded from serialization
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        if (f.getAnnotation(Exclude.class) == null) {
            return false;
        }

        return true;
    }

    // This method is called for all classes. If the method returns false the
    // class is excluded.
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
//        if (clazz.equals(classToExclude)) {
//            return true;
//        }
        return false;
    }

}

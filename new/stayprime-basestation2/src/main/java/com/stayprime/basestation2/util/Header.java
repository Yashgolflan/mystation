
package com.stayprime.basestation2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * 
 * @author Omer 
 */
public class Header extends NameValuePair {
    private List<Element> elements = new ArrayList<Element>();
    
    /**
     * Creates a new Header with a null name and value, and no elements.
     */
    public Header() {
    }
    
    /**
     * Creates a new Header with the given name and value, and no elements.
     * 
     * @param name The name. May be null.
     * @param value The value. May be null.
     */
    public Header(String name, String value) {
        super(name, value);
    }
    
    
    public Header(String name, String value, Element... elements) {
        super(name, value);
        setElements(elements);
    }
    
   
    public Element[] getElements() {
        return elements.toArray(new Element[0]);
    }

    public void setElements(Element... elements) {
        Element[] old = getElements();
        this.elements.clear();
        if (elements != null) {
            this.elements.addAll(Arrays.asList(elements));
        }
        firePropertyChange("elements", old, getElements());
    }
    
    @Override
    public String toString() {
        return getName() + ": " + getValue();
    }

    /**
     * A representation of an Element within a Header.
     */
    public static final class Element {
        private Parameter[] params = new Parameter[0];
        
        /**
         * Create a new instance of Element with the given params.
         * 
         * @param params the Parameters. May be null.
         */
        public Element(Parameter... params) {
            if (params == null) {
                this.params = new Parameter[0];
            } else {
                this.params = new Parameter[params.length];
                System.arraycopy(params, 0, this.params, 0, this.params.length);
            }
        }
              
        public Parameter[] getParameters() {
            Parameter[] dest = new Parameter[params.length];
            System.arraycopy(params, 0, dest, 0, params.length);
            return dest;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.legacy;

/**
 *
 * @author benjamin
 */
public class Pair <E, T> {
    public E firstItem;
    public T secondItem;

    public Pair(E object1, T object2) {
        this.firstItem = object1;
        this.secondItem = object2;
    }

    @Override
    public Pair clone() {
        return new Pair(firstItem, secondItem);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Pair) {
            boolean equals = true;
            Pair pair = (Pair) o;
            if(firstItem != null && !firstItem.equals(pair.firstItem))
                equals = false;
            else if(firstItem != pair.firstItem)
                equals = false;
            if(secondItem != null && !secondItem.equals(pair.secondItem))
                equals = false;
            else if(secondItem != pair.secondItem)
                equals = false;

            return equals;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.firstItem != null ? this.firstItem.hashCode() : 0);
        hash = 61 * hash + (this.secondItem != null ? this.secondItem.hashCode() : 0);
        return hash;
    }
}

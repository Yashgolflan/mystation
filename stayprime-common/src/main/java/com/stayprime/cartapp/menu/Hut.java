/*
 * 
 */
package com.stayprime.cartapp.menu;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class Hut {
    private final int number;
    private final List<HoleObject> holes = new ArrayList<HoleObject>();

    public Hut(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setForHole(int hole) {
        holes.add(new HoleObject(hole, false));
    }

    public boolean isForHole(int hole) {
        if (hole > 0 && hole <= 64) {
            for (HoleObject h : this.holes) {
                if (h.getHoleNumber() == hole) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Hut) {
            return ((Hut) obj).getNumber() == number;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.number;
        return hash;
    }

    private static class HoleObject {
        private int holeNumber;
        private boolean popup;

        public HoleObject(int holeNumber, boolean popup) {
            this.holeNumber = holeNumber;
            this.popup = popup;
        }

        public int getHoleNumber() {
            return holeNumber;
        }

        public boolean isPopup() {
            return popup;
        }

    }

}

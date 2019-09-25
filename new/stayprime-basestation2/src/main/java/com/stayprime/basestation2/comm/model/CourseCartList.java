/*
 * 
 */
package com.stayprime.basestation2.comm.model;

import com.stayprime.model.golf.GolfCart;
import java.util.ArrayList;
import java.util.List;

/**
 * Model of the carts currently playing on the course and every hole.
 * @author benjamin
 */
public class CourseCartList {
    private static final int COURSE = 0;
    private List<CartList> holes;
    private final int holeCount;

    public CourseCartList(int holeCount) {
        this.holeCount = holeCount;
        holes = new ArrayList(holeCount + 1);

        for (int i = 0; i <= holeCount; i++) {
            holes.add(new CartList());
        }
    }

    public int getHoleCount() {
        return holeCount;
    }

    public CartList getCourseCartList() {
        return holes.get(COURSE);
    }

    public CartList getHoleCartList(int n) {
        return holes.get(n);
    }

    public boolean isCartInHole(int cart, int hole) {
        CartList list = getHoleCartList(hole);
        return list.findCartNumber(cart) >= 0;
    }

    public void putInHole(int hole, GolfCart cart) {
        CartList list = getHoleCartList(hole);
        list.put(cart);
    }

    public boolean removeFromHole(int hole, GolfCart cart) {
        CartList list = getHoleCartList(hole);
        return list.remove(cart);
    }

}

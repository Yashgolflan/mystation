/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.cartapp.menu;

import com.stayprime.golf.course.GolfHole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 *
 * @author benjamin
 */
public class Menu {

    private String version;
    private Date lastUpdated;
    private final List<MenuItem> snacks;
    private final List<MenuItem> food;
    private final List<MenuItem> drinks;
    private final List<Hut> huts;
    private MenuItemComparator comparator;

    public Menu() {
        snacks = new ArrayList<MenuItem>();
        food = new ArrayList<MenuItem>();
        drinks = new ArrayList<MenuItem>();

        huts = new ArrayList<Hut>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<MenuItem> getSnacks() {
        return snacks;
    }

    public List<MenuItem> getFood() {
        return food;
    }

    public List<MenuItem> getDrinks() {
        return drinks;
    }

    public List<Hut> getHuts() {
        return huts;
    }

    public int getHutForHole(int currentHole) {
        if (huts != null) {
            for (Hut h : huts) {
                if (h.isForHole(currentHole)) {
                    return h.getNumber();
                }
            }
        }

        return 0;
    }

    private boolean isEmpty() {
        return CollectionUtils.isEmpty(food) && CollectionUtils.isEmpty(drinks) && CollectionUtils.isEmpty(snacks);
    }

    public boolean isEnabledForHole(GolfHole hole) {
        if (isEmpty() || hole == null || hole.getHoleIndex() == 0) {
            return false;
        }
        return isEnabledForHole(hole.getHoleIndex());
    }

    public boolean isEnabledForHole(int hole) {
        if (huts == null || huts.isEmpty()) {
            return true;
        }
        else {
            for (Hut hut : huts) {
                if (hut.isForHole(hole)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void sortItems() {
        comparator = new MenuItemComparator();
        Collections.sort(snacks, comparator);
        Collections.sort(drinks, comparator);
        Collections.sort(food, comparator);
    }

    private static class MenuItemComparator implements Comparator<MenuItem> {
        @Override
        public int compare(MenuItem item1, MenuItem item2) {
            if (item1 == null || item1.getCode() == null) {
                if (item2 == null || item2.getCode() == null) {
                    return 0;
                }
                else {
                    return 1;
                }
            }
            else if (item2 == null || item2.getCode() == null) {
                if (item1 == null || item1.getCode() == null) {
                    return 0;
                }
                else {
                    return -1;
                }
            }
            else {
                return item1.getCode().compareTo(item2.getCode());
            }
        }
    }

}

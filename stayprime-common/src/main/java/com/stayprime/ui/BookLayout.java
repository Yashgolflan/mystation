/*
 * 
 */

package com.stayprime.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class BookLayout implements LayoutManager {
    private List<Component> components;

    public BookLayout() {
	components = new ArrayList<Component>();
    }

    public void addLayoutComponent(String name, Component comp) {
	components.add(comp);
    }

    public void removeLayoutComponent(Component comp) {
	components.remove(comp);
    }

    public Dimension preferredLayoutSize(Container parent) {
	Dimension d = new Dimension();

	for(int i = 0; i < parent.getComponentCount(); i++) {
	    Component c = parent.getComponent(i);

	    Dimension pref = c.getPreferredSize();
	    d.width = Math.max(d.width, pref.width);
	    d.height = Math.max(d.height, pref.height);
	}
	
	//d.width *= 2;

	return d;
    }

    public Dimension minimumLayoutSize(Container parent) {
	Dimension d = new Dimension();

	for(int i = 0; i < parent.getComponentCount(); i++) {
	    Component c = parent.getComponent(i);

	    Dimension min = c.getMinimumSize();
	    d.width = Math.max(d.width, min.width);
	    d.height = Math.max(d.height, min.height);
	}

	//d.width *= 2;

	return d;
    }

    public void layoutContainer(Container parent) {
	int width = parent.getWidth();
	int height = parent.getHeight();

	//int left = width / 2;
	//int right = width - left;

	for(int i = 0; i < parent.getComponentCount(); i++) {
	    Component c = parent.getComponent(i);

	    boolean l = i%2 == 0;
	    //c.setBounds(l? 0 : left, 0, l? left : right, height);
	    c.setBounds(0, 0, width, height);
	}
    }

}

package com.stayprime.ui;

/**
THIS PROGRAM IS PROVIDED "AS IS" WITHOUT ANY WARRANTIES (OR CONDITIONS),
EXPRESS OR IMPLIED WITH RESPECT TO THE PROGRAM, INCLUDING THE IMPLIED WARRANTIES (OR CONDITIONS)
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK ARISING OUT OF USE OR
PERFORMANCE OF THE PROGRAM AND DOCUMENTATION REMAINS WITH THE USER.
*/
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * A vertical layout manager similar to java.awt.FlowLayout.
 * Like FlowLayout components do not expand to fill available space except when the horizontal alignment
 * is <code>BOTH</code>
 * in which case components are stretched horizontally. Unlike FlowLayout, components will not wrap to form another
 * column if there isn't enough space vertically. VerticalLayout can optionally anchor components to the top or bottom
 * of the display area or center them between the top and bottom.
 *
 * Revision date 12th July 2001
 *
 * @author Colin Mummery  e-mail: colin_mummery@yahoo.com Homepage:www.kagi.com/equitysoft -
 * Based on 'FlexLayout' in Java class libraries Vol 2 Chan/Lee Addison-Wesley 1998
 */

public class RowTableLayout implements LayoutManager {

  private int hgap; //the horizontal gap between components...defaults to 5
  private int minHeight = 0;
  private List<Container> rows;
  private List<Integer> columns;

    public RowTableLayout() {
	this(5);
    }

    public RowTableLayout(int hgap) {
	this.hgap = hgap;
	rows = new ArrayList<Container>();
	columns = new ArrayList<Integer>();
    }
//----------------------------------------------------------------------------

    public void addRow(Container row) {
	if(!rows.contains(row))
	    rows.add(row);
    }

    public void removeRow(Container row) {
	rows.remove(row);
    }

    public void clear() {
	rows.clear();
    }

    private void calculateColumnSizes() {
	columns.clear();

	for(Container row: rows) {
	    for(int i = 0; i < row.getComponentCount(); i++) {
		Component c = row.getComponent(i);

		if(i == columns.size())
		    columns.add(0);

		Dimension pref = c.getPreferredSize();

		if(c.isVisible() && columns.get(i) < pref.width) {
		    columns.set(i, pref.width);
		}
	    }
	}
    }

    private Dimension layoutSize(Container parent, boolean minimum) {
	calculateColumnSizes();
	Dimension dim = new Dimension();
	boolean addGap = false;

	synchronized (parent.getTreeLock()) {
	    for (int i = 0; i < columns.size(); i++) {
		int col = columns.get(i);
		if (col > 0) {
		    dim.width += col;
		    if (addGap) {
			dim.width += hgap;
		    }
		    addGap = true;
		}
	    }

	    int n = parent.getComponentCount();
	    for (int i = 0; i < n; i++) {
		Component c = parent.getComponent(i);
		if (c.isVisible()) {
		    dim.height = Math.max(dim.height, c.getPreferredSize().height);
		}
	    }
	}

	Insets in = parent.getInsets();
	dim.width += in.left + in.right;
	dim.height += in.top + in.bottom;
	return dim;
    }
//-----------------------------------------------------------------------------

    /**
     * Lays out the container.
     */
    public void layoutContainer(Container parent) {
	Insets in = parent.getInsets();
	int height = 0;
	
	synchronized (parent.getTreeLock()) {
	    calculateColumnSizes();
	    
	    int n = parent.getComponentCount();
	    for (int i = 0; i < n; i++) {
		Component c = parent.getComponent(i);
		if (c.isVisible()) 
		    height = Math.max(height, c.getPreferredSize().height);
	    }

	    Dimension pd = parent.getSize();
	    float pw = pd.width;
	    float ph = pd.height;
	    int x = in.left, y = in.top;
	    boolean addGap = false;

	    for (int i = 0; i < columns.size(); i++) {
		if(parent.getComponentCount() <= i)
		    continue;
		
		Component c = parent.getComponent(i);
		int col = columns.get(i);

		if (col > 0) {
		    if (addGap)
			x += hgap;
		    c.setBounds(x, y, col, height);

		    x += col;
		    addGap = true;
		}
	    }

	}
    }
//-----------------------------------------------------------------------------
   public Dimension minimumLayoutSize(Container parent){return layoutSize(parent,false);}
//-----------------------------------------------------------------------------
   public Dimension preferredLayoutSize(Container parent){return layoutSize(parent,false);}
//----------------------------------------------------------------------------
/**
 * Not used by this class
 */
   public void addLayoutComponent(String name,Component comp){}
//-----------------------------------------------------------------------------
/**
 * Not used by this class
 */
   public void removeLayoutComponent(Component comp){}
//-----------------------------------------------------------------------------
   public String toString(){return getClass().getName()+"[hgap="+hgap+"]";}

    public int getHgap() {
        return hgap;
    }

    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }
}
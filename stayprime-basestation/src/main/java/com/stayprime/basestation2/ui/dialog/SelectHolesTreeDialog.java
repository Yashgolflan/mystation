/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SelectHolesTreeDialog.java
 *
 * Created on 3/06/2011, 03:33:58 PM
 */

package com.stayprime.basestation2.ui.dialog;

import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.GolfClub;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import org.jdesktop.application.Action;

/**
 *
 * @author benjamin
 */
public class SelectHolesTreeDialog extends javax.swing.JDialog {
    private String selectedHolesString = null;

    /** Creates new form SelectHolesTreeDialog */
    public SelectHolesTreeDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

	CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
	jTree1.setCellRenderer(renderer);

	jTree1.setCellEditor(new CheckBoxNodeEditor(jTree1));
	jTree1.setEditable(true);
    }

    public void setGolfClub(GolfClub gc) {
	NamedVector courses = new NamedVector("Golf Club");

	if(gc != null && gc.getCourses() != null) {
	    for(CourseDefinition course: gc.getCourses()) {
		NamedVector courseNode = new NamedVector(course);

		for(int i = 1; i <= course.getHoleCount(); i++) {
		    courseNode.add(new CheckBoxNode("Hole " + i, false));
		}

		courses.add(courseNode);
	    }
	}

	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Golf Club");
	JTree.DynamicUtilTreeNode.createChildren(root, courses);
	jTree1.setModel(new DefaultTreeModel(root));

	for (int i = 0; i < jTree1.getRowCount(); i++) {
		 jTree1.expandRow(i);
	}
    }

    public void setSelectedHoles(String selectedHoles) {
	selectedHolesString = null;
	
	DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
	if(model.getRoot() instanceof DefaultMutableTreeNode) {
	    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
	    for(int i = 0; i < root.getChildCount(); i++) {
		//if(root.getChildAt(i) instanceof DefaultMutableTreeNode) {
		    DefaultMutableTreeNode course = (DefaultMutableTreeNode) root.getChildAt(i);
		    for(int j = 0; i < course.getChildCount(); i++) {
			//if(course.getChildAt(i) instanceof DefaultMutableTreeNode) {
			    DefaultMutableTreeNode hole = (DefaultMutableTreeNode) course.getChildAt(i);
			    //if(hole.getUserObject() instanceof CheckBoxNode) {
				CheckBoxNode checkBoxNode = (CheckBoxNode) hole.getUserObject();
				checkBoxNode.setSelected(false);
			    //}
			//}
		    }
		//}
	    }

	    if(selectedHoles != null) {
		String selectedCourses[] = selectedHoles.split(";");

		for(String startCourse: selectedCourses) {
		    if(startCourse.indexOf(":") > 0) {
			int courseNumber = Integer.parseInt(startCourse.substring(0, startCourse.indexOf(":")));
			String holes[] = startCourse.substring(1+startCourse.indexOf(":")).split(",");
			for(String startHole: holes) {
			    int holeNumber = Integer.parseInt(startHole);

			    if(courseNumber <= root.getChildCount()) {// && root.getChildAt(courseNumber - 1) instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode courseNode = (DefaultMutableTreeNode) root.getChildAt(courseNumber - 1);
				if(//courseNode.getUserObject() instanceof CourseDefinition &&
					((CourseDefinition) ((NamedVector)courseNode.getUserObject()).name ).getCourseNumber() == courseNumber) {
				    if(holeNumber <= courseNode.getChildCount()) {// && courseNode.getChildAt(holeNumber - 1) instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode holeNode = (DefaultMutableTreeNode) courseNode.getChildAt(holeNumber - 1);
					((CheckBoxNode)holeNode.getUserObject()).selected = true;
				    }
				}
			    }
			}
		    }
		}
	    }

	}
    }

    public String getSelectedHolesString() {
	return selectedHolesString;
    }

    private String createSelectedHolesString() {
        StringBuilder string = new StringBuilder();
        int currentCourse = -1, currentHole = -1;

	DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
	if(model.getRoot() instanceof DefaultMutableTreeNode) {
	    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
	    for(int i = 0; i < root.getChildCount(); i++) {
		DefaultMutableTreeNode course = (DefaultMutableTreeNode) root.getChildAt(i);
		int courseNumber = ((CourseDefinition) ((NamedVector)course.getUserObject()).name ).getCourseNumber();

		for(int holeIndex = 0; holeIndex < course.getChildCount(); holeIndex++) {
		    DefaultMutableTreeNode hole = (DefaultMutableTreeNode) course.getChildAt(holeIndex);
		    CheckBoxNode checkBoxNode = (CheckBoxNode) hole.getUserObject();

		    if(checkBoxNode.selected) {
			if(currentCourse != courseNumber) {
			    if(currentCourse != -1)
				string.append(";");
			    currentCourse = courseNumber;
			    string.append(currentCourse);
			    string.append(":");
			    currentHole = -1;
			}

			if(currentHole != -1)
			    string.append(",");

			currentHole = holeIndex + 1;
			string.append(currentHole);
		    }
		}
	    }
	}

        return string.toString();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(SelectHolesTreeDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(150, 200));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setEditable(true);
        jTree1.setName("jTree1"); // NOI18N
        jTree1.setRootVisible(false);
        jTree1.setToggleClickCount(0);
        jScrollPane1.setViewportView(jTree1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(SelectHolesTreeDialog.class, this);
        jButton1.setAction(actionMap.get("cancel")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jButton2.setAction(actionMap.get("done")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void done() {
	selectedHolesString = createSelectedHolesString();
	setVisible(false);
    }

    @Action
    public void cancel() {
	setVisible(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables

}

class CheckBoxNodeRenderer extends DefaultTreeCellRenderer {
    Icon empty, full;
    public CheckBoxNodeRenderer() {
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(SelectHolesTreeDialog.class);
        empty = resourceMap.getIcon("checkboxEmpty.icon");
        full = resourceMap.getIcon("checkboxFull.icon");
    }


    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
	    boolean selected, boolean expanded, boolean leaf, int row,
	    boolean hasFocus) {

	if (leaf) {
	    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

	    if (value instanceof DefaultMutableTreeNode) {
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		Object userObject = treeNode.getUserObject();

		if(userObject instanceof CheckBoxNode) {
		    CheckBoxNode checkBoxNode = (CheckBoxNode) userObject;
		    setIcon(checkBoxNode.selected? full : empty);
		}
	    }
	}
	else {
	    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}
	return this;
    }
}

class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
    JTree tree;

    public CheckBoxNodeEditor(JTree tree) {
	this.tree = tree;
    }

    public Object getCellEditorValue() {
	//JCheckBox checkbox = renderer.getLeafRenderer();
	return null;
    }

    @Override
    public boolean isCellEditable(EventObject event) {
	if (event instanceof MouseEvent) {
	    MouseEvent mouseEvent = (MouseEvent) event;
	    TreePath path = tree.getPathForLocation(mouseEvent.getX(),
		    mouseEvent.getY());
	    if (path != null) {
		Object node = path.getLastPathComponent();
		if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
		    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
		    Object userObject = treeNode.getUserObject();

		    if(treeNode.isLeaf() && userObject instanceof CheckBoxNode) {
			CheckBoxNode checkBoxNode = (CheckBoxNode) userObject;
			checkBoxNode.selected = !checkBoxNode.selected;
			tree.repaint();
		    }
		}
	    }
	}
	return false;
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value,
	    boolean selected, boolean expanded, boolean leaf, int row) {
	return null;
    }
}

class CheckBoxNode {

    String text;
    boolean selected;

    public CheckBoxNode(String text, boolean selected) {
	this.text = text;
	this.selected = selected;
    }

    public boolean isSelected() {
	return selected;
    }

    public void setSelected(boolean newValue) {
	selected = newValue;
    }

    public String getText() {
	return text;
    }

    public void setText(String newValue) {
	text = newValue;
    }

    @Override
    public String toString() {
	return text;
    }
}

class NamedVector extends Vector {

    Object name;

    public NamedVector(Object name) {
	this.name = name;
    }

    public NamedVector(String name, Object elements[]) {
	this.name = name;
	for (int i = 0, n = elements.length; i < n; i++) {
	    add(elements[i]);
	}
    }

    @Override
    public String toString() {
	return name.toString();
    }
}

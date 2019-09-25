/*
 * 
 */
package com.stayprime.basestation.ui.site;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import com.stayprime.ui.editor.Factory;
import com.stayprime.ui.editor.ListEditor;
import com.stayprime.hibernate.entities.Courses;
import com.stayprime.hibernate.entities.Holes;
import com.stayprime.hibernate.entities.HolesId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author benjamin
 */
public class HoleListEditor extends ListEditor<Holes> {
    private Courses course;

    public HoleListEditor() {
        setTableFormat(new HoleTableFormat());
        setItemFactory(new Factory<Holes>() {
            public Holes create() {
                int n = getSourceList().size() + 1;
                Holes hole = new Holes(course, n);
                return hole;
            }
        });
    }

    public void setGolfCourse(Courses course) {
        this.course = course;
        List<Holes> holes = null;
        if (course != null) {
            holes = Holes.getSortedHolesList(course.getHoleses());

        }
        setList(holes);
        setEditingObject(course);
    }

    @Override
    protected void addItem(int i, Holes item) {
        super.addItem(i, item);
        course.getHoleses().add(item);
    }

    @Override
    protected void deleteItem(int i) {
        super.deleteItem(i);
        List<Holes> tempHolesList = Holes.getSortedHolesList(course.getHoleses());
        tempHolesList.remove(i);
        course.getHoleses().clear();
        for (Holes hole : tempHolesList) {
            course.getHoleses().add(hole);
        }
    }

    @Override
    public boolean applyChanges() {
        boolean modified = isModified() | super.applyChanges();
        setGolfCourse(course);
        return modified;
    }

    private class HoleTableFormat implements AdvancedTableFormat<Holes>, WritableTableFormat<Holes> {
        @Override
        public Class getColumnClass(int column) {
            int i = 0;
            if(column == i++) return Integer.class; //number
            if(column == i++) return Integer.class; //par
            if(column == i++) return Integer.class; //si
            if(column == i++) return Integer.class; // pace of play
            if(column == i++) return Boolean.class; //cart path only
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public Comparator getColumnComparator(int column) {
            return new ComparableComparator();
        }
        
        @Override
        public int getColumnCount() {
            return 5;
        }
        
        @Override
        public String getColumnName(int column) {
            int i = 0;
            if(column == i++) return "Number"; //number
            if(column == i++) return "Par"; //par
            if(column == i++) return "SI"; //si
            if(column == i++) return "PoP"; // pace of play
            if(column == i++) return "Path only"; //cart path only
            throw new IllegalArgumentException("Illegal column index " + column);
        }
        
        @Override
        public Object getColumnValue(Holes hole, int column) {
            int i = 0;
            if(column == i++) return hole.getId().getHole(); //number
            if(column == i++) return hole.getPar(); //par
            if(column == i++) return hole.getStrokeIndex(); //SI
            if(column == i++) return hole.getPaceOfPlay(); // pace of play
            if(column == i++) return hole.isCartPathOnly(); //cart path only
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public boolean isEditable(Holes baseObject, int column) {
            int i = 0;
            if(column == i++) return false; //number
            if(column == i++) return true; //par
            if(column == i++) return true; //si
            if(column == i++) return true; // pace of play
            if(column == i++) return true; //cart path only
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public Holes setColumnValue(Holes hole, Object editedValue, int column) {
            int i = 0;
            if(column == i++); //number
            if(column == i++) hole.setPar((Integer)editedValue); //par
            if(column == i++) hole.setStrokeIndex((Integer)editedValue); //SI
            if(column == i++) hole.setPaceOfPlay((Integer) editedValue); // pace of play
            if(column == i++) hole.setCartPathOnly((Boolean)editedValue); //cart path only
            setModified(true);
            return hole;
        }
    }
    
}

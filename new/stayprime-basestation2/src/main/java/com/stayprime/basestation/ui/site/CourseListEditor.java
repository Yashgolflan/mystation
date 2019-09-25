/*
 * 
 */
package com.stayprime.basestation.ui.site;

import com.stayprime.ui.editor.ListEditor;
import com.stayprime.ui.editor.Factory;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import com.stayprime.hibernate.entities.Courses;
import java.util.Comparator;
import java.util.List;

/**
 * @author Benjamin Baron
 */
public class CourseListEditor extends ListEditor<Courses> {
    public CourseListEditor() {com.alee.utils.swing.DocumentChangeListener c;
        setTableFormat(new CourseTableFormat());
        setItemFactory(new Factory<Courses>() {
            @Override
            public Courses create() {
                int n = getSourceList().size() + 1;
                Courses course = new Courses();
                course.setNumber(n);
                course.setName("Course " + n);
                course.setHoles(18);
                course.trimHoleCount();
                return course;
            }
        });
    }

    @Override
    protected void setupTableColumns() {
        getTable().getColumnModel().getColumn(0).setPreferredWidth(80);
        getTable().getColumnModel().getColumn(1).setPreferredWidth(20);
    }

    public void setCourses(List<Courses> courses) {
        setList(courses);
        //refresh();
    }

    @Override
    protected void addItem(int i, Courses item) {
        super.addItem(i, item);
        applyChanges();
    }

    private class CourseTableFormat implements AdvancedTableFormat<Courses>, WritableTableFormat<Courses> {
        @Override
        public Class getColumnClass(int column) {
            int i = 0;
//            if(column == i++) return Integer.class; //number
            if(column == i++) return String.class; //name
            if(column == i++) return Integer.class; //holes
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public Comparator getColumnComparator(int column) {
            return new ComparableComparator();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            int i = 0;
//            if(column == i++) return "Number"; //number
            if(column == i++) return "Course name"; //name
            if(column == i++) return "Holes"; //holes
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public Object getColumnValue(Courses course, int column) {
            int i = 0;
//            if(column == i++) return course.getNumber(); //number
            if(column == i++) return course.getName(); //name
            if(column == i++) return course.getHoles(); //holes
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public boolean isEditable(Courses baseObject, int column) {
            int i = 0;
//            if(column == i++) return false; //number
            if(column == i++) return true; //name
            if(column == i++) return true; //holes
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public Courses setColumnValue(Courses course, Object editedValue, int column) {
            int i = 0;
//            if(column == i++); //number
            if(column == i++) course.setName(editedValue.toString()); //name
            if(column == i++) { //holes
                course.setHoles((Integer)editedValue);
                course.trimHoleCount();
                applyChanges();
            }
            setModified(true);
            return course;
        }
    }

//    private void initComponents() {
//        ResourceBundle bundle = ResourceBundle.getBundle("com.stayprime.basestation.resources.GolfCourseEditor");
//        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
//        separator1 = compFactory.createSeparator(bundle.getString("CourseListEditor.separator1.text"), SwingConstants.CENTER);
//        add(separator1, BorderLayout.NORTH);
//    }
//
//    private JComponent separator1;
}

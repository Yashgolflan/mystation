/*
 * 
 */
package com.stayprime.basestation.ui.site;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.ui.editor.Factory;
import com.stayprime.ui.editor.ListEditor;
import com.stayprime.hibernate.entities.Courses;
import com.stayprime.hibernate.entities.Holes;
import com.stayprime.hibernate.entities.HolesId;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXTextField;

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

    @Override

    protected void setupTableColumns() {
        //  getTable().getColumnModel().getColumn(4).setPreferredWidth(300);
        //getTable().setPreferredScrollableViewportSize(getTable().getPreferredSize());

        // getTable().setRowHeight(30);
        getTable().getColumnModel().getColumn(1).setCellEditor(new CustomTableEditor1(new JXTextField()));
        getTable().getColumnModel().getColumn(2).setCellEditor(new CustomTableEditor1(new JXTextField()));
        getTable().getColumnModel().getColumn(3).setCellEditor(new CustomTableEditor1(new JXTextField()));
//        getTable().getColumnModel().getColumn(1).setCellRenderer(new CustomComboRenderer(ComMode.values()));
//        getTable().getColumnModel().getColumn(2).setCellEditor(new CustomTableEditor1(new JXTextField()));
//        getTable().getColumnModel().getColumn(4).setCellRenderer(new CustomButtonRenderer());
//        getTable().getColumnModel().getColumn(4).setCellEditor(new CustomTableEditor(new JTextField()));
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

    class CustomTableEditor1 extends DefaultCellEditor {
//        private JButton button;
//        private String hole;

        private boolean clicked;
        private JTextField jtf;

        public CustomTableEditor1(JTextField textField) {
            super(textField);
            setClickCountToStart(1);
            this.jtf = textField;
            // jtf.setText("+");
            jtf.setEditable(true);
//            button = new JButton();
//            button.setOpaque(true);
//            button.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    fireEditingStopped();
//                }
//            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//            button.setForeground(Color.black);
//            button.setBackground(UIManager.getColor("Button.background"));
//            hole = setSponsoredHolesLabel(value == null ? "" : value.toString());
//            button.setText(hole);
//            clicked = true;
            if (value != null) {
                jtf.setText(value.toString());

            }
            jtf.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent ke) {
                    char c = ke.getKeyChar();
                    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                        ke.consume();
                    }

                    //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void keyPressed(KeyEvent ke) {
                    char c = ke.getKeyChar();
                    if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                        ke.consume();
                    }
                    ///throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void keyReleased(KeyEvent ke) {

                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });

            return jtf;
        }

//        public Object getCellEditorValue() {
//            if (clicked) {
//             //   HutsInfo hutInfo = getViewList().get(getTable().getSelectedRow());
//               // hole = showHoleSelectionDialog(hutInfo.getHoles());
//            }
//            clicked = false;
//            return hole;
//        }
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private class HoleTableFormat implements AdvancedTableFormat<Holes>, WritableTableFormat<Holes> {

        @Override
        public Class getColumnClass(int column) {
            int i = 0;
            if (column == i++) {
                return Integer.class; //number
            }
            if (column == i++) {
                return Integer.class; //par
            }
            if (column == i++) {
                return Integer.class; //si
            }
            if (column == i++) {
                return Integer.class; // pace of play
            }
            if (column == i++) {
                return Boolean.class; //cart path only
            }
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
            if (column == i++) {
                return "Number"; //number
            }
            if (column == i++) {
                return "Par"; //par
            }
            if (column == i++) {
                return "SI"; //si
            }
            if (column == i++) {
                return "PoP"; // pace of play
            }
            if (column == i++) {
                return "Path only"; //cart path only
            }
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public Object getColumnValue(Holes hole, int column) {
            int i = 0;
            if (column == i++) {
                return hole.getId().getHole(); //number
            }
            if (column == i++) {
                return hole.getPar(); //par
            }
            if (column == i++) {
                return hole.getStrokeIndex(); //SI
            }
            if (column == i++) {
                return hole.getPaceOfPlay(); // pace of play
            }
            if (column == i++) {
                return hole.isCartPathOnly(); //cart path only
            }
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public boolean isEditable(Holes baseObject, int column) {
            int i = 0;
            if (column == i++) {
                return false; //number
            }
            if (column == i++) {
                return true; //par
            }
            if (column == i++) {
                return true; //si
            }
            if (column == i++) {
                return true; // pace of play
            }
            if (column == i++) {
                return true; //cart path only
            }
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public Holes setColumnValue(Holes hole, Object editedValue, int column) {
            int i = 0;
            if (column == i++); //number
            if (column == i++) {
                if (editedValue instanceof String) {
                    try {
                        Integer.parseInt(editedValue.toString());
                        hole.setPar(Integer.parseInt(editedValue.toString()));
                    } catch (Exception e) {
                        NotificationPopup.showIncompeleteDetailsErrorPopup("Out of Range Value Added");
                    }
                   
                } else {
                    hole.setPar((Integer) editedValue);
                }
            } //par
            if (column == i++) {
                if (editedValue instanceof String) {
                    try {
                        Integer.parseInt(editedValue.toString());
                        hole.setStrokeIndex(Integer.parseInt(editedValue.toString()));
                    } catch (Exception e) {
                        NotificationPopup.showIncompeleteDetailsErrorPopup("Out of Range Value Added");
                    }
                   // hole.setStrokeIndex(Integer.parseInt(editedValue.toString()));
                } else {
                    hole.setStrokeIndex((Integer) editedValue); //SI
                }
            }
            if (column == i++) {
                if (editedValue instanceof String) {
                    try {
                        Integer.parseInt(editedValue.toString());
                        hole.setPaceOfPlay(Integer.parseInt(editedValue.toString()));
                    } catch (Exception e) {
                        NotificationPopup.showIncompeleteDetailsErrorPopup("Out of Range Value Added");
                    }

                } else {
                    hole.setPaceOfPlay((Integer) editedValue); // pace of play
                }
            }
            if (column == i++) {
                hole.setCartPathOnly((Boolean) editedValue); //cart path only
            }
            setModified(true);
            return hole;
        }
    }

}

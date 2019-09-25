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
import com.stayprime.hibernate.entities.Holes;
import com.stayprime.hibernate.entities.TeeBoxes;
import com.stayprime.ui.ColorPickerComboBox;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.swingx.JXTextField;

/**
 *
 * @author benjamin
 */
public class HoleTeeBoxEditor extends ListEditor<TeeBoxes> {
    private Holes hole;

    public HoleTeeBoxEditor() {
        setTableFormat(new TeeBoxTableFormat());
        setItemFactory(new Factory<TeeBoxes>() {
            public TeeBoxes create() {
                return hole.createTeeBox();
            }
        });
    }

    @Override
    protected void setupTableColumns() {
        JComboBox colorEditor = new ColorPickerComboBox();
        DefaultCellEditor combo = ColorPickerComboBox.createDefaultCellEditor(colorEditor);
        getTable().getColumnModel().getColumn(0).setCellEditor(combo);
        getTable().getColumnModel().getColumn(0).setCellRenderer(new ColorPickerComboBox());
        getTable().getColumnModel().getColumn(1).setCellEditor(new CustomTableEditor1(new JXTextField()));
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
    public Holes getGolfHole() {
        return hole;
    }

    public void setGolfHole(Holes hole) {
        setEditingObject(null);
        this.hole = hole;
        if (hole != null) {
            setList(hole.getTeeBoxes());
        }
        else {
            setList(null);
        }
        setEditingObject(hole);
    }

    @Override
    protected void addItem(int i, TeeBoxes item) {
        super.addItem(i, item);
        hole.getTeeBoxes().add(i, item);
        setHolesTeeBoxes();
    }

    @Override
    protected void deleteItem(int i) {
        super.deleteItem(i);
        hole.getTeeBoxes().remove(i);
        setHolesTeeBoxes(i);
    }

    /**
     * Sets the number, color and name of all the holes' tee boxes at once.
     */
    public void setHolesTeeBoxes() {
        setModified(true);
        Set<Holes> holes = null;

        if (hole != null && hole.getCourse() != null) {
            holes = hole.getCourse().getHoleses();
        }

        if(CollectionUtils.isNotEmpty(holes)) {
            for (Holes h: holes) {
                List<TeeBoxes> list = hole.getTeeBoxes();
                List<TeeBoxes> teeBoxes = h.getTeeBoxes();

                while (teeBoxes.size() > list.size()) {
                    teeBoxes.remove(teeBoxes.size() - 1);
                }
                while (teeBoxes.size() < list.size()) {
                  //  teeBoxes.add(hole.createTeeBox());
                  teeBoxes.add(h.createTeeBox());
                }
                for(int i = 0; i < list.size(); i++) {
                    teeBoxes.get(i).setColor(list.get(i).getColor());
                }
            }
        }
    }
     public void setHolesTeeBoxes(int index) {
        setModified(true);
        Set<Holes> holes = null;

        if (hole != null && hole.getCourse() != null) {
            holes = hole.getCourse().getHoleses();
        }

        if(CollectionUtils.isNotEmpty(holes)) {
            for (Holes h: holes) {
                List<TeeBoxes> list = hole.getTeeBoxes();
                List<TeeBoxes> teeBoxes = h.getTeeBoxes();

                while (teeBoxes.size() > list.size()) {
                    teeBoxes.remove(index);
                }
                while (teeBoxes.size() < list.size()) {
                  //  teeBoxes.add(hole.createTeeBox());
                  teeBoxes.add(h.createTeeBox());
                }
                for(int i = 0; i < list.size(); i++) {
                    teeBoxes.get(i).setColor(list.get(i).getColor());
                }
            }
        }
    }


    private class TeeBoxTableFormat implements AdvancedTableFormat<TeeBoxes>, WritableTableFormat<TeeBoxes> {

        @Override
        public Class getColumnClass(int column) {
            int i = 0;
            if (column == i++) return Color.class; //Color
            else if (column == i++) return Integer.class; //Distance
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
            if (column == i++)
                return "Color"; //Color
            else if (column == i++)
                return "Distance"; //Distance
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public Object getColumnValue(TeeBoxes teeBox, int column) {
            int i = 0;
            if (column == i++) {
                return new Color(teeBox.getColor()); //Color
            }
            else if (column == i++){
                try{
                    Integer.parseInt(teeBox.getDistance()==null?"0":teeBox.getDistance());
                     return Integer.parseInt(teeBox.getDistance()==null?"0":teeBox.getDistance()); //Distance
                }
                catch(Exception e){
                    return "0";
                }
               
            }
                
            throw new IllegalArgumentException("Illegal column index " + column);
        }

//        @Override
//        public boolean isEditable(TeeBoxes teeBox, int column) {
//            int i = 0;
//            if (column == i++)
//                return true; //Color
//            else if (column == i++)
//                return true; //Distance
//            throw new IllegalArgumentException("Illegal column index " + column);
//        }

        @Override
        public TeeBoxes setColumnValue(TeeBoxes teeBox, Object editedValue, int column) {
            if (column == 0) {
                if (editedValue instanceof Color) {
                    teeBox.setColor(((Color) editedValue).getRGB());
                }
                else if (editedValue instanceof Integer) {
                    teeBox.setColor((Integer) editedValue);
                }
                setHolesTeeBoxes(); //Calls setModified(true)
            }
            else if (column == 1) {
                try{
                        Integer.parseInt(editedValue.toString());
                        teeBox.setDistance(editedValue.toString()); //Distance
                        //hole.setPaceOfPlay(Integer.parseInt(editedValue.toString()));
                    }
                    catch(Exception e){
                        NotificationPopup.showIncompeleteDetailsErrorPopup("Out of Range Value Added");
                    }
                
                setModified(true);
            }
            return teeBox;
        }

        @Override
        public boolean isEditable(TeeBoxes baseObject, int column) {
                        int i = 0;
            if (column == i++)
                return true; //Color
            else if (column == i++)
                return true; //Distance
            throw new IllegalArgumentException("Illegal column index " + column);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.ui.fnb;

import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.HoleDefinition;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.ui.dialog.SelectHolesTreeDialog;
import com.stayprime.comm.enums.ComMode;
import com.stayprime.hibernate.entities.HutsInfo;
import com.stayprime.ui.editor.Factory;
import com.stayprime.ui.editor.ListEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXTextField;

/**
 *
 * @author priyanshu
 */
public class HutInfoList extends ListEditor<HutsInfo> {

    private HutTableFormat hutTableFormat;
    private JComboBox cmbMessageType;

    private SelectHolesTreeDialog selectHolesTreeDialog;

    private GolfClub golfClub;

    /**
     * Creates new form HutInfoList
     */
    public HutInfoList() {
        initComponents();
        hutTableFormat = new HutTableFormat();
        cmbMessageType = new JComboBox();
        setTableFormat(hutTableFormat);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setItemFactory(new Factory<HutsInfo>() {
            @Override
            public HutsInfo create() {
                return new HutsInfo(HutsInfo.generateHutNumber(getSourceList()));
            }
        });
    }

    @Override
    public void init() {
        cmbMessageType.setModel(new DefaultComboBoxModel(ComMode.values()));
        super.init();
    }

    @Override
    protected void setupTableColumns() {
        getTable().getColumnModel().getColumn(4).setPreferredWidth(300);
        getTable().setPreferredScrollableViewportSize(getTable().getPreferredSize());

        getTable().setRowHeight(30);

        getTable().getColumnModel().getColumn(1).setCellEditor(new CustomComboEditor(new JComboBox(new DefaultComboBoxModel(ComMode.values()))));
        getTable().getColumnModel().getColumn(1).setCellRenderer(new CustomComboRenderer(ComMode.values()));
        getTable().getColumnModel().getColumn(2).setCellEditor(new CustomTableEditor1(new JXTextField()));
        getTable().getColumnModel().getColumn(4).setCellRenderer(new CustomButtonRenderer());
        getTable().getColumnModel().getColumn(4).setCellEditor(new CustomTableEditor(new JTextField()));
    }

    public void setGolfClub(GolfClub golfClub) {
        this.golfClub = golfClub;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setFocusable(false);
        setRequestFocusEnabled(false);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    public String showHoleSelectionDialog(String hole) {

        if (selectHolesTreeDialog == null) {
            selectHolesTreeDialog = new SelectHolesTreeDialog(Application.getInstance(BaseStation2App.class).getMainFrame(), true);
            //To deal with Look and Feel Changes
            SwingUtilities.updateComponentTreeUI(selectHolesTreeDialog);
            selectHolesTreeDialog.setName("selectHolesTreeDialog");
            selectHolesTreeDialog.pack();
        }

        selectHolesTreeDialog.setGolfClub(BaseStation2App.getApplication().getGolfClub());
        selectHolesTreeDialog.setSelectedHoles(hole);
        Application.getInstance(BaseStation2App.class).show(selectHolesTreeDialog);

        String selectedHoles;
        if (selectHolesTreeDialog.getSelectedHolesString() != null) {
            return selectHolesTreeDialog.getSelectedHolesString();
        } else {
            selectedHoles = hole;
        }

        return selectedHoles;
    }

    private String setSponsoredHolesLabel(String holesString) {
        int count = 0;

        if (StringUtils.isNotBlank(holesString)) {
            List<HoleDefinition> holeList = getSponsoredHolesList(golfClub, holesString);
            count = holeList.size();

            String holeCount = "";
//            if (count == 1) {
//                holeCount = holeList.get(0).course.getName() + " " + holeList.get(0).number;
//            } else if (count == 0) {
//                holeCount = "None";
//            } else {
            for (HoleDefinition hole : holeList) {
                holeCount += hole.number + " ";
            }
            // }
            return holeCount + " holes";
        }
        return StringUtils.EMPTY + "No Holes Selected";
    }

    private List<HoleDefinition> getSponsoredHolesList(GolfClub golfClub, String holes) {
        List<HoleDefinition> list = new ArrayList<HoleDefinition>();
        String[] courses = holes.split(";");

        for (String courseHoles : courses) {
            String[] numHoles = courseHoles.split(":");
            int courseNumber = NumberUtils.toInt(numHoles[0], 0);

            if (courseNumber > 0 && courseNumber <= golfClub.getCourses().size()) {
                CourseDefinition course = golfClub.getCourseNumber(courseNumber);

                String holesList[] = numHoles[1].split(",");
                for (String hole : holesList) {
                    int holeNumber = NumberUtils.toInt(hole, 0);
                    if (holeNumber > 0 && holeNumber <= course.getHoleCount()) {
                        list.add(course.getHoleNumber(holeNumber));
                    }
                }
            }
        }

        return list;
    }

    private class CustomComboRenderer extends JComboBox implements TableCellRenderer {

        private CustomComboRenderer(ComMode[] values) {
            super(values);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(Color.black);
            setBackground(UIManager.getColor("Button.background"));

            if (value != null) {
                if ((Integer) value == ComMode.MOBILE.id) {
                    setSelectedItem(ComMode.MOBILE);
                } else {
                    //setSelectedItem(ComMode.PRINTER);
                }
            }
            return this;
        }
    }

    public static class CustomComboEditor extends DefaultCellEditor {

        private JComboBox comboBox;

        public CustomComboEditor(JComboBox comboBox) {
            super(comboBox);
            this.comboBox = comboBox;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            comboBox.setForeground(Color.black);
            comboBox.setBackground(UIManager.getColor("Button.background"));

            if (value != null) {
                if ((Integer) value == ComMode.MOBILE.id) {
                    comboBox.setSelectedItem(ComMode.MOBILE);
                } else {
                    //  comboBox.setSelectedItem(ComMode.PRINTER);
                }
            }
            return comboBox;
        }

        public Object getCellEditorValue() {
            return comboBox.getSelectedItem();
        }
    }

    private class CustomButtonRenderer extends JButton implements TableCellRenderer {

        public CustomButtonRenderer() {
            setOpaque(true);

        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(Color.black);
            setBackground(UIManager.getColor("Button.background"));

            String hole = setSponsoredHolesLabel(value == null ? "" : value.toString());
            setText(hole);
            return this;
        }
    }

    class CustomTableEditor extends DefaultCellEditor {

        private JButton button;
        private String hole;
        private boolean clicked;

        public CustomTableEditor(JTextField textField) {
            super(textField);
            setClickCountToStart(1);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setForeground(Color.black);
            button.setBackground(UIManager.getColor("Button.background"));
            hole = setSponsoredHolesLabel(value == null ? "" : value.toString());
            button.setText(hole);
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                HutsInfo hutInfo = getViewList().get(getTable().getSelectedRow());
                hole = showHoleSelectionDialog(hutInfo.getHoles());
            }
            clicked = false;
            return hole;
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
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
            jtf.setText("+");
            jtf.setEditable(true);
            jtf.setHighlighter(null);
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
                      if(jtf.getCaretPosition()==0){
                        ke.consume();
                    }
                      else if (jtf.getText().length() == 1 && (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                        ke.consume();
                    } else if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                        ke.consume();
                    }

                    //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void keyPressed(KeyEvent ke) {
                    char c = ke.getKeyChar();
                    if(jtf.getCaretPosition()==0){
                        ke.consume();
                    }
                    else if (jtf.getText().length() == 1 && (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                        ke.consume();
                    } else if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
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

    private static class TypeEditor extends DefaultCellEditor implements TableCellRenderer {

        JComboBox combo;

        public TypeEditor(JComboBox combo) {
            super(combo);
            this.combo = combo;
            combo.setFocusable(false);
            setClickCountToStart(1);

        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected,
                int row, int column) {
            if (value instanceof Integer) {
                return (JComboBox) super.getTableCellEditorComponent(table, combo.getItemAt((Integer) value), isSelected, row, column);
            } else {
                return (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        }

        @Override
        public Object getCellEditorValue() {
            return combo.getSelectedIndex();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return getTableCellEditorComponent(table, value, isSelected, row, column);
        }
    }

}

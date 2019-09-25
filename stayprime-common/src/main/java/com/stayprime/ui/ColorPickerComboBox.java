/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author benjamin
 */
public class ColorPickerComboBox extends JComboBox implements TableCellRenderer {
    private Color selectedColor = Color.white;

    private String pickColorString = "Pick color...";
    private final ColorChooserComboBoxModel colorChooserComboBoxModel;

    public ColorPickerComboBox() {
        colorChooserComboBoxModel = new ColorChooserComboBoxModel(new Object[] {Color.WHITE, Color.RED, new Color(0x0066FF), Color.BLACK, new Color(0xF0CA00), pickColorString});
        setModel(colorChooserComboBoxModel);
        setEditor(new ColorComboBoxEditor());
        setEditable(true);

        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jlist, Object value, int i, boolean bln, boolean bln1) {
                if(value instanceof Color) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(jlist, " ", i, bln, bln1);
                    label.setBackground((Color)value);
                    return label;
                }
                else {
                    return super.getListCellRendererComponent(jlist, value, i, bln1, bln1);
                }

            }
        });
    }

    public String getPickColorString() {
        return pickColorString;
    }

    public void setPickColorString(String pickColorString) {
        colorChooserComboBoxModel.removeElement(this.pickColorString);
        colorChooserComboBoxModel.addElement(pickColorString);
        this.pickColorString = pickColorString;
    }

    @Override
    public void setSelectedItem(Object obj) {
        colorChooserComboBoxModel.setSelectedItem(obj);
        if(obj instanceof Color) {
            JLabel editor = (JLabel) getEditor().getEditorComponent();
            editor.setBackground((Color) obj);
            selectedColor = (Color) obj;
        }
    }

    @Override
    public Color getBackground() {
        return selectedColor;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setSelectedItem(value);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
    }

    public static DefaultCellEditor createDefaultCellEditor(JComboBox colorPicker) {
        return new DefaultCellEditor(colorPicker) {
            @Override
            public boolean isCellEditable(EventObject anEvent) {
                if (anEvent instanceof MouseEvent) {
                    return ((MouseEvent) anEvent).getID() == MouseEvent.MOUSE_PRESSED
                            && ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
                }
                return true;
            }
        };
    }

    private class ColorChooserComboBoxModel extends DefaultComboBoxModel {

        public ColorChooserComboBoxModel(Object[] items) {
            super(items);
        }

        @Override
        public void setSelectedItem(Object obj) {
            Color color = null;
            if (obj == pickColorString) {
                color = JColorChooser.showDialog(ColorPickerComboBox.this, "Choose tee box color", Color.red);
            }
            else if(obj instanceof Color) {
                color = (Color) obj;
            }

            if (color != null) {
                int index = indexOf(color);
                if(index == -1) {
                    index = this.getSize() - 1;
                    insertElementAt(color, index);
                }
                super.setSelectedItem(color);
            }
        }

        public int indexOf(Object c) {
            if(c != null) {
                for (int i = 0; i < getSize(); i++)
                    if (c.equals(getElementAt(i)))
                        return i;
            }
            return -1;
        }
    }

    private static class ColorComboBoxEditor implements ComboBoxEditor {
        private JLabel label;
        private Color color;
        public ColorComboBoxEditor() {
            label = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(color == null? Color.white : color);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
        }

        @Override
        public Component getEditorComponent() {
            return label;
        }

        @Override
        public void setItem(Object item) {
            label.setOpaque(true);
            if (item instanceof Color) {
                Color color = (Color) item;
                this.color = color;
                label.setBackground(color);
            }
        }

        @Override
        public Object getItem() {
            return color;
        }

        @Override
        public void selectAll() {
        }

        @Override
        public void addActionListener(ActionListener l) {
        }

        @Override
        public void removeActionListener(ActionListener l) {
        }
    }
}

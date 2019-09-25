/*
 * 
 */
package com.stayprime.basestation2.ui.custom;

import com.stayprime.ui.swing.IconCache;
import ca.odell.glazedlists.EventList;
import com.stayprime.hibernate.entities.MenuItems;
import com.stayprime.util.file.FileUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author benjamin
 */
public class MenuIconEditor extends JButton implements TableCellEditor, TableCellRenderer {
    private IconCache icons;
    private List<CellEditorListener> cellEditorListeners;
    private JFileChooser fileChooser;
    private javax.swing.JPopupMenu iconPopup;
    private int editingRow = -1, editingCol = -1;
    private Object value;
    private EventList<MenuItems> list;
    private MenuItems selected;
    private String pictureText = "Load...";
    private int rowHeight = 45;
    private final Icon[] defaultIcons = new Icon[] {
        new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/food.png")),
        new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/drink.png")),
        new javax.swing.ImageIcon(getClass().getResource("/com/stayprime/basestation2/resources/snack.png"))
    };
    private ImageIcon icon;

    public MenuIconEditor() {
        icons = new IconCache();
        icons.setMaxHeight(rowHeight);
        cellEditorListeners = new ArrayList<>();

        setFocusable(false);

        iconPopup = new javax.swing.JPopupMenu();
        final JMenuItem chooseFile = new javax.swing.JMenuItem();
        final JMenuItem clear = new javax.swing.JMenuItem();
        iconPopup.setName("iconPopup"); // NOI18N
        chooseFile.setText("Choose Icon...");
        chooseFile.setName("chooseFile"); // NOI18N
        iconPopup.add(chooseFile);
        clear.setText("Clear (use default)");
        clear.setName("clear"); // NOI18N
        iconPopup.add(clear);
        iconPopup.setInvoker(this);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                String ext = getExtension(f.getName());
                return ext.equals("png") || ext.equals("jpg")
                        || ext.equals("jpeg") || ext.equals("gif");
            }

            @Override
            public String getDescription() {
                return "Image file";
            }

            private String getExtension(String name) {
                int dot = name.lastIndexOf(".");
                if (dot >= 0) {
                    return name.substring(dot + 1).toLowerCase();
                }
                return "";
            }
        });
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point loc = getLocationOnScreen();
                Dimension dim = getSize();
                iconPopup.setLocation(loc.x + dim.width / 2, loc.y + dim.height / 2);
                iconPopup.setVisible(true);
                iconPopup.requestFocus();
                chooseFile.requestFocus();
            }
        });

        chooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iconPopup.setVisible(false);
                if (fileChooser.showOpenDialog(MenuIconEditor.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        selectIcon(fileChooser.getSelectedFile());
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(MenuIconEditor.this,
                                "Unknown error while loading icon file.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    stopCellEditing();
                }
            }

        });
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectIcon(null);
                stopCellEditing();
            }
        });
    }

    public Icon getDefaultIcon() {
        if (selected != null) {
            int type = selected.getType();
            if (type < defaultIcons.length) {
                return defaultIcons[type];
            }
        }
        return defaultIcons[0];
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getTableCellEditorComponent(table, value, isSelected, row, column);
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.selected = list == null? null : list.get(row);
        this.value = value;
        editingRow = row;
        editingCol = column;
        loadIcon((String)value);
        return this;
    }

    private void loadIcon(String string) {
        icon = getIcon(string);
        setText(null);

        if (icon != null) {
            setIcon(icon);
        }
        else if (editingCol == 0) {
            setIcon(getDefaultIcon());
        }
        else {
            setIcon(null);
            setText(pictureText);
        }
    }

    public ImageIcon getIcon(String name) {
        if (name == null) {
            return null;
        }
        else {
            return icons.getIcon(name);
        }
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        editingRow = -1;
        //editingCol is not cleared to be able to set value with filechooser
        iconPopup.setVisible(false);
        List<CellEditorListener> oldListeners = new ArrayList<>();
        oldListeners.addAll(cellEditorListeners);
        for (CellEditorListener l : oldListeners) {
            l.editingStopped(changeEvent);
        }
        return true;
    }

    @Override
    public void cancelCellEditing() {
        editingRow = -1;
        //editingCol is not cleared to be able to set value with filechooser
        iconPopup.setVisible(false);
        if (fileChooser != null) {
            fileChooser.cancelSelection();
        }
        List<CellEditorListener> oldListeners = new ArrayList<>();
        oldListeners.addAll(cellEditorListeners);
        for (CellEditorListener l : oldListeners) {
            l.editingCanceled(changeEvent);
        }
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        if (!cellEditorListeners.contains(l)) {
            cellEditorListeners.add(l);
        }
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        cellEditorListeners.remove(l);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (iconPopup != null) {
            SwingUtilities.updateComponentTreeUI(iconPopup);
            SwingUtilities.updateComponentTreeUI(fileChooser);
        }
    }

    public void setList(EventList<MenuItems> list) {
        this.list = list;
    }

    private void selectIcon(File file) {
        String path = file == null ? null : file.getAbsolutePath();
        setIcon(getIcon(path));
        iconPopup.setVisible(false);

        if (list != null && selected != null) {
            int i = list.indexOf(selected);
            if (editingCol == 0) {
                selected.setThumb(path);
            }
            else if (editingCol == 1) {
                selected.setPicture(path);
            }
            list.set(i, selected);
            value = path;
        }

        if (file != null) {
            FileUtils.setReadableForAllUsers(file);
        }
    }

    @Override
    public void paint(Graphics g) {
        if (icon == null) {
            super.paint(g);
        }
        else {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.white);
            int w = getWidth();
            g2.fillRect(0, 0, w, getHeight());
            g2.drawImage(icon.getImage(), (w/2 - icon.getIconWidth()/2), 0, null);
        }
    }

}


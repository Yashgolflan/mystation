/*
 * 
 */
package com.stayprime.basestation.ui.site;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import com.stayprime.ui.editor.Factory;
import com.stayprime.ui.editor.ListEditor;
import com.stayprime.hibernate.entities.Holes;
import com.stayprime.hibernate.entities.TeeBoxes;
import com.stayprime.ui.ColorPickerComboBox;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import org.apache.commons.collections.CollectionUtils;

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
        setHolesTeeBoxes();
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
                    teeBoxes.add(hole.createTeeBox());
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
            else if (column == i++)
                return teeBox.getDistance(); //Distance
            throw new IllegalArgumentException("Illegal column index " + column);
        }

        @Override
        public boolean isEditable(TeeBoxes teeBox, int column) {
            int i = 0;
            if (column == i++)
                return true; //Color
            else if (column == i++)
                return true; //Distance
            throw new IllegalArgumentException("Illegal column index " + column);
        }

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
                teeBox.setDistance(editedValue.toString()); //Distance
                setModified(true);
            }
            return teeBox;
        }
    }

}

/*
 * 
 */
package com.stayprime.basestation2.ui.fnb;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import com.stayprime.comm.enums.ComMode;
import com.stayprime.hibernate.entities.HutsInfo;
import java.util.Comparator;

/**
 *
 * @author Omer
 */
public class HutTableFormat implements AdvancedTableFormat<HutsInfo>, WritableTableFormat<HutsInfo> {

    private static ComparableComparator comparableComparator = new ComparableComparator();

    public int getColumnCount() {
        return 5;
    }

    public String getColumnName(int column) {
        int i = 0;
        if (column == i++)
            return "Hut Number";
        else if (column == i++)
            return "Type";
        else if (column == i++)
            return "FnB Number";
        else if (column == i++)
            return "Email Address";
        else if (column == i++)
            return "Holes";
        throw new IllegalStateException();
    }

    public Object getColumnValue(HutsInfo hut, int column) {
        int i = 0;
         if (column == i++)
            return hut.getHutNumber();
         else if (column == i++)
            return hut.getType();
        else if (column == i++)
            return hut.getPhoneNumber();       
        else if (column == i++)
            return hut.getEmail();
        else if (column == i++) {
            return hut.getHoles();
        }
        throw new IllegalStateException();
    }

    public Class getColumnClass(int column) {
        int i = 0;
        if (column == i++)
            return Integer.class; //hutnumber
        else if (column == i++)
            return Integer.class; //type
        else if (column == i++)
            return String.class; //phone         
        else if (column == i++)
            return String.class; //email
        else if (column == i++)
            return String.class; //holes         

        throw new IllegalStateException();
    }

    @Override
    public Comparator getColumnComparator(int column) {
        return comparableComparator;
    }

    @Override
    public boolean isEditable(HutsInfo e, int column) {
        return column != 0;
    }

    @Override
    public HutsInfo setColumnValue(HutsInfo e, Object o, int column) {
        int i = 0 ;
        if (column == i++ )
            e.setHutNumber((Integer) o);
        if (column == i++ )
            e.setType(((ComMode)o).getComMode());
        else if (column == i++ )
            e.setPhoneNumber((String) o);
        else if (column == i++ )
            e.setEmail((String) o);
        else if (column == i++ ) {
            e.setHoles((String)o);
        }
        return e;
    }
}

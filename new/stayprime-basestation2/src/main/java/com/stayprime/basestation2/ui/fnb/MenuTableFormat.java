/*
 * 
 */
package com.stayprime.basestation2.ui.fnb;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import com.stayprime.hibernate.entities.MenuItems;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Omer
 */
public class MenuTableFormat implements AdvancedTableFormat<MenuItems>, WritableTableFormat<MenuItems> {
    private final NumberFormat priceFormat;

    public MenuTableFormat() {
        priceFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    public void setCurrency(String currencyCode) {
        priceFormat.setCurrency(Currency.getInstance(currencyCode));
    }

    private static ComparableComparator comparableComparator = new ComparableComparator();

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int column) {
        int i = 0;
        if (column == i++) {
            return "Icon";
        }
        else if (column == i++) {
            return "Picture";
        }
        else if (column == i++) {
            return "Type";
        }
        else if (column == i++) {
            return "Code";
        }
        else if (column == i++) {
            return "Name";
        }
        else if (column == i++) {
            return "Description";
        }
        else if (column == i++) {
            return "Price";
        }
        throw new IllegalStateException();
    }

    @Override
    public Object getColumnValue(MenuItems e, int column) {
        int i = 0;
        if (column == i++) {
            return e.getThumb();
        }
        else if (column == i++) {
            return e.getPicture();
        }
        else if (column == i++) {
            return e.getType();
        }
        else if (column == i++) { //Code
            return e.getName();
        }
        else if (column == i++) { //Name
            String name = getName(e);
            return StringUtils.isNotBlank(name) ? name : e.getName();
        }
        else if (column == i++) { //Desc
            return getDescription(e);
        }
        else if (column == i++) {
            return priceFormat.format(e.getPrice());
        }
        throw new IllegalStateException();
    }

    @Override
    public Class getColumnClass(int column) {
        int i = 0;
        if (column == i++) {
            return String.class;
        }
        else if (column == i++) {
            return String.class;
        }
        else if (column == i++) {
            return Integer.class; //type
        }
        else if (column == i++) {
            return String.class; //code
        }
        else if (column == i++) {
            return String.class; //name
        }
        else if (column == i++) {
            return String.class; //desc
        }
        else if (column == i++) {
            return String.class; //price
        }
        throw new IllegalStateException();
    }

    @Override
    public Comparator getColumnComparator(int column) {
        return comparableComparator;
    }

    @Override
    public boolean isEditable(MenuItems e, int column) {
        return true;
    }

    @Override
    public MenuItems setColumnValue(MenuItems e, Object o, int column) {
        int i = 0;
        if (column == i++) {
            e.setThumb((String) o);
        }
        else if (column == i++) {
            e.setPicture((String) o);
        }
        else if (column == i++) {
            e.setType((Integer) o);
        }
        else if (column == i++) {  //Code
            e.setName((String) o);
        }
        else if (column == i++) { //Name
            e.setDescription((String) o + ";" + getDescription(e));
        }
        else if (column == i++) { //Desc
            e.setDescription(getName(e) + ";" + (String) o);
        }
        else if (column == i++) {
            return parsePrice(e, (String) o);
        }
        return e;
    }

    private MenuItems parsePrice(MenuItems e, String s) {
        try {
            e.setPrice(priceFormat.parse(s).floatValue());
            return e;
        }
        catch (ParseException ex) {}

        e.setPrice(Float.valueOf(s));
        return e;
    }

    private String getName(MenuItems e) {
        if (StringUtils.isNotBlank(e.getDescription())) {
            String[] nameCode = e.getDescription().split(";", 2);
            if (nameCode.length > 0) {
                return nameCode[0];
            }
        }
        else {
            return e.getName();
        }
        return StringUtils.EMPTY;
    }

    private String getDescription(MenuItems e) {
        if (e != null && StringUtils.isNotBlank(e.getDescription())) {
            String[] nameCode = e.getDescription().split(";", 2);
            if (nameCode.length == 2) {
                return nameCode[1];
            }
        }
        return StringUtils.EMPTY;
    }

}

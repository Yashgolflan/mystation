/*
 * 
 */
package com.stayprime.ui.swing;

/**
 * Listener called by ListEditor on double click on a table row.
 * @author benjamin
 */
public interface TableActionListener<T> {

    public void actionPerformed(T obj, int row, int col);

}

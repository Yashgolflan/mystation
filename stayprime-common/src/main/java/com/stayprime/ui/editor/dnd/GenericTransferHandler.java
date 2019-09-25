/*
 * 
 */
package com.stayprime.ui.editor.dnd;

import ca.odell.glazedlists.EventList;
import com.stayprime.ui.editor.ListEditor;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 * Implements drag and drop support for ListEditor's table.
 * The transferable objects are of generic type T.
 * @author benjamin
 * @param <T> the type of the transferable objects to handle.
 */
public class GenericTransferHandler<T> extends TransferHandler {
    protected final DataFlavor dataFlavor;
    protected ListEditor listEditor;
    protected JTable table;

    //Keep track of exported and imported objects to detect same table dnd
    protected Object exported;
    protected Object imported;
    //Keep track of the imported object position in the table
    protected int exportRow = -1;
    protected int exportCol = -1;
    protected int importRow = -1;
    protected int importCol = -1;

    public GenericTransferHandler(ListEditor listEditor, DataFlavor dataFlavor) {
        this.listEditor = listEditor;
        this.table = listEditor.getTable();
        this.dataFlavor = dataFlavor;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        //Call overridable method to get the transferable object
        T obj = getTransferableObject();

        //Keep record of exported object, and clear imported to detect same
        //table drag and drop
        exported = obj;
        exportRow = table.getSelectedRow();
        exportCol = table.getSelectedColumn();
        imported = null;
        importRow = -1;
        importCol = -1;

        if (obj != null) {
            return new DataHandler(obj, dataFlavor.getMimeType());
        }

        return null;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        boolean supportedDrop = info.isDrop() && info.isDataFlavorSupported(dataFlavor);
        table.setCursor(supportedDrop ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return supportedDrop;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        //Get object from Transferable
        T obj = getObject(info.getTransferable());
        if (obj == null) {
            return false;
        }

        //Get position in table
        JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
        int index = dl.getRow();

        if (index >= 0 && index <= table.getRowCount()) {
            importRow = index;
            importCol = dl.getColumn();
            boolean importData = importData(obj, importRow, importCol);

            if (importData) {
                imported = obj;
                if (imported != exported) {
                    //If this is not the same table dnd, select the imported object
                    //If it's the same table we shouldn't select it yet because
                    //the selected item will be used to remove the source object
                    selectImportedObject();
                }
            }
            else {
                imported = null;
                importRow = -1;
                importCol = -1;
            }
            return importData;
        }
        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        if ((act == TransferHandler.MOVE) || (act == TransferHandler.NONE)) {
            c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        T obj = getObject(t);
        if (obj != null && act != TransferHandler.NONE) {
            removeExported(obj);
        }
        if (exported == imported) {
            selectImportedObject();
        }
        exported = null;
        exportRow = -1;
        exportCol = -1;
        imported = null;
        importRow = -1;
        importCol = -1;
    }

    /**
     * Extract object for data transfer from the list.
     * @return extracted object for data transfer.
     */
    protected T getTransferableObject() {
        //Create transferable object directly from the list
        return (T) listEditor.getViewList().get(listEditor.getTable().getSelectedRow());
    }

    /**
     * Import object into the list.
     * @param obj object to import
     * @param row row index to import into
     * @param col column index to import into
     * @return true if the data was imported successfully
     */
    protected boolean importData(T obj, int row, int col) {
        EventList list = listEditor.getViewList();

        //Detect if the object is already in the list
        int prevIndex = list.indexOf(obj);

        if (prevIndex >= 0) {
            //Already in the list, move object position
            list.remove(prevIndex);
            if (prevIndex < row) {
                //The object is before the import row and will be removed
                //decrease the import row to insert in the right position
                importRow--;
                row--;
            }
            list.add(row, obj);
        }
        else {
            //Not in the list, add the object to the list
            list.add(row, obj);
        }

        return true;
    }

    /**
     * Cleanup once the object export is finished.
     * @param obj exported object
     */
    protected void removeExported(T obj) {
        if (obj != imported) {
            //If the exported object is the same as the imported,
            //don't remove it from the list
            listEditor.getSourceList().remove(obj);
        }
    }

    protected T getObject(Transferable t) {
        try {
            return (T) t.getTransferData(dataFlavor);
        }
        catch (Exception ex) {
            return null;
        }
    }

    private void selectImportedObject() {
        try {
            if (importRow >= 0) {
                table.getSelectionModel().setSelectionInterval(importRow, importRow);
                if (importCol >= 0) {
                    table.getColumnModel().getSelectionModel().setSelectionInterval(importCol, importCol);
                }
            }
        }
        catch (Exception ex) {
        }
    }

}

/*
 * 
 */
package com.stayprime.ui.editor;

import com.stayprime.ui.DocumentChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import org.jdesktop.swingx.JXPanel;

/**
 * Base class to create a panel for editing an object of type T.
 * @author benjamin
 * @param <T> the type of object this panel edits
 */
public class EditorPanel<T> extends JXPanel {
    private EditorPanel parentEditorPanel;
    private List<EditorPanel> nestedEditorPanels;
    private PropertyChangeListener nestedPanelListener;
    private ModificationListener modificationListener;
    private T editingObject;
    private boolean refreshing;

    public EditorPanel() {
    }

    /**
     * Creates and keeps a listener that implements different change listeners.
     * For now it only implements document listener but it's easy to add others.
     * @return the ModificationListener instance for this EditorPanel
     */
    protected ModificationListener getModificationListener() {
        if (modificationListener == null) {
            modificationListener = new ModificationListener();
        }
        return modificationListener;
    }

    /**
     * Adds a nested EditorPanel to this one to track modifications.
     * @param editPanel the nested EditorPanel.
     */
    public void addNestedEditorPanel(EditorPanel editPanel) {
        if (nestedEditorPanels == null) {
            nestedEditorPanels = new ArrayList<EditorPanel>();
            nestedPanelListener = new NestedPanelListener();
        }

        nestedEditorPanels.add(editPanel);
        editPanel.setParentEditorPanel(this);
        editPanel.addPropertyChangeListener(PROP_MODIFIED, nestedPanelListener);
    }

    public T getEditingObject() {
        return editingObject;
    }

    public void setEditingObject(T editingObject) {
        this.editingObject = editingObject;
        refreshAll();
        if (editingObject == null) {
            setModified(false);
        }
    }

    public EditorPanel getParentEditorPanel() {
        return parentEditorPanel;
    }

    public void setParentEditorPanel(EditorPanel parentEditorPanel) {
        this.parentEditorPanel = parentEditorPanel;
    }

    /**
     * Calls applyChanges on all the nested panels.
     * @return true if changes were detected and applied
     */
    public boolean applyChanges() {
        boolean changesApplied = false;
        if (nestedEditorPanels != null) {
            for (EditorPanel editPanel: nestedEditorPanels) {
                changesApplied |= editPanel.applyChanges();
            }
        }
        return changesApplied;
    }

    /**
     * Reload the properties of the current editing object.
     * This implementation just calls refresh on any nested panels.
     */
    protected void refreshAll() {
        refreshing = true;
        refresh();
        refreshNestedPanels();
        refreshing = false;
    }

    public void refresh() {
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    protected void refreshNestedPanels() {
        if (nestedEditorPanels != null) {
            for (EditorPanel editPanel: nestedEditorPanels) {
                editPanel.refreshAll();
            }
        }
    }

    private boolean modified;
    public static final String PROP_MODIFIED = "modified";

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        if (isRefreshing()) return;
        boolean oldModified = this.modified;
        this.modified = editingObject != null && modified;
        firePropertyChange(PROP_MODIFIED, oldModified, modified);

        //Sets modified to false on all nested panels
        if (modified == false && nestedEditorPanels != null) {
            for (EditorPanel editorPanel: nestedEditorPanels) {
                editorPanel.setModified(false);
            }
        }
    }

    public void saveChanges() {
    }

    private class NestedPanelListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (PROP_MODIFIED.equals(evt.getPropertyName()) && evt.getNewValue() == Boolean.TRUE) {
                setModified(true);
            }
        }
    }

    private class ModificationListener extends DocumentChangeListener {
        @Override
        public void documentChanged(DocumentEvent e) {
            if (editingObject != null) {
                setModified(true);
            }
        }
    }
}

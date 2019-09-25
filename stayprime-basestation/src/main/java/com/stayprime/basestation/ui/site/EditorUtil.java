/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation.ui.site;

import com.stayprime.ui.editor.EditorPanel;
import javax.swing.JOptionPane;

/**
 *
 * @author benjamin
 */
public class EditorUtil {
    public static final String prop_exitScreen = "exitScreen";

    public static boolean askSaveChanges(EditorPanel editor) {
	if(editor.isModified()) {
	    int result = JOptionPane.showConfirmDialog(editor, "Do you want to save the changes?", "Question", JOptionPane.YES_NO_CANCEL_OPTION);

            if(result == JOptionPane.YES_OPTION) {
                editor.putClientProperty(prop_exitScreen, Boolean.TRUE);
		editor.saveChanges();
		return true;
	    }
            if(result==JOptionPane.CANCEL_OPTION){
                return false;
            }
	    else {
                return result == JOptionPane.NO_OPTION;
            }
	}
        return true;
    }
    
}

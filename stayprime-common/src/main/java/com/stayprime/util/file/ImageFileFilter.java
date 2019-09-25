/*
 * 
 */
package com.stayprime.util.file;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author benjamin
 */
public class ImageFileFilter extends FileFilter {
    private boolean lookForXML;
    private String extensions[];

    public ImageFileFilter(String[] extensions, boolean lookForXMLFile) {
        this.extensions = extensions;
        this.lookForXML = lookForXMLFile;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return false;
        }
        int dot = f.getName().lastIndexOf('.');
        if (dot != -1) {
            String suffix = f.getName().substring(dot + 1);
            for (String ext : extensions) {
                if (suffix.equalsIgnoreCase(ext)) {
                    if (!lookForXML) {
                        return true;
                    }

                    File xml = new File(f.getParent(), f.getName().substring(0, dot) + ".xml");
                    if (xml.exists()) {
                        return true;
                    }
                    xml = new File(f.getParent(), f.getName().substring(0, dot) + ".XML");
                    if (xml.exists()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Image file";
    }
}


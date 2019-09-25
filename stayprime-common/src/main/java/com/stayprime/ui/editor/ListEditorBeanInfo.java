/*
 * 
 */
package com.stayprime.ui.editor;

import java.beans.*;

/**
 *
 * @author benjamin
 */
public class ListEditorBeanInfo extends SimpleBeanInfo {
    @Override
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(ListEditor.class);
        desc.setValue("containerDelegate", "getContainerDelegate");
        return desc;
    }
}

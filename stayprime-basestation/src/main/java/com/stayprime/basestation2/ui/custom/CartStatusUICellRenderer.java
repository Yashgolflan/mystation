/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NotificationCellRenderer.java
 *
 * Created on 25/03/2011, 02:30:41 AM
 */

package com.stayprime.basestation2.ui.custom;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Paint;
import java.text.DateFormat;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.RectanglePainter;

/**
 *
 * @author benjamin
 */
/*
/*
    Name Change from DetailedCellRenderer to CartStatusUICellRenderer by javed on 10-04-19.
    Name change cause: this is use only in cart status table cell render
*/
public class CartStatusUICellRenderer extends JXPanel implements ListCellRenderer {
    protected JList list;
    protected JTable table;
    protected JLabel empty;
    protected Painter normal, selected;
    protected DateFormat dateFormat;
    protected float fontScale;

    protected int preferredWidth = -1;

    /** Creates new form CartStatusUICellRenderer */
    public CartStatusUICellRenderer() {
	this(1.2f);
    }

    public CartStatusUICellRenderer(float fontScale) {
	this.fontScale = fontScale;
	
        initComponents();
        empty = new JLabel();

	Paint fillNormal = new GradientPaint(0, 0, new Color(0x22888888, true),
		0, 100, new Color(0x22000000, true));
	Paint fillSelected = new GradientPaint(0, 0, new Color(0x66888888, true),
		0, 100, new Color(0x66000000, true));

	normal = new RectanglePainter(2, 2, 0, 2, 6, 6, true, fillNormal, 1f, Color.black);
	selected = new RectanglePainter(2, 2, 0, 2, 6, 6, true, fillSelected, 1.2f, Color.black);
	setBackgroundPainter(normal);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

	if(list != null) {
	    if(getSize().height != getPreferredSize().height) {
		if(list != null && list.getModel() instanceof DefaultListModel) {
		    DefaultListModel model = (DefaultListModel) list.getModel();
		    model.addElement(null);
		    model.removeElementAt(model.getSize() - 1);
		}
	    }
	}
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
	if(preferredWidth == -1)
	    return super.getPreferredSize();
	else
	    return new Dimension(preferredWidth, super.getPreferredSize().height);
    }
    
    public void setPreferredWidth(int preferredWidth) {
	this.preferredWidth = preferredWidth;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rectanglePainter1 = new org.jdesktop.swingx.painter.RectanglePainter();
        messageLabel = new javax.swing.JLabel();
        topRightLabel = new javax.swing.JLabel();
        detailsLabel = new javax.swing.JLabel();
        midRightLabel = new javax.swing.JLabel();
        bottomExtraPanel = new org.jdesktop.swingx.JXPanel();

        rectanglePainter1.setFillPaint(Color.lightGray);

        setBackgroundPainter(rectanglePainter1);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 2, 4));
        setFont(new java.awt.Font("Droid Sans", 0, 10)); // NOI18N
        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        messageLabel.setFont(messageLabel.getFont().deriveFont(messageLabel.getFont().getStyle() | java.awt.Font.BOLD));
        messageLabel.setText("Message");
        messageLabel.setName("messageLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(messageLabel, gridBagConstraints);

        topRightLabel.setFont(topRightLabel.getFont().deriveFont(topRightLabel.getFont().getSize()-2f));
        topRightLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        topRightLabel.setName("topRightLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        add(topRightLabel, gridBagConstraints);

        detailsLabel.setFont(detailsLabel.getFont().deriveFont(detailsLabel.getFont().getSize()-1f));
        detailsLabel.setText("Detailed description");
        detailsLabel.setName("detailsLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(detailsLabel, gridBagConstraints);

        midRightLabel.setFont(midRightLabel.getFont().deriveFont(midRightLabel.getFont().getStyle() | java.awt.Font.BOLD, midRightLabel.getFont().getSize()-1));
        midRightLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        midRightLabel.setName("midRightLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        add(midRightLabel, gridBagConstraints);

        bottomExtraPanel.setOpaque(false);
        bottomExtraPanel.setName("bottomExtraPanel"); // NOI18N
        bottomExtraPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(bottomExtraPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public org.jdesktop.swingx.JXPanel bottomExtraPanel;
    protected javax.swing.JLabel detailsLabel;
    protected javax.swing.JLabel messageLabel;
    protected javax.swing.JLabel midRightLabel;
    private org.jdesktop.swingx.painter.RectanglePainter rectanglePainter1;
    protected javax.swing.JLabel topRightLabel;
    // End of variables declaration//GEN-END:variables

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	messageLabel.setText(value.toString());
	setBackgroundPainter(isSelected? selected : normal);
	return this;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CartTrackingPanel.java
 *
 * Created on 5/05/2011, 09:39:04 PM
 */

package com.stayprime.basestation2.ui.mainview;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.renderers.DashboardCartTrackRenderer;
import com.aeben.golfclub.view.Dashboard;
import com.aeben.golfcourse.util.Formats;
import com.stayprime.basestation2.services.CartService;
import com.stayprime.basestation2.ui.custom.NoViewPortCustomBalloonTip;
import com.stayprime.geo.Coordinates;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.hibernate.entities.CartTracking;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.calendar.DateSelectionModel;

/**
 *
 * @author benjamin
 */
public class CartTrackingPanel extends JPanel {
    private Dashboard dashboard;
    private DashboardCartTrackRenderer trackRenderer;
    private CartBalloonListener balloonListener;

    private float fontIncrease = -2f;
    private boolean adjustingDate = false;
    private NoViewPortCustomBalloonTip cartBalloon;
    private Collection<CartInfo> carts;

    private CartService cartService;
    private ShowTrackTask showTrackTask;

    public CartTrackingPanel() {
        initComponents();
    }

    public void init() {
        setDateFormat();
	trackRenderer = new DashboardCartTrackRenderer();

        addChangeListeners();

        datePicker.getMonthView().setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        balloonListener = new CartBalloonListener();
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    private void setDateFormat() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        datePicker.setFormats(df);
    }

    public void cartInfoUpdated(Collection<CartInfo> carts) {
        this.carts = carts;
    }

    private void addChangeListeners() {
	ItemListener itemListener = new ItemListener() {
            @Override
	    public void itemStateChanged(ItemEvent e) {
		updateTrack();
	    }
	};
	ActionListener actionListener = new ActionListener() {
            @Override
	    public void actionPerformed(ActionEvent e) {
		updateTrack();
	    }
	};
	ChangeListener changeListener = new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		updateTrack();
	    }
	};

	cartNumberComboBox.addItemListener(itemListener);
        timeSlider.addChangeListener(changeListener);
//	pointsSpinner.addChangeListener(changeListener);
//	beforeRadioButton.addActionListener(actionListener);
//	afterRadioButton.addActionListener(actionListener);

	datePicker.addActionListener(actionListener);
    }

    public void setUp() {
//        BaseStationManager manager = BaseStation2App.getApplication().getManager();
//        Map<Integer, CartStatus> carts = manager.getCartsStatus();
//        if (carts != null && carts.isEmpty()) {
//            manager.updateCartsStatus();
//        }
        createCartsComboModel();
        initDatePicker();
	
	if(dashboard != null) {
	    dashboard.addObjectRenderer(trackRenderer);
            dashboard.addMouseListener(balloonListener);
            dashboard.setDashboardListener(balloonListener);
        }
    }

    private void createCartsComboModel() {
        if (carts != null) {
            Integer numbers[] = new Integer[carts.size()];
            int i = 0;
            for (CartInfo cart : carts) {
                numbers[i++] = cart.getCartNumber();
            }
            Arrays.sort(numbers);
            List<Object> model = new ArrayList<Object>(numbers.length + 1);
            model.addAll(Arrays.asList(numbers));
            model.add("All carts");
            cartNumberComboBox.setModel(new DefaultComboBoxModel(model.toArray()));
        }
    }

    private void initDatePicker() {
//	if(datePicker.getDate() == null) {
        Date now = new Date();
        datePicker.setDate(now);
        //hourSpinner.setValue(now);
//	}
    }

    public void cleanup() {
	if(dashboard != null) {
	    dashboard.removeObjectRenderer(trackRenderer);
            dashboard.removeMouseListener(balloonListener);
            dashboard.setDashboardListener(null);
        }
        if (cartBalloon != null) {
            cartBalloon.setVisible(false);
        }
    }

    public void setDashboard(Dashboard dashboard) {
	this.dashboard = dashboard;
        createCartBalloon();
    }

    private void updateTrack() {
	if(adjustingDate == false && trackRenderer.getTrack() != null) {
	    showTrack();
	}
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        cartNumberLabel = new javax.swing.JLabel();
        jXTitledSeparator1 = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        cartNumberComboBox = new javax.swing.JComboBox();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        jToggleButton1 = new javax.swing.JToggleButton();
        previousDateButton = new javax.swing.JButton();
        nextDateButton = new javax.swing.JButton();
        timeSlider = new com.jidesoft.swing.RangeSlider();
        todayButton = new javax.swing.JButton();
        resetTimeButton = new javax.swing.JButton();
        hour0Label = new javax.swing.JLabel();
        hour24Label = new javax.swing.JLabel();
        hour12Label = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(CartTrackingPanel.class);
        cartNumberLabel.setText(resourceMap.getString("cartNumberLabel.text")); // NOI18N
        cartNumberLabel.setName("cartNumberLabel"); // NOI18N

        jXTitledSeparator1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXTitledSeparator1.setTitle(resourceMap.getString("jXTitledSeparator1.title")); // NOI18N
        jXTitledSeparator1.setName("jXTitledSeparator1"); // NOI18N

        cartNumberComboBox.setName("cartNumberComboBox"); // NOI18N
        cartNumberComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cartNumberComboBoxItemStateChanged(evt);
            }
        });

        datePicker.setName("datePicker"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(CartTrackingPanel.class, this);
        jToggleButton1.setAction(actionMap.get("showTrack")); // NOI18N
        jToggleButton1.setName("jToggleButton1"); // NOI18N

        previousDateButton.setText(resourceMap.getString("previousDateButton.text")); // NOI18N
        previousDateButton.setName("previousDateButton"); // NOI18N
        previousDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousDateButtonActionPerformed(evt);
            }
        });

        nextDateButton.setText(resourceMap.getString("nextDateButton.text")); // NOI18N
        nextDateButton.setName("nextDateButton"); // NOI18N
        nextDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextDateButtonActionPerformed(evt);
            }
        });

        timeSlider.setMajorTickSpacing(12);
        timeSlider.setMaximum(24);
        timeSlider.setMinorTickSpacing(1);
        timeSlider.setPaintLabels(true);
        timeSlider.setPaintTicks(true);
        timeSlider.setSnapToTicks(true);
        timeSlider.setLowValue(0);
        timeSlider.setMinimumSize(new java.awt.Dimension(36, 52));
        timeSlider.setName("timeSlider"); // NOI18N
        timeSlider.setPreferredSize(new java.awt.Dimension(200, 52));

        todayButton.setText(resourceMap.getString("todayButton.text")); // NOI18N
        todayButton.setName("todayButton"); // NOI18N
        todayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todayButtonActionPerformed(evt);
            }
        });

        resetTimeButton.setText(resourceMap.getString("resetTimeButton.text")); // NOI18N
        resetTimeButton.setName("resetTimeButton"); // NOI18N
        resetTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetTimeButtonActionPerformed(evt);
            }
        });

        hour0Label.setName("hour0Label"); // NOI18N

        hour24Label.setName("hour24Label"); // NOI18N

        hour12Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hour12Label.setName("hour12Label"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jXTitledSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(datePicker, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previousDateButton)
                .addGap(0, 0, 0)
                .addComponent(nextDateButton))
            .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(timeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(todayButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetTimeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(cartNumberLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cartNumberComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(hour0Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hour12Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hour24Label))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cartNumberComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cartNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXTitledSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(previousDateButton)
                    .addComponent(nextDateButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hour0Label)
                    .addComponent(hour24Label)
                    .addComponent(hour12Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(todayButton)
                    .addComponent(resetTimeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToggleButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void previousDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousDateButtonActionPerformed
        advanceDate(-1);
    }//GEN-LAST:event_previousDateButtonActionPerformed

    private void nextDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextDateButtonActionPerformed
        advanceDate(1);
    }//GEN-LAST:event_nextDateButtonActionPerformed

    private void todayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todayButtonActionPerformed
        advanceDate(0);
    }//GEN-LAST:event_todayButtonActionPerformed

    private void resetTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetTimeButtonActionPerformed
        timeSlider.setLowValue(0);
        timeSlider.setHighValue(24);
    }//GEN-LAST:event_resetTimeButtonActionPerformed

    private void cartNumberComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cartNumberComboBoxItemStateChanged
        updateTrack();
    }//GEN-LAST:event_cartNumberComboBoxItemStateChanged

    private void advanceDate(int days) {
        GregorianCalendar cal = new GregorianCalendar();
        if (days != 0) {
            cal.setTime(datePicker.getDate());
            cal.add(Calendar.DATE, days);
        }
        datePicker.setDate(cal.getTime());
        updateTrack();
    }

    @Action
    public Task showTrack() {
        showTrackTask = new ShowTrackTask(org.jdesktop.application.Application.getInstance());
        return showTrackTask;
    }

    private class ShowTrackTask extends org.jdesktop.application.Task<List<CartTracking>, Void> {
        private boolean connectTrack;
        private Integer cart;
        private GregorianCalendar from;
        private GregorianCalendar to;
        ShowTrackTask(org.jdesktop.application.Application app) {
            super(app);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(datePicker.getDate());
            from = new GregorianCalendar(c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH), c.get(Calendar.DATE),
                    timeSlider.getLowValue(), 0
            );

            Date first = datePicker.getMonthView().getSelection().first();
            Date last = datePicker.getMonthView().getSelection().last();

            if (first.equals(last)) {
                to = new GregorianCalendar(c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH), c.get(Calendar.DATE),
                        timeSlider.getHighValue(), 0
                );
    //                to.add(Calendar.DATE, 1);
            }
            else {
                c.setTime(last);
                to = new GregorianCalendar(c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH), c.get(Calendar.DATE),
                        timeSlider.getHighValue(), 0
                );
            }
            connectTrack = cartNumberComboBox.getSelectedItem() instanceof Integer;
	    jToggleButton1.setAction(Application.getInstance(BaseStation2App.class).getContext().getActionMap(CartTrackingPanel.class, CartTrackingPanel.this).get("clearTrack"));
        }
        @Override protected List<CartTracking> doInBackground() {
            int limit = Integer.MAX_VALUE;

            if (connectTrack) {
                cart = (Integer) cartNumberComboBox.getSelectedItem();
                return cartService.listCartTrack(cart, from.getTime(), to.getTime(), limit);
            }
            else {
                return cartService.listAllCartsTrack(from.getTime(), to.getTime(), limit);
            }
        }
        @Override protected void succeeded(List<CartTracking> track) {
            if (connectTrack) trackRenderer.setCartTrack(cart, track);
            else trackRenderer.setPoints(track);

            if(dashboard != null) dashboard.repaint();

            if (cartBalloon != null) cartBalloon.setVisible(false);
            showTrackTask = null;
        }
    }

    @Action
    public void clearTrack() {
        if (showTrackTask != null) {
            showTrackTask.cancel(false);
            showTrackTask = null;
        }

        trackRenderer.setPoints(null);

	if(dashboard != null) {
	    dashboard.repaint();
        }
        if (cartBalloon != null) {
            cartBalloon.setVisible(false);
        }

	jToggleButton1.setAction(Application.getInstance(BaseStation2App.class).getContext().getActionMap(CartTrackingPanel.class, this).get("showTrack"));
    }

    @Action
    public void setCurrentTime() {
	adjustingDate = true;
	Date date = new Date();
//	try {
//	    secondSpinner.setValue(date);
//	    minuteSpinner.setValue(date);
//	    hourSpinner.setValue(date);
//	    secondSpinner.commitEdit();
//	    minuteSpinner.commitEdit();
//	    hourSpinner.commitEdit();
//	}
//	catch(ParseException ex) {}

	datePicker.setDate(date);
//	beforeRadioButton.setSelected(true);
	adjustingDate = false;
	updateTrack();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cartNumberComboBox;
    private javax.swing.JLabel cartNumberLabel;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JLabel hour0Label;
    private javax.swing.JLabel hour12Label;
    private javax.swing.JLabel hour24Label;
    private javax.swing.JToggleButton jToggleButton1;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator1;
    private javax.swing.JButton nextDateButton;
    private javax.swing.JButton previousDateButton;
    private javax.swing.JButton resetTimeButton;
    private com.jidesoft.swing.RangeSlider timeSlider;
    private javax.swing.JButton todayButton;
    // End of variables declaration//GEN-END:variables

    private void createCartBalloon() {
        if (dashboard != null) {
            if (cartBalloon == null) {
                JLabel label = new JLabel();
                label.setFont(label.getFont().deriveFont(label.getFont().getSize()*0.8f));
                cartBalloon = new NoViewPortCustomBalloonTip(dashboard, label,
                        null, new RoundedBalloonStyle(4, 3, Color.white, Color.black),
                        BalloonTip.Orientation.LEFT_ABOVE,
                        BalloonTip.AttachLocation.ALIGNED, 7, 7, false);
                cartBalloon.setVisible(false);
            }
            else {
                cartBalloon.setAttachedComponent(dashboard);
            }
        }
    }

    private class CartBalloonListener extends MouseAdapter implements Dashboard.DashboardListener {
        //Timestamp to avoid removal of the balloon during the view updates of the same click
        private long timestamp = 0;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (BaseStation2App.getApplication().getDashboardManager().isDrawingToolInstalled()) {
                return;
            }

            Point p = e.getPoint();
            List<CartTracking> track = trackRenderer.getTrack();
            int n = trackRenderer.getNearestTrackPoint(p);
            Point2D viewPoint = trackRenderer.getViewPoint(n);
            boolean show = false;

            if (track != null && n >= 0 && viewPoint != null && p.distance(viewPoint) < 10) {
                CartTracking cart = track.get(n);
                if (cartBalloon != null) {
                    show = true;
                    timestamp = System.currentTimeMillis();
                    StringBuilder text = new StringBuilder("<html>Cart ");
                    text.append(cart.getId().getCartNumber());

                    if (cart.getId().getTimestamp() != null) {
                        text.append("<br>");
                        text.append(Formats.shortDateTimeFormat.format(cart.getId().getTimestamp()));
                    }

                    cartBalloon.setLabelText(text.toString());
                    cartBalloon.setOffset(new Rectangle(
                            (int) (viewPoint.getX() - 7),
                            (int) viewPoint.getY(), 0, 0));
                }
            }

            if (cartBalloon != null) {
                cartBalloon.setVisible(show);
            }
        }

        @Override
        public void pointClicked(Coordinates pointCoordinates) {
        }

        @Override
        public void viewChanged() {
            if (cartBalloon != null && System.currentTimeMillis() - timestamp > 500) {
                cartBalloon.setVisible(false);
            }
        }

    }

}

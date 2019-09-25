/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * NotificationsPanel.java
 *
 * Created on 25/03/2011, 02:28:30 AM
 */
package com.stayprime.basestation2.ui.mainview;

import com.aeben.golfclub.RequestType;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.comm.NotificationsManager;
import com.stayprime.ui.PersistentComboBox;
import com.stayprime.hibernate.entities.ServiceRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.DefaultRowSorter;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import org.joda.time.DateTime;

/**
 *
 * @author benjamin
 */
public class NotificationsPanel extends javax.swing.JPanel implements
        NotificationsManager.NotificationsListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationsPanel.class);

    public List<ServiceRequest> serviceRequests;
    private NotificationsManager notificationsManager;
    private NotificationCellEditor notificationCellEditor;

    protected boolean newNotificationArrived = false;
    public static final String PROP_NEWNOTIFICATIONARRIVED = "newNotificationsArrived";

    protected boolean notificationsListEmpty = true;
    public static final String PROP_NOTIFICATIONSLISTEMPTY = "notificationsListEmpty";

    /**
     * Creates new form NotificationsPanel
     */
    public NotificationsPanel() {
        initComponents();
        serviceRequests = new ArrayList<ServiceRequest>();

        notificationCellEditor = new NotificationCellEditor();
        notificationsTable.getColumnModel().getColumn(0).setCellEditor(notificationCellEditor);
        notificationsTable.getColumnModel().getColumn(0).setCellRenderer(new NotificationCellRenderer());

        ((DefaultRowSorter) notificationsTable.getRowSorter()).setRowFilter(new RowFilter<TableModel, Object>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Object> entry) {
              //  System.out.println(showFilterComboBox.getSelectedIndex()+""+jComboBox1.getSelectedIndex());
                if (showFilterComboBox.getSelectedIndex()==1&&jComboBox1.getSelectedIndex()==1 && entry.getValue(0) instanceof ServiceRequest) {
                    ServiceRequest cartServiceRequest = (ServiceRequest) entry.getValue(0);
                 //  Calendar cal1 = Calendar.getInstance();
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
               //     System.out.println("Date = "+ cal.getTimeInMillis());
                    return cartServiceRequest.getStatus() < RequestType.REQUEST_SERVICED&&(cartServiceRequest.getTime().getTime()-cal.getTime().getTime()>0 );
                }
                else if(showFilterComboBox.getSelectedIndex()==1&&jComboBox1.getSelectedIndex()==0&& entry.getValue(0) instanceof ServiceRequest){
                    ServiceRequest cartServiceRequest = (ServiceRequest) entry.getValue(0);
                 //  Calendar cal1 = Calendar.getInstance();
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -7);
                 //   System.out.println("Date = "+ cal.getTimeInMillis());
                    return cartServiceRequest.getStatus() < RequestType.REQUEST_SERVICED&&(cartServiceRequest.getTime().getTime()-cal.getTime().getTime()>86400200&&cartServiceRequest.getTime().getTime()-cal.getTime().getTime()<626400000 );

                }
                 else if(showFilterComboBox.getSelectedIndex()==0&&jComboBox1.getSelectedIndex()==1&& entry.getValue(0) instanceof ServiceRequest){
                    ServiceRequest cartServiceRequest = (ServiceRequest) entry.getValue(0);
                 //  Calendar cal1 = Calendar.getInstance();
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                   // System.out.println("Date = "+(cartServiceRequest.getTime().getTime()-cal.getTime().getTime()));
                    return (cartServiceRequest.getTime().getTime()-cal.getTime().getTime()>0 );

                }
                  else if(showFilterComboBox.getSelectedIndex()==0&&jComboBox1.getSelectedIndex()==0&& entry.getValue(0) instanceof ServiceRequest){
                    ServiceRequest cartServiceRequest = (ServiceRequest) entry.getValue(0);
                 //  Calendar cal1 = Calendar.getInstance();
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -7);
                   // System.out.println("Date = "+ cal.getTimeInMillis());
                    return (cartServiceRequest.getTime().getTime()-cal.getTime().getTime()>86400200&&cartServiceRequest.getTime().getTime()-cal.getTime().getTime()<626400000 );

                }
                else {
                    return true;
                }
            }
        });
    }

    public void setNotificationsManager(NotificationsManager notificationsManager) {
        this.notificationsManager = notificationsManager;
        if (notificationsManager != null) {
            notificationsManager.addNotificationListener(this);
        }
        notificationCellEditor.setNotificationsManager(notificationsManager);
    }

    public boolean isNewNotificationArrived() {
        return newNotificationArrived;
    }

    protected void setNewNotificationArrived(boolean newNotificationArrived) {
        boolean oldNewNotificationArrived = this.newNotificationArrived;
        this.newNotificationArrived = newNotificationArrived;
        firePropertyChange(PROP_NEWNOTIFICATIONARRIVED, oldNewNotificationArrived, newNotificationArrived);
    }

    public boolean isNotificationsListEmpty() {
        return notificationsListEmpty;
    }

    protected void setNotificationsListEmpty(boolean notificationsListEmpty) {
        boolean oldNotificationsListEmpty = this.notificationsListEmpty;
        this.notificationsListEmpty = notificationsListEmpty;
        firePropertyChange(PROP_NOTIFICATIONSLISTEMPTY, oldNotificationsListEmpty, notificationsListEmpty);
    }

    protected void doExtraNotification() {
        InputStream in = getClass().getResourceAsStream("/com/stayprime/basestation2/resources/alert.mp3");
        playAudioClip(in);
    }

    protected void playAudioClip(InputStream in) {
        try {
            AudioDevice dev = FactoryRegistry.systemRegistry().createAudioDevice();
            Player player = new Player(in, dev);
            player.play();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        purgeButton = new javax.swing.JButton();
        showFilterComboBox = new PersistentComboBox();
        showButton = new javax.swing.JButton();
        notificationsScrollPane1 = new javax.swing.JScrollPane();
        notificationsTable = new javax.swing.JTable();
        jComboBox1 = new PersistentComboBox();

        setName("Form"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(NotificationsPanel.class, this);
        purgeButton.setAction(actionMap.get("purgeRequests")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(NotificationsPanel.class);
        purgeButton.setText(resourceMap.getString("purgeButton.text")); // NOI18N
        purgeButton.setName("purgeButton"); // NOI18N

        showFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Active" }));
        showFilterComboBox.setName("showFilterComboBox"); // NOI18N
        showFilterComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                showFilterComboBoxItemStateChanged(evt);
            }
        });

        showButton.setAction(actionMap.get("switchFilter")); // NOI18N
        showButton.setText(resourceMap.getString("showButton.text")); // NOI18N
        showButton.setName("showButton"); // NOI18N

        notificationsScrollPane1.setName("notificationsScrollPane1"); // NOI18N
        notificationsScrollPane1.setPreferredSize(new java.awt.Dimension(150, 402));

        notificationsTable.setAutoCreateRowSorter(true);
        notificationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Notifications"
            }
        ));
        notificationsTable.setName("notificationsTable"); // NOI18N
        notificationsTable.setRowMargin(2);
        notificationsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        notificationsTable.setShowHorizontalLines(false);
        notificationsTable.setShowVerticalLines(false);
        notificationsTable.setTableHeader(null);
        notificationsScrollPane1.setViewportView(notificationsTable);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "7-day","1-day" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(showButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(purgeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(notificationsScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showButton)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationsScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(purgeButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showFilterComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showFilterComboBoxItemStateChanged
        notificationsTable.clearSelection();
        if (notificationsTable.getRowSorter() != null) {
            ((DefaultRowSorter) notificationsTable.getRowSorter()).sort();
        }
    }//GEN-LAST:event_showFilterComboBoxItemStateChanged

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        notificationsTable.clearSelection();
        if (notificationsTable.getRowSorter() != null) {
            ((DefaultRowSorter) notificationsTable.getRowSorter()).sort();
        }
//        if (notificationsTable.getRowSorter() != null) {
//            ((DefaultRowSorter) notificationsTable.getRowSorter()).sort();
//        }
        
        
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    @Action
    public Task purgeRequests() {
        return new PurgeRequestsTask(org.jdesktop.application.Application.getInstance());
    }

    private class PurgeRequestsTask extends org.jdesktop.application.Task<Object, Void> {
        PurgeRequestsTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to PurgeRequestsTask fields, here.
            super(app);
            
        }
        @Override 
        protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            if (notificationsManager != null) {
            notificationsManager.dismissRepliedRequests();
           // serviceRequestDismissed();
            }
            return null;  // return your result
        }
        @Override 
        protected void succeeded(Object result) {
            
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public void switchFilter() {
        System.out.println("\n***********"+showFilterComboBox.getItemAt(showFilterComboBox.getSelectedIndex()));
        showFilterComboBox.setSelectedIndex((showFilterComboBox.getSelectedIndex() + 1) % showFilterComboBox.getItemCount());
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JScrollPane notificationsScrollPane1;
    private javax.swing.JTable notificationsTable;
    private javax.swing.JButton purgeButton;
    private javax.swing.JButton showButton;
    private javax.swing.JComboBox showFilterComboBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void serviceRequestReceived(final ServiceRequest serviceRequest) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//		
//            }
//
//        });

        /*
            Code commented by javed on 05-04-19
            this was use for loading notification at once in main thread
            if code written below the comment is copied in run then all notification loads once only not one by one
         */
        notificationsTable.clearSelection();
        serviceRequests.add(0, serviceRequest);

        DefaultTableModel model = (DefaultTableModel) notificationsTable.getModel();
        model.insertRow(0, new Object[]{serviceRequest});

        ((DefaultRowSorter) notificationsTable.getRowSorter()).sort();
        setNotificationsListEmpty(notificationsTable.getRowCount() == 0);

        if (serviceRequest.getStatus() < RequestType.REQUEST_SERVICED) {
            setNewNotificationArrived(true);
            doExtraNotification();
        }
    }

    @Override
    public void serviceRequestUpdated(final ServiceRequest serviceRequest) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                notificationsTable.clearSelection();
                //log.info("javed 44 " + notificationsTable.getRowCount());
                int index = findRequest(serviceRequests, serviceRequest.getId());

                if (index >= 0) {
                    serviceRequests.set(index, serviceRequest);

                    DefaultTableModel model = (DefaultTableModel) notificationsTable.getModel();
                    if(model == null)
                       // log.info("javed 55 ");
                    model.setValueAt(serviceRequest, index, 0);

                    ((DefaultRowSorter) notificationsTable.getRowSorter()).sort();
                    //log.info("javed 66 ");
                    setNotificationsListEmpty(notificationsTable.getRowCount() == 0);
                    //log.info("javed 77 ");
                    if (isNewNotificationArrived()) {
                        //log.info("javed 88 isNewNotificationArrived");
                        boolean active = false;
                        for (ServiceRequest r : serviceRequests) {
                            if (r.getStatus() < RequestType.REQUEST_SERVICED) {
                                active = true;
                            }
                        }
                        //log.info("javed 99 for loop complete");
                        if (!active) {
                            setNewNotificationArrived(false);
                            //log.info("javed 100 in active");
                        }
                    }
                }
            }

        });
    }

    private int findRequest(List<ServiceRequest> serviceRequests, Integer id) {
        int index = -1;
        for (int i = 0; i < serviceRequests.size(); i++) {
            if (serviceRequests.get(i).getId().equals(id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void serviceRequestDismissed(final ServiceRequest serviceRequest) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                notificationsTable.clearSelection();

                int index = findRequest(serviceRequests, serviceRequest.getId());

                serviceRequests.remove(index);

                DefaultTableModel model = (DefaultTableModel) notificationsTable.getModel();
                model.removeRow(index);

                ((DefaultRowSorter) notificationsTable.getRowSorter()).sort();
                setNotificationsListEmpty(notificationsTable.getRowCount() == 0);

                if (isNewNotificationArrived()) {
                    boolean active = false;
                    for (ServiceRequest r : serviceRequests) {
                        if (r.getStatus() < RequestType.REQUEST_SERVICED) {
                            active = true;
                        }
                    }
                    if (!active) {
                        setNewNotificationArrived(false);
                    }
                }
            }
        });
    }

}

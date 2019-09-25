/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * OnCourseAdsDetailPanel.java
 *
 * Created on 30/05/2011, 08:16:06 AM
 */
package com.stayprime.basestation2.ui.modules;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.aeben.golfclub.CourseDefinition;
import com.aeben.golfclub.GolfClub;
import com.aeben.golfclub.HoleDefinition;
import com.ezware.dialog.task.TaskDialogs;
import com.stayprime.basestation.ui.FileChooser;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.ui.custom.ItemListComboBoxCellEditor;
import com.stayprime.basestation2.ui.custom.ItemListTableCellRenderer;
import com.stayprime.basestation2.ui.dialog.SelectHolesTreeDialog;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.Ads;
import com.stayprime.hibernate.entities.Clients;
import com.stayprime.hibernate.entities.Contracts;
import com.stayprime.oncourseads.Ad;
import com.stayprime.util.FormatUtils;
import com.stayprime.util.file.FileUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.table.DatePickerCellEditor;

/**
 *
 * @author benjamin
 */
public class OnCourseAdsDetailPanel extends javax.swing.JPanel {

    private static final Logger log = LoggerFactory.getLogger(OnCourseAdsClientsPanel.class);
    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

    private SortedList<Ads> adsList;
    private DefaultEventTableModel<Ads> adsTableModel;

    private List<ChangeListener> contractListeners;
    private ChangeListener forAllContractDeleted; /*  Listener added by javed on 04-04-2019 for updated the UI when ads deleted*/
    private BasicEventList<Contracts> contractsList;
    private DefaultEventTableModel<Contracts> contractsTableModel;
    private ArrayList<Integer> categories;
    private GolfClub golfClub;
    private ItemListComboBoxCellEditor categoryEditor;
    private ItemListTableCellRenderer categoryRenderer;
    private SelectHolesTreeDialog selectHolesTreeDialog;

    private String loadedImage;
    private FileChooser fileChooser;

    /* Bound properties */
    private Clients selectedClient;
    public static final String PROP_SELECTEDCLIENT = "selectedClient";
    public static final String PROP_CLIENTSELECTED = "clientSelected";

    private Ads selectedAd;
    public static final String PROP_SELECTEDAD = "selectedAd";
    public static final String PROP_ADSELECTED = "adSelected";

    private Contracts selectedContract;
    public static final String PROP_SELECTEDCONTRACT = "selectedContract";
    public static final String PROP_CONTRACTSELECTED = "contractSelected";

    private boolean adInformationChanged;
    public static final String PROP_ADINFORMATIONCHANGED = "adInformationChanged";

    private AdFilter adFilter = AdFilter.SHOW_ALL;
    public static final String PROP_ADFILTER = "adFilter";

    /**
     * Creates new form OnCourseAdsDetailPanel
     */
    public OnCourseAdsDetailPanel() {
        initComponents();
        initAdsTable();
        initContractsTable();
        initAdImagePainter();
        addListeners();
    }

    public void setFileChooser(FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    private void initAdsTable() {
        adsList = new SortedList(new BasicEventList<Ads>(), new AdComparator());
        adsTableModel = new DefaultEventTableModel<Ads>(adsList, new AdsTableFormat());
        adsTable.setModel(adsTableModel);
        //Ad type cell editor and renderer
        adsTable.getColumnModel().getColumn(1).setCellEditor(new ItemListComboBoxCellEditor(new Object[]{"Banner Ad", "Static Ad", "Fullscreen Ad"}, true));
        adsTable.getColumnModel().getColumn(1).setCellRenderer(new ItemListTableCellRenderer(new Object[]{"Banner Ad", "Static Ad", "Fullscreen Ad"}, true));
        //Categories list, editor and renderer
        categories = new ArrayList<Integer>();
        categories.add(0);
        categoryEditor = new ItemListComboBoxCellEditor(new Object[0], false);
        categoryRenderer = new ItemListTableCellRenderer(new Object[0], false);
        adsTable.getColumnModel().getColumn(2).setCellEditor(categoryEditor);
        adsTable.getColumnModel().getColumn(2).setCellRenderer(categoryRenderer);

        TableComparatorChooser.install(adsTable, adsList, AbstractTableComparatorChooser.SINGLE_COLUMN);
    }

    private void initContractsTable() {
        contractsList = new BasicEventList<Contracts>();
        contractsTableModel = new DefaultEventTableModel<Contracts>(contractsList, new ContractsTableFormat());
        contractsTable.setModel(contractsTableModel);
        
        ContractDatePickerCellEditor editor = new ContractDatePickerCellEditor();
        DefaultTableCellRenderer renderer = new DateTableCellRenderer();
        editor.setClickCountToStart(1);
        contractsTable.getColumnModel().getColumn(0).setCellEditor(editor);
        contractsTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
        contractsTable.getColumnModel().getColumn(1).setCellEditor(editor);
        contractsTable.getColumnModel().getColumn(1).setCellRenderer(renderer);
       // contractsTable.getColumnModel().getColumn(2).setCellEditor(new ItemListComboBoxCellEditor(new Object[]{"Light", "Moderate", "Heavy"}, true));
        //contractsTable.getColumnModel().getColumn(2).setCellRenderer(new ItemListTableCellRenderer(new Object[]{"Light", "Moderate", "Heavy"}, true));
       // contractsTable.getColumnModel().removeColumn(contractsTable.getColumnModel().getColumn(2));
    }

    private void initAdImagePainter() {
        ImagePainter imagePainter = new ImagePainter();
        imagePainter.setScaleType(ImagePainter.ScaleType.InsideFit);
        imagePainter.setScaleToFit(true);
        imagePanel.setBackgroundPainter(imagePainter);
    }

    private void addListeners() {
        contractListeners = new ArrayList<ChangeListener>();

        UpdateAdsListener adsListener = new UpdateAdsListener();
        adsTable.getSelectionModel().addListSelectionListener(adsListener);
        adsList.addListEventListener(adsListener);

        UpdateContractsListener contractsListener = new UpdateContractsListener();
        contractsTable.getSelectionModel().addListSelectionListener(contractsListener);
        contractsList.addListEventListener(contractsListener);
    }

    /* Public initialization methods */
    public void addContractChangeListener(ChangeListener changeListener) {
        contractListeners.add(changeListener);
    }

    public void setGolfClub(GolfClub golfClub) {
        this.golfClub = golfClub;
    }

    public void setCategoriesList(List<Integer> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        categories.trimToSize();

        categoryEditor.setItems(categories.toArray());
        categoryRenderer.setItems(categories.toArray());
    }

    /* Bound properties */
    public boolean isClientSelected() {
        return selectedClient != null;
    }

    public Clients getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(Clients client) {
        Clients oldSelectedClient = this.selectedClient;
        selectedClient = null;

        if (client != null) {
            if (client.getAdses() == null) {
                client.setAdses(new HashSet());
            }

            setAdsList(client.getAdses());
        } else {
            setAdsList(new HashSet<Ads>());
        }

        selectedClient = client;
        imageFileLabel.setText("");
        firePropertyChange(PROP_SELECTEDCLIENT, oldSelectedClient, selectedClient);
        firePropertyChange(PROP_CLIENTSELECTED, oldSelectedClient != null, selectedClient != null);
    }

    public boolean isAdSelected() {
        return selectedAd != null;
    }

    public Ads getSelectedAd() {
        return selectedAd;
    }

    private void setSelectedAd(Ads ad) {
        Ads oldSelectedAd = selectedAd;
        selectedAd = null;

        contractsList.clear();
        contractsTable.setEnabled(ad != null);

        if (ad != null) {
            if (StringUtils.isBlank(ad.getSource())) {
                loadedImage = null;
                ((ImagePainter) imagePanel.getBackgroundPainter()).setImage(null);
                imageFileLabel.setText("(No image)");
            } else if (ObjectUtils.notEqual(loadedImage, ad.getSource())) {
                loadedImage = ad.getSource();
                ImagePainter painter = (ImagePainter) imagePanel.getBackgroundPainter();
                painter.setImage(loadImage(ad.getSource()));
                if(painter.getImage()==null)
                 imageFileLabel.setText(ad.getSource());
            }

            if (CollectionUtils.isNotEmpty(ad.getContractses())) {
                contractsList.addAll(ad.getContractses());
            }

        } else {
            loadedImage = null;
            ((ImagePainter) imagePanel.getBackgroundPainter()).setImage(null);
        }

        imagePanel.repaint();

        selectedAd = ad;

        firePropertyChange(PROP_SELECTEDAD, oldSelectedAd, selectedAd);
        firePropertyChange(PROP_ADSELECTED, oldSelectedAd != null, selectedAd != null);

        if (selectedAd != null && CollectionUtils.isNotEmpty(selectedAd.getContractses())) {
            contractsTable.getSelectionModel().setSelectionInterval(0, 0);
        }

        setAdInformationChanged(false);
    }

    public boolean isContractSelected() {
        return selectedContract != null;
    }

    public Contracts getSelectedContract() {
        return selectedContract;
    }

    public void setSelectedContract(Contracts selectedContract) {
        Contracts oldSelectedContract = this.selectedContract;
        this.selectedContract = null;

        if (selectedContract != null) {
            setSponsoredHolesLabel(selectedContract.getHoleAd());
        } else {
            setSponsoredHolesLabel(null);
        }
        this.selectedContract = selectedContract;
        firePropertyChange(PROP_SELECTEDCONTRACT, oldSelectedContract, selectedContract);
        firePropertyChange(PROP_CONTRACTSELECTED, oldSelectedContract != null, selectedContract != null);
    }

    public boolean isAdInformationChanged() {
        return adInformationChanged;
    }

    private void setAdInformationChanged(boolean adInformationChanged) {
        boolean oldAdInformationChanged = this.adInformationChanged;
        this.adInformationChanged = adInformationChanged;
        firePropertyChange(PROP_ADINFORMATIONCHANGED, oldAdInformationChanged, adInformationChanged);

        if (adInformationChanged && selectedAd != null) {
            selectedAd.setUpdated(new Date());
        }
    }

    public void discardChanges() {
        setAdInformationChanged(false);
    }

    public AdFilter getAdFilter() {
        return adFilter;
    }

    public void setAdFilter(AdFilter adFilter) {
        AdFilter oldAdFilter = this.adFilter;
        this.adFilter = adFilter;
        firePropertyChange(PROP_ADFILTER, oldAdFilter, adFilter);

        applyFilter();
    }

    /* Utility and private methods */
    public ApplicationActionMap getAppActionMap() {
        return Application.getInstance().getContext().getActionMap(this);
    }

    private void setAdsList(Collection<Ads> ads) {
        adsList.clear();
        if (ads != null) {
            if (adFilter == AdFilter.SHOW_ACTIVE) {
                for (Ads ad : ads) {
                    if (ad.isActive()) {
                        adsList.add(ad);
                    }
                }
            } else if (adFilter == AdFilter.SHOW_INACTIVE) {
                for (Ads ad : ads) {
                    if (!ad.isActive()) {
                        adsList.add(ad);
                    }
                }
            } else {
                adsList.addAll(ads);
            }
        }
    }

    private BufferedImage loadImage(String source) {
        try {
            if (source != null) {
                File file = new File(source);
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            }
        } catch (Throwable t) {
            log.error(t.toString());
//	    TaskDialogs.showException(new RuntimeException("Error loading Ad image", t));
            NotificationPopup.showErrorDialog("Error loading Ad image");
        }
        return null;
    }

    private void applyFilter() {
        // setSelectedClient(selectedClient);
        if (selectedClient != null) {
            if (selectedClient.getAdses() != null) {
                //  client.setAdses(new HashSet());
//                Collection<Ads> filterAds= new HashSet<>();
//                if(adFilter==AdFilter.SHOW_ACTIVE){
//                    for(Ads ad:selectedClient.getAdses()){
//                        if(ad.isActive())
//                            filterAds.add(ad);
//                    }
//                }
//                else if(adFilter==AdFilter.SHOW_INACTIVE){
//                    for(Ads ad:selectedClient.getAdses()){
//                        if(!ad.isActive())
//                            filterAds.add(ad);
//                    }
//                }
//                else{
//                    filterAds=selectedClient.getAdses();
//                }

                setAdsList(selectedClient.getAdses());
            }

            //setAdsList(client.getAdses());
        } else {
            //      setAdsList(new HashSet<Ads>());
        }
        //adsTable.clearSelection();
        //((DefaultRowSorter)adsTable.getRowSorter()).sort();
    }

    private void fireContractChanged() {
        adsTable.repaint();
       // contractsTableModel.notifyAll();
//        contractsTable.repaint();
        
        if (isContractSelected()) {
            for (ChangeListener l : contractListeners) {
                l.stateChanged(new ChangeEvent(getSelectedContract()));
            }
        }
    }
    private void initContractsTable1(){
        contractsTableModel = new DefaultEventTableModel<Contracts>(contractsList, new ContractsTableFormat());
        contractsTable.setModel(contractsTableModel);
        
        ContractDatePickerCellEditor editor = new ContractDatePickerCellEditor();
        DefaultTableCellRenderer renderer = new DateTableCellRenderer();
        editor.setClickCountToStart(1);
       
        contractsTable.getColumnModel().getColumn(0).setCellEditor(editor);
        contractsTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
        contractsTable.getColumnModel().getColumn(1).setCellEditor(editor);
        contractsTable.getColumnModel().getColumn(1).setCellRenderer(renderer);
       // contractsTable.getColumnModel().getColumn(2).setCellEditor(new ItemListComboBoxCellEditor(new Object[]{"Light", "Moderate", "Heavy"}, true));
      //  contractsTable.getColumnModel().getColumn(2).setCellRenderer(new ItemListTableCellRenderer(new Object[]{"Light", "Moderate", "Heavy"}, true));
       // contractsTable.getColumnModel().removeColumn(contractsTable.getColumnModel().getColumn(2));
    }
    private void setSponsoredHolesLabel(String holes) {
        int count = 0;

        if (StringUtils.isNotBlank(holes)) {
            List<HoleDefinition> holeList = getSponsoredHolesList(golfClub, holes);
            count = holeList.size();

            if (count == 1) {
                holeSponsorButton.setText(holeList.get(0).course.getName() + " " + holeList.get(0).number);
            }
        }

        if (count == 0) {
            holeSponsorButton.setText("None");
        } else if (count != 1) {
            holeSponsorButton.setText(count + " holes");
        }
    }

    private List<HoleDefinition> getSponsoredHolesList(GolfClub golfClub, String holes) {
        List<HoleDefinition> list = new ArrayList<HoleDefinition>();
        String[] courses = holes.split(";");

        for (String courseHoles : courses) {
            String[] numHoles = courseHoles.split(":");
            int courseNumber = NumberUtils.toInt(numHoles[0], 0);

            if (courseNumber > 0 && courseNumber <= golfClub.getCourses().size()) {
                CourseDefinition course = golfClub.getCourseNumber(courseNumber);

                String holesList[] = numHoles[1].split(",");
                for (String hole : holesList) {
                    int holeNumber = NumberUtils.toInt(hole, 0);
                    if (holeNumber > 0 && holeNumber <= course.getHoleCount()) {
                        list.add(course.getHoleNumber(holeNumber));
                    }
                }
            }
        }

        return list;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        adsSeparator = new com.stayprime.basestation2.ui.custom.JXTitledSeparator();
        adsTableScrollPane = new javax.swing.JScrollPane();
        adsTable = new javax.swing.JTable();
        adDetailTabbedPane = new javax.swing.JTabbedPane();
        imageTabPanel = new org.jdesktop.swingx.JXPanel();
        imagePanel = new org.jdesktop.swingx.JXPanel();
        imageFileLabel = new org.jdesktop.swingx.JXLabel();
        selectImageButton = new javax.swing.JButton();
        contractsTabPanel = new org.jdesktop.swingx.JXPanel();
        contractsScrollPane = new javax.swing.JScrollPane();
        contractsTable = new javax.swing.JTable();
        newContractButton = new javax.swing.JButton();
        deleteContractButton = new javax.swing.JButton();
        holeSponsorLabel = new javax.swing.JLabel();
        holeSponsorButton = new javax.swing.JButton();
        deleteAdButton = new javax.swing.JButton();
        newAdButton = new javax.swing.JButton();

        setName("Form"); // NOI18N

        adsSeparator.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(OnCourseAdsDetailPanel.class);
        adsSeparator.setTitle(resourceMap.getString("adsSeparator.title")); // NOI18N
        adsSeparator.setName("adsSeparator"); // NOI18N

        adsTableScrollPane.setName("adsTableScrollPane"); // NOI18N
        adsTableScrollPane.setPreferredSize(new java.awt.Dimension(300, 150));

        adsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Type", "Category", "Contract", "Client"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        adsTable.setName("adsTable"); // NOI18N
        adsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        adsTableScrollPane.setViewportView(adsTable);
        adsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        adDetailTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        adDetailTabbedPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        adDetailTabbedPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        adDetailTabbedPane.setName("adDetailTabbedPane"); // NOI18N

        imageTabPanel.setName("imageTabPanel"); // NOI18N
        imageTabPanel.setLayout(new java.awt.BorderLayout());

        imagePanel.setBorder(null);
        imagePanel.setName("imagePanel"); // NOI18N
        imagePanel.setLayout(new java.awt.BorderLayout());

        imageFileLabel.setBorder(null);
        imageFileLabel.setLineWrap(true);
        imageFileLabel.setText(resourceMap.getString("imageFileLabel.text")); // NOI18N
        imageFileLabel.setName("imageFileLabel"); // NOI18N
        imageFileLabel.setTextAlignment(org.jdesktop.swingx.JXLabel.TextAlignment.CENTER);
        imagePanel.add(imageFileLabel, java.awt.BorderLayout.CENTER);

        imageTabPanel.add(imagePanel, java.awt.BorderLayout.CENTER);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(OnCourseAdsDetailPanel.class, this);
        selectImageButton.setAction(actionMap.get("selectImage")); // NOI18N
        selectImageButton.setText(resourceMap.getString("selectImageButton.text")); // NOI18N
        selectImageButton.setActionCommand(resourceMap.getString("selectImageButton.actionCommand")); // NOI18N
        selectImageButton.setName("selectImageButton"); // NOI18N
        imageTabPanel.add(selectImageButton, java.awt.BorderLayout.PAGE_END);

        adDetailTabbedPane.addTab(resourceMap.getString("imageTabPanel.TabConstraints.tabTitle"), imageTabPanel); // NOI18N

        contractsTabPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        contractsTabPanel.setName("contractsTabPanel"); // NOI18N

        contractsScrollPane.setName("contractsScrollPane"); // NOI18N
        contractsScrollPane.setPreferredSize(new java.awt.Dimension(150, 100));

        contractsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Start", "End"
            }
        ));
        contractsTable.setMaximumSize(new java.awt.Dimension(30, 0));
        contractsTable.setName("contractsTable"); // NOI18N
        contractsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        contractsTable.getTableHeader().setReorderingAllowed(false);
        contractsScrollPane.setViewportView(contractsTable);
        if (contractsTable.getColumnModel().getColumnCount() > 0) {
            contractsTable.getColumnModel().getColumn(0).setResizable(false);
            contractsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            contractsTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("contractsTable.columnModel.title0")); // NOI18N
            contractsTable.getColumnModel().getColumn(1).setResizable(false);
            contractsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            contractsTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("contractsTable.columnModel.title1")); // NOI18N
        }
        //contractsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        newContractButton.setAction(actionMap.get("createContract")); // NOI18N
        newContractButton.setName("newContractButton"); // NOI18N

        deleteContractButton.setAction(actionMap.get("deleteContract")); // NOI18N
        deleteContractButton.setName("deleteContractButton"); // NOI18N

        holeSponsorLabel.setText(resourceMap.getString("holeSponsorLabel.text")); // NOI18N
        holeSponsorLabel.setName("holeSponsorLabel"); // NOI18N

        holeSponsorButton.setAction(actionMap.get("editHoleSponsor")); // NOI18N
        holeSponsorButton.setText(resourceMap.getString("holeSponsorButton.text")); // NOI18N
        holeSponsorButton.setName("holeSponsorButton"); // NOI18N

        javax.swing.GroupLayout contractsTabPanelLayout = new javax.swing.GroupLayout(contractsTabPanel);
        contractsTabPanel.setLayout(contractsTabPanelLayout);
        contractsTabPanelLayout.setHorizontalGroup(
            contractsTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contractsTabPanelLayout.createSequentialGroup()
                .addComponent(holeSponsorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(holeSponsorButton, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
            .addGroup(contractsTabPanelLayout.createSequentialGroup()
                .addComponent(newContractButton, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteContractButton, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
            .addComponent(contractsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        contractsTabPanelLayout.setVerticalGroup(
            contractsTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contractsTabPanelLayout.createSequentialGroup()
                .addComponent(contractsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contractsTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(holeSponsorLabel)
                    .addComponent(holeSponsorButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contractsTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newContractButton)
                    .addComponent(deleteContractButton)))
        );

        adDetailTabbedPane.addTab(resourceMap.getString("contractsTabPanel.TabConstraints.tabTitle"), contractsTabPanel); // NOI18N

        deleteAdButton.setAction(actionMap.get("deleteAd")); // NOI18N
        deleteAdButton.setName("deleteAdButton"); // NOI18N

        newAdButton.setAction(actionMap.get("createAd")); // NOI18N
        newAdButton.setName("newAdButton"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(adsSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(adsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(deleteAdButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newAdButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(adDetailTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(adsSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(adsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deleteAdButton)
                            .addComponent(newAdButton)))
                    .addComponent(adDetailTabbedPane)))
        );

        adDetailTabbedPane.getAccessibleContext().setAccessibleName(resourceMap.getString("adDetailTabbedPane.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    @Action(enabledProperty = PROP_CLIENTSELECTED)
    public void createAd() {
        Clients client = getSelectedClient();
        categories.add(1);
        Ads ad = new Ads(1, client, "", 0, new Date());

        client.getAdses().add(ad);
        adsList.add(ad);
        setAdInformationChanged(true);

        int viewIndex = adsTable.convertRowIndexToView(adsList.size() - 1);
        adsTable.getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
    }

    @Action(enabledProperty = PROP_ADSELECTED)
    public void deleteAd() {
        int answer = JOptionPane.showConfirmDialog(OnCourseAdsDetailPanel.this,
                "Do you really want to delete this ad definition and contracts?",
                "Question", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            Ads ad = getSelectedAd();
            selectedClient.getAdses().remove(ad);
            adsList.remove(ad);
            forAllContractDeleted.stateChanged(new ChangeEvent(getSelectedClient()));
        }
    }

    @Action(enabledProperty = PROP_ADSELECTED)
    public void selectImage() {
        File file = fileChooser.selectAdFile(this);
        if (file != null) {
            selectedAd.setSource(file.getAbsolutePath());
            setSelectedAd(selectedAd);
            setAdInformationChanged(true);
            FileUtils.setReadableForAllUsers(file);
            imageFileLabel.setText("");
        }
    }

    @Action(enabledProperty = PROP_ADSELECTED)
    public void createContract() {
        Ads ad = getSelectedAd();
        Contracts contract = new Contracts(ad, new Date(), new Date(), (byte) 0);

        if (ad.getContractses() == null) {
            ad.setContractses(new HashSet());
        }

        ad.getContractses().add(contract);
        contractsList.add(contract);
        int viewIndex = contractsTable.convertRowIndexToView(contractsList.size() - 1);
        System.out.println("dsssssssssss" + viewIndex);
        contractsTable.getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
        fireContractChanged();
    }

    @Action(enabledProperty = PROP_CONTRACTSELECTED)
    public void deleteContract() {
        int answer = JOptionPane.showConfirmDialog(this,
                "Do you really want to delete this contract definition?",
                "Question", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            Contracts contract = getSelectedContract();
            getSelectedAd().getContractses().remove(contract);
            contractsList.remove(contract);
            initContractsTable1();
            fireContractChanged();
            /*
                Added by javed on 04-04-2019 for updating UI
                of active clients when all ads are deleted
             */
//            contractsTable.getModel().
            if (selectedAd.getContractses().size() == 0) {
                forAllContractDeleted.stateChanged(new ChangeEvent(getSelectedClient()));
            }
        }
    }

    @Action(enabledProperty = PROP_CONTRACTSELECTED)
    public void editHoleSponsor() {
        if (selectHolesTreeDialog == null) {
            selectHolesTreeDialog = new SelectHolesTreeDialog(Application.getInstance(BaseStation2App.class).getMainFrame(), true);
            //To deal with Look and Feel Changes
            SwingUtilities.updateComponentTreeUI(selectHolesTreeDialog);
            selectHolesTreeDialog.setName("selectHolesTreeDialog");
            selectHolesTreeDialog.pack();
        }

        selectHolesTreeDialog.setGolfClub(golfClub);
        selectHolesTreeDialog.setSelectedHoles(getSelectedContract().getHoleAd());
        Application.getInstance(BaseStation2App.class).show(selectHolesTreeDialog);

        String holes = selectHolesTreeDialog.getSelectedHolesString();

        if (holes != null) {
            getSelectedContract().setHoleAd(holes);
            setSponsoredHolesLabel(holes);
            setAdInformationChanged(true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane adDetailTabbedPane;
    private org.jdesktop.swingx.JXTitledSeparator adsSeparator;
    private javax.swing.JTable adsTable;
    private javax.swing.JScrollPane adsTableScrollPane;
    private javax.swing.JScrollPane contractsScrollPane;
    private org.jdesktop.swingx.JXPanel contractsTabPanel;
    private javax.swing.JTable contractsTable;
    private javax.swing.JButton deleteAdButton;
    private javax.swing.JButton deleteContractButton;
    private javax.swing.JButton holeSponsorButton;
    private javax.swing.JLabel holeSponsorLabel;
    private org.jdesktop.swingx.JXLabel imageFileLabel;
    private org.jdesktop.swingx.JXPanel imagePanel;
    private org.jdesktop.swingx.JXPanel imageTabPanel;
    private javax.swing.JButton newAdButton;
    private javax.swing.JButton newContractButton;
    private javax.swing.JButton selectImageButton;
    // End of variables declaration//GEN-END:variables

    private static class ContractDatePickerCellEditor extends DatePickerCellEditor implements TableCellRenderer {

        public ContractDatePickerCellEditor() {
            super(FormatUtils.dateFormat);
            datePicker.setFocusable(false);
            datePicker.getEditor().setEditable(false);
            setFontColor();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //   System.out.println(row+"========="+column);
            datePicker.setForeground(Color.black);
            datePicker.setBackground(UIManager.getColor("Button.background"));
            return datePicker;
        }

        private void setFontColor() {
            datePicker.getMonthView().setSelectionDate(new Date());
            datePicker.getMonthView().setDayForeground(Calendar.SUNDAY, Color.BLACK);
            datePicker.getMonthView().setDayForeground(Calendar.MONDAY, Color.BLACK);
            datePicker.getMonthView().setDayForeground(Calendar.TUESDAY, Color.BLACK);
            datePicker.getMonthView().setDayForeground(Calendar.WEDNESDAY, Color.BLACK);
            datePicker.getMonthView().setDayForeground(Calendar.THURSDAY, Color.BLACK);
            datePicker.getMonthView().setDayForeground(Calendar.FRIDAY, Color.BLACK);
            datePicker.getMonthView().setDayForeground(Calendar.SATURDAY, Color.BLACK);
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private class DateTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Date) {
                value = FormatUtils.dateFormat.format((Date) value);
            }
            //   System.out.println(row+"========="+column);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class AdComparator implements Comparator<Ads> {

        @Override
        public int compare(Ads a1, Ads a2) {
            if (a1.getAdId() == null && a2.getAdId() == null) {
                return 0;
            } else if (a2.getAdId() == null) {
                return -1;
            } else if (a1.getAdId() == null) {
                return 1;
            } else {
                return a1.getAdId() - a2.getAdId();
            }
        }
    }

    public enum AdFilter {
        SHOW_ALL(0, "All ads"),
        SHOW_ACTIVE(1, "Active ads"),
        SHOW_INACTIVE(2, "Inactive ads");

        public final int id;
        public final String title;

        private AdFilter(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public boolean include(Ad ad) {
            return (id == 0)
                    || (id == 1 && ad.hasActiveContracts())
                    || (id == 2 && !ad.hasActiveContracts());
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private static class AdsTableFormat implements WritableTableFormat<Ads> {

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int column) {
            int i = 0;
            if (column == i++) {
                return "Name";
            }
            if (column == i++) {
                return "Type";
            }
            if (column == i++) {
                return "Category";
            }
            if (column == i++) {
                return "Contract";
            }
            throw new IllegalArgumentException();
        }

        @Override
        public Object getColumnValue(Ads ad, int column) {
            int i = 0;
            if (column == i++) {
                return ad.getName();
            }
            if (column == i++) {
                return ad.getType();
            }
            if (column == i++) {
                return "NA";
            }
            if (column == i++) {
                return describeContracts(ad);
            }
            throw new IllegalArgumentException();
        }

        @Override
        public boolean isEditable(Ads baseObject, int column) {
            return column < 3;
        }

        @Override
        public Ads setColumnValue(Ads ad, Object editedValue, int column) {
            int i = 0;
            if (column == i++) {
                ad.setName((String) editedValue);
            }
            if (column == i++) {
                ad.setType((Integer) editedValue);
            }
            if (column == i++) {
                ad.setCategory(1);
            }
            return ad;
        }

        private static String describeContracts(Ads ad) {
            if (CollectionUtils.isEmpty(ad.getContractses())) {
                return "No contracts";
            } else {
                Date now = new Date(), firstEnd = null;
                for (Contracts contract : ad.getContractses()) {
                    if (contract.isActive() && (firstEnd == null || firstEnd.after(contract.getEndDate()))) {
                        firstEnd = contract.getEndDate();
                    }
                }

                if (firstEnd != null) {
                    int days = (int) Math.ceil((firstEnd.getTime() - now.getTime()) / 24d / 3600000d);
                    return days + " days left";
                }

                return ad.getContractses().size() + " inactive";
            }
        }
    }

    private static class ContractsTableFormat implements WritableTableFormat<Contracts> {

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            int i = 0;
            if (column == i++) {
                return "Start";
            }
            if (column == i++) {
                return "End";
            }
//            if (column == i++) {
//                return "Plan";
//            }
            throw new IllegalArgumentException();
        }

        @Override
        public Object getColumnValue(Contracts contract, int column) {
            int i = 0;
            if (column == i++) {
                return contract.getStartDate();
            }
            if (column == i++) {
                return contract.getEndDate();
            }
//            if (column == i++) {
//                return contract.getPlan();
//            }
            throw new IllegalArgumentException();
        }

        @Override
        public boolean isEditable(Contracts baseObject, int column) {
            return true;
        }

        @Override
        public Contracts setColumnValue(Contracts contract, Object editedValue, int column) {
            int i = 0;
            if (column == i++) {
                contract.setStartDate((Date) editedValue);
            }
            if (column == i++) {
                contract.setEndDate((Date) editedValue);
            }
//            if (column == i++) {
//                contract.setPlan(((Integer) editedValue).byteValue());
//            }
            return contract;
        }
    }

    private class UpdateAdsListener implements ListSelectionListener, ListEventListener<Ads> {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            Ads ad = null;

            if (isClientSelected() && adsTable.getSelectedRowCount() == 1) {
                int modelIndex = adsTable.convertRowIndexToModel(adsTable.getSelectedRow());
                if (adsList.size() > modelIndex) {
                    ad = adsList.get(modelIndex);
                }
            }

            setSelectedAd(ad);
        }

        @Override
        public void listChanged(ListEvent<Ads> listChanges) {
            if (isClientSelected()) {
                setAdInformationChanged(true);
            }
        }

    }

    private class UpdateContractsListener implements ListSelectionListener, ListEventListener<Contracts> {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            Contracts contract = null;

            if (isAdSelected() && contractsTable.getSelectedRowCount() == 1) {
                int modelIndex = contractsTable.convertRowIndexToModel(contractsTable.getSelectedRow());
                if (contractsList.size() > modelIndex) {
                    contract = contractsList.get(modelIndex);
                }
                // System.out.println("yashtest--"+modelIndex);
            }

            setSelectedContract(contract);
        }

        @Override
        public void listChanged(ListEvent<Contracts> listChanges) {
            if (isAdSelected()) {
                setAdInformationChanged(true);
                fireContractChanged();
            }
        }
    }

    /*  
        Added by javed on 04-04-2019 
        for updated the UI when ads deleted
     */
    public void setForAllContractDeleted(ChangeListener forAllContractDeleted) {
        this.forAllContractDeleted = forAllContractDeleted;
    }

}

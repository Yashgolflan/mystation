/*
 * 
 */
package com.stayprime.basestation.ui;

import com.alee.laf.filechooser.FileChooserAdapter;
import com.alee.laf.filechooser.FileChooserListener;
import com.stayprime.util.file.ImageFileFilter;
import com.alee.laf.filechooser.FileChooserViewType;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.utils.ImageUtils;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.geo.BasicMapImage;
import com.stayprime.geo.Coordinates;
import com.stayprime.geo.MapUtils;
import com.stayprime.legacy.Xml;
import com.stayprime.util.file.FileUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.w3c.dom.Element;

/**
 *
 * @author benjamin
 */
public class FileChooser {
    private final CustomFileChooser fileChooser;
    private final ImageFileFilter flyoverFileFilter;
    private final ImageFileFilter imageFileFilter;
    private final ImageFileFilter mapImageFileFilter;    
    private final FileNameExtensionFilter pdfFileFilter;
    private final ImageFileFilter adsFileFilter;

    public FileChooser() {
        fileChooser = new CustomFileChooser();
        fileChooser.setCurrentDirectory(BaseStation2App.getApplication().getBasePath());
        flyoverFileFilter = new ImageFileFilter(new String[] {"mpg", "mpeg", "wmv", "avi", "mp4"}, false);
        imageFileFilter = new ImageFileFilter(new String[] {"jpg", "jpeg", "png"}, false);        
        mapImageFileFilter = new ImageFileFilter(new String[] {"jpg", "jpeg", "png"}, true);
        pdfFileFilter = new FileNameExtensionFilter("PDF document", "PDF", "pdf");
        adsFileFilter = new ImageFileFilter(new String[] {"jpg", "jpeg", "png", "mpg", "mpeg", "wmv", "avi", "mp4"}, false);
        
        fileChooser.getFileChooserPanel().setViewType(FileChooserViewType.table);
    }

    private File select(Component parent, FileFilter fileFilter) {
        fileChooser.setFileFilter(fileFilter);
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileUtils.setReadableForAllUsers(file);
            return file;
        }
        else {
            return null;
        }
    }

    public File selectPdf(Component parent) {
        fileChooser.setShowPreview(false);
        return select(parent, pdfFileFilter);
    }

    public File selectMap(Component parent) {
        fileChooser.setShowPreview(true);
        return select(parent, mapImageFileFilter);
    }

    public File selectFlyby(Component parent) {
        fileChooser.setShowPreview(true);
        return select(parent, flyoverFileFilter);
    }

    public File selectImage(Component parent) {
        fileChooser.setShowPreview(true);
        return select(parent, imageFileFilter);
    }
    
    public File selectAdFile(Component parent) {
        fileChooser.setShowPreview(true);
        return select(parent, adsFileFilter);
    }

    public BasicMapImage loadMap(File file) {
        try {
            int dot = file.getName().lastIndexOf('.');
            File xml = new File(file.getParent(), file.getName().substring(0, dot) + ".xml");
            if(!xml.exists()) {
                xml = new File(file.getParent(), file.getName().substring(0, dot) + ".XML");
            }
            if(!xml.exists()) {
                return null;
            }

            Element rootElement = Xml.rootElement(xml);
            Xml corners = new Xml(rootElement);

            if(corners.name().equals("root")) {
                corners = corners.child("MapImage");
            }

            Coordinates topLeft, topRight, bottomLeft, bottomRight;
            topLeft = new Coordinates(corners.child("TopLeft").string("location"));
            topRight = new Coordinates(corners.child("TopRight").string("location"));
            bottomLeft = new Coordinates(corners.child("BottomLeft").string("location"));
            bottomRight = new Coordinates(corners.child("BottomRight").string("location"));

            BasicMapImage map = new BasicMapImage(file.getAbsolutePath(),
                    topLeft, topRight, bottomLeft, bottomRight);
            MapUtils.normalizeMapImage(map);
            return map;
        }
        catch(Exception ex) {
            //notifyError(new RuntimeException("Error loading course image definition", ex));
            return null;
        }
    }

    private static class CustomFileChooser extends WebFileChooser {
        private boolean showPreview;
        private boolean showing;
        private JLabel preview;
        private FileChooserListener listener;

        public CustomFileChooser() {
            preview = new JLabel();
            preview.setHorizontalAlignment(SwingConstants.CENTER);
            preview.setPreferredSize(new Dimension (150, 150));
        }

        private void setPreview(ImageIcon imageIcon) {
            preview.setIcon(imageIcon);
            if (imageIcon != null && !showing) {
                add(preview, BorderLayout.EAST);
            }
            showing = imageIcon != null;
            if (!showing) {
                remove(preview);
            }
        }

        public void setShowPreview(boolean show) {
            this.showPreview = show;
            setPreview(null);
            createListener();
        }

        private void createListener() {
            if (listener == null) {
                listener = new FileChooserAdapter() {
                    @Override
                    public void selectionChanged(List<File> selectedFiles) {
                        if (showPreview && selectedFiles.size() > 0) {
                            File selection = selectedFiles.get(0);
                            if (selection == null) {
                                setPreview(null);
                            }
                            else {
                                try {
                                    BufferedImage img = ImageIO.read(selection);
                                    BufferedImage pre = ImageUtils.createPreviewImage(img, preview.getSize());
                                    setPreview(new ImageIcon(pre));
                                }
                                catch (Exception ex) {
                                    URL iconURL = getClass().getResource("/com/stayprime/basestation2/resources/document-error.png");
                                    setPreview(new ImageIcon(iconURL));
                                }
                            }
                        }
                        else {
                            setPreview(null);
                        }
                    }
                };
                getFileChooserPanel().addFileChooserListener(listener);
            }
        }

    }

}

package com.stayprime.basestation2.util;


import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class ImageRetriever extends SwingWorker<Icon, Void> {
    private ImageRetriever() {}
    private String strImageUrl;
    private JLabel lblImage;
    
    public ImageRetriever(JLabel lblImage, String strImageUrl) {
        this.strImageUrl = strImageUrl;
        this.lblImage = lblImage;
    }
    
    @Override
    protected Icon doInBackground() throws Exception {
        Icon icon = retrieveImage(strImageUrl);
        return icon;
    }
    
    private Icon retrieveImage(String strImageUrl) 
            throws MalformedURLException, IOException {       
        InputStream is = null;
        URL imgUrl = null;
        Icon icon = null;
        Image image = null;

        try {
            imgUrl = new URL(strImageUrl);
            is = imgUrl.openStream();
            ImageInputStream iis = ImageIO.createImageInputStream(is);
            Iterator<ImageReader> it = ImageIO.getImageReadersBySuffix("png");
            ImageReader reader = it.next();
            reader.setInput(iis);
            image = reader.read(0);
            icon = new ImageIcon(image);
        } catch (FileNotFoundException e) {
            System.out.println("Exception: File not found:" + e.getMessage());
        }
        return icon;
    }
    
    @Override
    protected void done() {
        Icon icon = null;
        String text = null;
        try {
            icon = get();
        } catch (InterruptedException ignore) {
            ignore.printStackTrace();
            text = "Image unavailable";
        } catch (ExecutionException ignore) {
            ignore.printStackTrace();
            text = "Image unavailable";
        }
        lblImage.setIcon(icon);
        lblImage.setText(text);
    }       
}
 
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.util.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
/**
 *
 * @author benjamin
 */
public class ImageRetrieverTask extends Task<Object, Void> {
    private static final Logger log = LoggerFactory.getLogger(ImageRetrieverTask.class);

    private boolean doneLoading = false;
    private ImageReader imageReader = null;
    private BufferedImage image;

    private final File imageFile;
    private final ImageRetrievedCallback callback;
    private final IIOReadProgressListener progressListener;

    public ImageRetrieverTask(Application app, File file, ImageRetrievedCallback callback) {
        this(app, file, callback, false);
    }

    public ImageRetrieverTask(Application app, File file, ImageRetrievedCallback callback, boolean trackProgress) {
        super(app);
        this.imageFile = file;
        this.callback = callback;

        if(trackProgress)
            progressListener = new IIOReadProgressListenerImpl();
        else
            progressListener = null;
    }

    @Override
    protected BufferedImage doInBackground() throws Exception {
//        log.info("Loading in background: " + imageFile);
        InputStream is = new FileInputStream(imageFile);
        ImageInputStream iis = ImageIO.createImageInputStream(is);
        String suffix = imageFile.getName().lastIndexOf(".") == -1? "":
            imageFile.getName().substring(imageFile.getName().lastIndexOf(".") + 1);
        Iterator<ImageReader> it =
            ImageIO.getImageReadersBySuffix(suffix);

        imageReader = it.next();
        imageReader.setInput(iis);
        if(progressListener != null)
            imageReader.addIIOReadProgressListener(new IIOReadProgressListenerImpl());
        new Thread(retrieveImage, "RetrieveImage").start();

        while(true) {
            synchronized(this) {
                if(doneLoading)
                    break;
            }

            if(isCancelled()) {
                imageReader.abort();
//                log.info("Loading cancelled: " + imageFile);
                break;
            }

            try {
                Thread.sleep(20);
            } catch(InterruptedException ex) {
                log.warn("Exception during sleep in doInBackground(): " + ex.toString());
                log.trace("Exception during sleep in doInBackground(): ", ex);
            }
        }

        try {
            iis.close();
            is.close();
        }
        catch(IOException ex) {
            log.error("Error while trying to close ImageInputStream: " + iis);
        }

        return null;
    }

    @Override
    protected void succeeded(Object result) {
        try {
            if(isCancelled())
                callback.retrieveImageCanceled();
            else if(result instanceof Exception)
                callback.retrieveImageFailed((Exception) result);
            else
                callback.imageRetrieved(image);
        } catch (Exception ex) {
	    ex.printStackTrace();
//            log.warn("Exception in ImageRetrieverTask.done(): " + ex.toString());
//            log.trace("Exception in ImageRetrieverTask.done(): ", ex);
        }
    }

    /* The methods below are what's required by the Java imaging
     * API to enable tracking the progress of an ImageIO read()
     * and optionally aborting it.  If we weren't interested in
     * tracking image-loading progress or supporting Task.cancel()
     * it would be enough to just use ImageIO.read().
     */
    Runnable retrieveImage = new Runnable() {
        public void run()  {
            try {
                image = imageReader.read(0);
            }
	    catch (IOException ex) {
                log.error("Exception reading image: " + ex.toString());
                log.debug("Exception reading image: ", ex);
                image = null;
            }
            finally {
                synchronized(ImageRetrieverTask.this) {
                    doneLoading = true;
                }
            }
        }
    };

    public static interface ImageRetrievedCallback {
        public void imageRetrieved(BufferedImage image);
        public void retrieveImageFailed(Exception cause);
        public void retrieveImageCanceled();
    }

    private class IIOReadProgressListenerImpl implements IIOReadProgressListener {

        public void sequenceStarted(ImageReader source, int minIndex) {
        }

        public void sequenceComplete(ImageReader source) {
        }

        public void imageStarted(ImageReader source, int imageIndex) {
        }

        public void imageProgress(ImageReader source, float percentageDone) {
            setProgress(Math.round(percentageDone));
        }

        public void imageComplete(ImageReader source) {
        }

        public void thumbnailStarted(ImageReader source, int imageIndex, int thumbnailIndex) {
        }

        public void thumbnailProgress(ImageReader source, float percentageDone) {
        }

        public void thumbnailComplete(ImageReader source) {
        }

        public void readAborted(ImageReader source) {
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.util.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class SwingWorkerImageRetriever extends SwingWorker<Object, Void> {
    private static final Logger log = LoggerFactory.getLogger(SwingWorkerImageRetriever.class);

    private volatile boolean doneLoading = false;
    private ImageReader imageReader = null;
    private BufferedImage image;
    private RetrieveImageThread retrieveImage;
    private Object failureCause;

    private final File imageFile;
    private final ImageRetrievedCallback callback;
    private final IIOReadProgressListener progressListener;

    public SwingWorkerImageRetriever(File file, ImageRetrievedCallback callback) {
        this(file, callback, false);
    }

    public SwingWorkerImageRetriever(File file, ImageRetrievedCallback callback, boolean trackProgress) {
        this.imageFile = file;
        this.callback = callback;
//	fileName = file.getCode();

        if(trackProgress)
            progressListener = new IIOReadProgressListenerImpl();
        else
            progressListener = null;
    }

    @Override
    protected Object doInBackground() throws Exception {

        try {
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

	    retrieveImage = new RetrieveImageThread("ImageLoader");
	    retrieveImage.start();

            while(true) {
		if(doneLoading)
		    break;

                if(isCancelled()) {
                    imageReader.abort();
                    break;
                }

                try {
                    Thread.sleep(20);
                } catch(InterruptedException ex) {
//                    log.warn("Exception during sleep in doInBackground(): " + ex);
//                    log.trace("Exception during sleep in doInBackground(): ", ex);
                }
            }

            try {
                iis.close();
                is.close();
            }
            catch(Throwable ex) {
//                log.error("Error while trying to close ImageInputStream: " + ex);
            }

            if(isCancelled()) {
                image = null;
		failureCause = "Cancelled";
	    }
        }
        catch(Throwable th) {
	    failureCause = th;
        }

        return null;
    }

    /* The methods below are what's required by the Java imaging
     * API to enable tracking the progress of an ImageIO read()
     * and optionally aborting it.  If we weren't interested in
     * tracking image-loading progress or supporting Task.cancel()
     * it would be enough to just use ImageIO.read().
     */

    @Override
    protected void done() {
        try {
            if(image != null)
                callback.imageRetrieved(this, image);
	    else
		callback.failed(this, failureCause);
        } catch (Throwable ex) {
            log.error("Exception in ImageRetriever.done(): " + ex.toString());
            log.debug("Exception in ImageRetriever.done(): ", ex);
        }
    }

    public static interface ImageRetrievedCallback {
        public void imageRetrieved(SwingWorkerImageRetriever retriever, BufferedImage image);
	public void failed(SwingWorkerImageRetriever retriever, Object cause);
    }

    private class IIOReadProgressListenerImpl implements IIOReadProgressListener {

        public void sequenceStarted(ImageReader source, int minIndex) {
        }

        public void sequenceComplete(ImageReader source) {
        }

        public void imageStarted(ImageReader source, int imageIndex) {
//	    System.out.println(fileName + "started");
        }

        public void imageProgress(ImageReader source, float percentageDone) {
            setProgress(Math.round(percentageDone));
//	    System.out.println(fileName + percentageDone);
        }

        public void imageComplete(ImageReader source) {
//	    System.out.println(fileName + "completed");
        }

        public void thumbnailStarted(ImageReader source, int imageIndex, int thumbnailIndex) {
        }

        public void thumbnailProgress(ImageReader source, float percentageDone) {
        }

        public void thumbnailComplete(ImageReader source) {
        }

        public void readAborted(ImageReader source) {
//	    System.out.println(fileName + "aborted");
        }
    }

    private class RetrieveImageThread extends Thread {

	public RetrieveImageThread(String name) {
	    super(name);
	}

	public void run() {
	    try {
		image = imageReader.read(0);
	    }
	    catch (Throwable th) {
		failureCause = th;
	    }
	    finally {
		doneLoading = true;
	    }
	}
    }
}

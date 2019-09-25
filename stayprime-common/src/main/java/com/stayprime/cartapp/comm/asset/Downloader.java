/*
 *
 */
package com.stayprime.cartapp.comm.asset;

import com.stayprime.localservice.CartUnitCommunication;
import com.stayprime.util.file.FileLocator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 * @param <D>
 */
public abstract class Downloader<D> {

    protected CartUnitCommunication comm;
    protected final FileLocator fileLocator;
    protected final DownloadingFileLocator fileDownloader;

    protected int progress;
    protected boolean downloadInProgress;

    protected boolean assetsDownloaded = false;
    protected long totalBytesDownloaded = 0;

    protected final List<Observer<D>> observers;

    public Downloader(CartUnitCommunication comm, FileLocator fileLocator, DownloadingFileLocator fileDownloader) {
        this.comm = comm;
        this.fileLocator = fileLocator;
        this.fileDownloader = fileDownloader;

        observers = new ArrayList<Observer<D>>();
    }

    public boolean isDownloadInProgress() {
        return downloadInProgress;
    }

    protected void setDownloadInProgress(boolean inProgress) {
        this.downloadInProgress = inProgress;
    }

    public boolean isAssetsDownloaded() {
        return assetsDownloaded;
    }

    public void setProgress(int progress) {
        if (this.progress != progress) {
            this.progress = progress;
            notifyProgress();
        }
    }

    public abstract void checkAndLoadFromStorage();

    /*
     * Observer Support
     */
    public void addDownloaderObserver(Observer<D> observer) {
        observers.add(observer);
    }

    public void removeDownloaderObserver(Observer<D> observer) {
        observers.remove(observer);
    }

    protected void notifyDownloading() {
        for (Observer o : observers) {
            try {
                o.updatingFromServer(null, 0);
            }
            catch (Exception ex) {
            }
        }
    }

    private void notifyProgress() {
        for (Observer o : observers) {
            try {
                o.progressChanged(null, progress);
            }
            catch (Exception ex) {
            }
        }
    }

    protected abstract void notifyFinished();

    public interface Observer<D> {

        public void updatingFromServer(D def, int cause);

        public void progressChanged(D def, int progress);

        public void definitionLoaded(D def, int result);
    }
}

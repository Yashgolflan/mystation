/*
 *
 */
package com.stayprime.cartapp.comm.asset;

import com.stayprime.localservice.CartUnitCommunication;
import com.stayprime.util.file.FileLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 * @param <D>
 */
public class AssetDownloader<D> extends Downloader<D> implements AssetInfoLoader.ProgressCallback {

    protected static final Logger log = LoggerFactory.getLogger(AssetDownloader.class);

    private D assetInfo;
    private final AssetInfoLoader<D> assetInfoLoader;
    private Exception failureCause;

    public AssetDownloader(CartUnitCommunication comm, FileLocator fileLocator,
            DownloadingFileLocator fileDownloader, AssetInfoLoader<D> assetInfoLoader) {
        super(comm, fileLocator, fileDownloader);
        this.assetInfoLoader = assetInfoLoader;
    }

    public D getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(D assetInfo) {
        this.assetInfo = assetInfo;
    }

    public Exception getFailureCause() {
        return failureCause;
    }

    @Override
    public void checkAndLoadFromStorage() {
        log.debug("checkAndLoadFromStorage: " + assetInfoLoader.toString());
        try {
            notifyDownloading();
            D newAssetInfo = assetInfoLoader.load(fileLocator, this);
            setAssetInfo(newAssetInfo);
        }
        catch (Exception ex) {
            log.info("Loading from local storage failed: " + ex);
            log.trace("Loading from local storage failed: ", ex);
            this.failureCause = ex;
        }
        finally {
            notifyFinished();
        }
    }

    public void checkServerVersion() throws Exception {
        try {
            if (assetInfoLoader.shouldUpdateFromServer(comm, assetInfo)) {
                setProgress(0);
                setDownloadInProgress(true);
                notifyDownloading();

                fileDownloader.reset();
                D newAssetInfo = assetInfoLoader.load(fileDownloader, this);
                fileDownloader.commitDownloadedFiles();
                setAssetInfo(newAssetInfo);
                this.failureCause = null;
            }
        }
        catch (Exception ex) {
            log.warn("Checking server version failed: " + ex);
            log.trace("Checking server version failed: ", ex);
            this.failureCause = ex;
        }
        finally {
            setDownloadInProgress(false);
            notifyFinished();
        }
    }

    @Override
    public void notifyFinished() {
        for (Observer<D> o : observers) {
            try {
                o.definitionLoaded(assetInfo, 0);
            }
            catch (Exception ex) {
                log.error("Exception while notifying listeners: " + ex);
                log.trace("Exception while notifying listeners: ", ex);
            }
        }
    }
}

/*
 * 
 */
package com.stayprime.cartapp.comm.asset;

import com.stayprime.localservice.CartUnitCommunication;
import com.stayprime.localservice.Constants;
import com.stayprime.oncourseads.Ad;
import com.stayprime.oncourseads.AdList;
import com.stayprime.oncourseads.OnCourseAdsLoader;
import com.stayprime.util.file.FileLocator;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class AdsLoader implements AssetInfoLoader<AdList> {

    private static final Logger log = LoggerFactory.getLogger(AdsLoader.class);

    private OnCourseAdsLoader loader;

    public AdsLoader(OnCourseAdsLoader loader) {
        this.loader = loader;
    }

    @Override
    public boolean shouldUpdateFromServer(CartUnitCommunication comm, AdList adList) {
        String remoteVersion = comm.getAssetVersion(Constants.adsUpdated);

        if (remoteVersion == null) {
            log.debug("Server returned null: don't update");
            return false;
        }

        if (adList == null) {
            log.debug("AdList is null: update from server");
            return true;
        }

        String localVersion = adList.getVersion();
        if (ObjectUtils.notEqual(localVersion, remoteVersion)) {
            log.debug("AdList out of date: update form server");
            return true;
        }

        log.trace("AdList is up to date.");
        return false;
    }

    //For testing
    public AdList load(FileLocator fileLocator, ProgressCallback callback) {
        loader.setFileLocator(fileLocator);
        AdList ads = loader.loadAds(true);

        if (ads == null) {
            return null;
        }
        else {
            int currentAd = 0;
            int totalAds = ads.getList().size();
            for (Ad ad : ads.getList()) {
                currentAd++;
                if (StringUtils.isNotBlank(ad.source)) {
                    fileLocator.getFile(ad.source);
                }

                callback.setProgress(10 + Math.round(80f * currentAd / totalAds));
            }
            return ads;
        }
    }

}

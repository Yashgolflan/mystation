/*
 * 
 */
package com.stayprime.cartapp.comm.asset;

import com.stayprime.localservice.CartUnitCommunication;
import com.stayprime.util.file.FileLocator;

/**
 *
 * @author benjamin
 * @param <T> Type of the loaded asset
 */
public interface AssetInfoLoader<T> {

    public boolean shouldUpdateFromServer(CartUnitCommunication comm, T assetInfo);

    public T load(FileLocator fileLocator, ProgressCallback callback);

    public interface ProgressCallback {
        public void setProgress(int progress);
    }
}

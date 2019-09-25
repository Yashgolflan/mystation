/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.oncourseads;

import com.stayprime.util.file.FileLocator;
import java.util.List;

/**
 *
 * @author benjamin
 */
public interface OnCourseAdsLoader {

    public abstract List<Client> loadClients(Boolean active, boolean loadAds);
    public abstract Client loadClient(Integer id, boolean loadAds);
    public AdList loadAds(Boolean active);
    public Ad loadAd(Integer adId);

    public List<Category> loadCategories();

    public void setFileLocator(FileLocator fileLocator);
}

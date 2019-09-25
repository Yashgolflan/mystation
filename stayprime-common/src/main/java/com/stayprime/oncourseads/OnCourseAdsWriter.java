/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.oncourseads;

import java.util.List;

/**
 *
 * @author benjamin
 */
public interface OnCourseAdsWriter {
    public void writeClients(List<Client> clients, boolean writeAds);
    public void writeAds(AdList Ads);
    public void createClient(Client client);
    public void updateClient(Client client, boolean updateAds);
    public void deleteClient(Client client);
    public void createAd(Ad ad);
    public void updateAd(Ad ad);
    public void deleteAd(Ad ad);
}

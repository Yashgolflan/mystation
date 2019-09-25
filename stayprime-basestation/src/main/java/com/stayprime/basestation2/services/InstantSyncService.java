/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.services;

import com.stayprime.localservice.Constants;
import com.stayprime.storage.services.AssetSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author sarthak
 */
@Component
public class InstantSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(InstantSyncService.class);
    
    @Autowired
    private AssetSyncService assetSyncService;

    public void syncFnb() {
        log.info("Sync fnb");
        assetSyncService.syncInstantly(Constants.menuUpdated);
    }
    
    public void syncCourse() {
        log.info("Sync course");
        assetSyncService.syncInstantly(Constants.courseUpdated);
    }
    
    public void syncPins() {
        log.info("Sync pins");
        assetSyncService.syncInstantly(Constants.pinsUpdated);
    }
    
}

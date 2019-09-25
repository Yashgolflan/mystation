/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.services;

import com.stayprime.storage.services.AssetSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author benjamin
 */
@Component
public class Services {
    
    @Autowired
    CartService cartService;

    @Autowired
    CourseService courseService;

    @Autowired
    AssetSyncService assetSyncService;

    public CartService getCartService() {
        return cartService;
    }

    public CourseService getCourseService() {
        return courseService;
    }

    public AssetSyncService getAssetSyncService() {
        return assetSyncService;
    }

}

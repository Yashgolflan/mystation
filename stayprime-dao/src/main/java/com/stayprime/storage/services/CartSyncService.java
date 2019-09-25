/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.storage.services;

import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.storage.domain.ReportedUnit;
import com.stayprime.hibernate.entities.CartUnit;
import com.stayprime.storage.repos.CartInfoRepo;
import com.stayprime.storage.repos.CartUnitRepo;
import com.stayprime.storage.util.DomainUtil;
import com.stayprime.storage.util.LocalStorage;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author benjamin
 */
@Component
public class CartSyncService {
    private static final Logger log = LoggerFactory.getLogger(CartSyncService.class);

    @Autowired
    LocalStorage localStorage;

    @Autowired
    CartInfoRepo cartInfoRepo;

    @Autowired
    CartUnitRepo cartUnitRepo;

    public void syncCarts() {
        localStorage.saveCarts(DomainUtil.toList(cartInfoRepo.findAll()));
    }

    public void syncUnits() {
        localStorage.saveCartUnits(DomainUtil.toList(cartUnitRepo.findAll()));
    }

    public void syncReportedUnits() {
        saveReportedUnits(localStorage.listReportedUnits());
    }

    public void saveReportedUnits(List<ReportedUnit> list) {
        log.debug("Save reported units to cloud, count: {}", list == null ? 0 : list.size());
        if (CollectionUtils.isNotEmpty(list)) {
            List<CartInfo> carts = DomainUtil.toList(cartInfoRepo.findAll());
            List<CartUnit> units = DomainUtil.toList(cartUnitRepo.findAll());
            List<CartInfo> saveCarts = new ArrayList<>(list.size());
            List<CartUnit> saveUnits = new ArrayList<>(list.size());

            boolean success = true;
            for (ReportedUnit ru : list) {
                log.info("Saving ReportedUnit to DB: {}", ru);
                CartUnit unit = getUnit(units, ru.getMac());
                clearConflictingUnits(ru, units, saveUnits);
                ru.updateCartUnit(unit);
                saveUnits.add(unit);

                Integer cartNo = ru.getUnitId();
                if (cartNo != null) {
                    CartInfo cart = getCart(carts, cartNo);
                    clearConflictingCarts(ru, carts, saveCarts);
                    cart.setCartUnit(unit);
                    saveCarts.add(cart);
                }

                localStorage.removeReportedUnit(ru);
            }
            cartUnitRepo.save(saveUnits);
            cartInfoRepo.save(saveCarts);
            log.info(success? "CartSync successful." : "Errors while syncing.");
        }
        else {
            log.debug("No reported units.");
        }
    }

    private CartUnit getUnit(List<CartUnit> units, String mac) {
        CartUnit unit = CartUnit.find(units, mac);
        if (unit == null) {
            unit = new CartUnit();
            unit.setMacAddress(mac);
        }
        return unit;
    }

    private void clearConflictingUnits(ReportedUnit ru, List<CartUnit> units, List<CartUnit> saveList) {
        for (CartUnit unit : units) {
            if (ObjectUtils.notEqual(ru.getMac(), unit.getMacAddress())) {
                boolean sameIp = ObjectUtils.equals(ru.getIp(), unit.getIpAddress());
                boolean sameNumber = ObjectUtils.equals(ru.getUnitId(), unit.getCartNumber());
                boolean save = sameIp || sameNumber;
                if (sameIp) unit.setIpAddress(null);
                if (sameNumber) unit.setCartNumber(null);
                if (save) saveList.add(unit);
            }
        }
    }

    private CartInfo getCart(List<CartInfo> carts, int cartNo) {
        CartInfo cart = CartInfo.find(carts, cartNo);
        if (cart == null) {
            cart = new CartInfo();
            cart.setCartNumber(cartNo);
        }
        return cart;
    }

    private void clearConflictingCarts(ReportedUnit ru, List<CartInfo> carts, List<CartInfo> saveCarts) {
        for (CartInfo cart : carts) {
            if (ObjectUtils.notEqual(ru.getUnitId(), cart.getCartNumber())) {
                CartUnit unit = cart.getCartUnit();
                String unitMac = unit == null? null : unit.getMacAddress();
                boolean sameUnit = ObjectUtils.equals(ru.getMac(), unitMac);
                if (sameUnit) {
                    cart.setCartUnit(null);
                    saveCarts.add(cart);
                }
            }
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.services;

import com.aeben.golfclub.RequestType;
import com.stayprime.hibernate.entities.CartAssignment;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.hibernate.entities.CartTracking;
import com.stayprime.hibernate.entities.CartUnit;
import com.stayprime.hibernate.entities.ServiceRequest;
import com.stayprime.storage.repos.CartAssignmentRepo;
import com.stayprime.storage.repos.CartInfoRepo;
import com.stayprime.storage.repos.CartTrackingRepo;
import com.stayprime.storage.repos.CartUnitRepo;
import com.stayprime.storage.repos.ServiceRequestRepo;
import com.stayprime.storage.util.DomainUtil;
import com.stayprime.storage.util.LocalStorage;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author benjamin
 */
@Service
public class CartService {

    @Autowired
    LocalStorage localStorage;

    @Autowired
    ServiceRequestRepo serviceRequestRepo;

    @Autowired
    CartAssignmentRepo cartAssignmentRepo;

    @Autowired
    CartInfoRepo cartInfoRepo;

    @Autowired
    CartUnitRepo cartUnitRepo;

    @Autowired
    CartTrackingRepo cartTrackingRepo;

    public void syncCarts() {
        List<CartInfo> carts = DomainUtil.toList(cartInfoRepo.findAll());
        localStorage.saveCarts(carts);
    }

    public List<CartInfo> listCartsAndUnits() {
        return localStorage.listCarts();
    }

    public CartInfo getCart(String macAddress) {
        return localStorage.getCartInfo(macAddress);
    }

    public void saveCart(CartInfo cart) {
        cartInfoRepo.save(cart);
    }

    public void deleteCart(CartInfo cartInfo) {
        cartInfoRepo.delete(cartInfo);
    }

    public List<CartUnit> listUnits() {
        return localStorage.listCartUnits();
    }

    public void saveUnit(CartUnit cartUnit) {
        cartUnitRepo.save(cartUnit);
    }

    public void deleteUnit(CartUnit cartInfo) {
        cartUnitRepo.delete(cartInfo);
    }

    public List<CartAssignment> listCartAssignments() {
        return DomainUtil.toList(cartAssignmentRepo.findAll());
    }

    public void saveCartAssignments(List<CartAssignment> list) {
        cartAssignmentRepo.save(list);
    }

    public List<ServiceRequest> listServiceRequests(int maxStatus) {
        return serviceRequestRepo.findByStatusLessThanEqual(maxStatus);
    }

    public void replyRequest(ServiceRequest request, boolean confirm, String message) {
        //sendTextMessage
        //save to DB
        int status = confirm? RequestType.REQUEST_SERVICED : RequestType.REQUEST_DENIED;
        request.setStatus(status);
        request.setRepliedBy(getCurrentUser());
        request.setReplyTime(new Date());
        serviceRequestRepo.save(request);
    }

    public void dismissRequest(ServiceRequest request) {
        request.setRepliedBy(getCurrentUser());
        request.setStatus(RequestType.REQUEST_DISMISSED);
        request.setReplyTime(new Date());
        serviceRequestRepo.save(request);
    }

    private String getCurrentUser() {
        return "Base Station";
    }

    public List<CartTracking> listCartTrack(Integer cart, Date start, Date end, int limit) {
        Pageable p = new PageRequest(0, limit);
        return cartTrackingRepo.findById_CartNumberAndId_TimestampBetween(cart, start, end, p);
    }

    public List<CartTracking> listAllCartsTrack(Date start, Date end, int limit) {
        Pageable p = new PageRequest(0, limit);
        return cartTrackingRepo.findById_TimestampBetween(start, end, p);
    }

}

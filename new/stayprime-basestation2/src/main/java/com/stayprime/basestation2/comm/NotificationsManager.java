/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.comm;

import com.aeben.golfclub.RequestType;
import com.stayprime.basestation2.services.CartService;
import com.stayprime.basestation2.services.CourseService;
import com.stayprime.basestation2.util.NotificationPopup;
import com.stayprime.hibernate.entities.FnbOrder;
import com.stayprime.hibernate.entities.ServiceRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author benjamin
 */
@Component
public class NotificationsManager {
    private static final Logger log = LoggerFactory.getLogger(NotificationsManager.class);

    private final Map<Integer, ServiceRequest> serviceRequestsById;

    private final List<NotificationsListener> listeners;

    @Autowired
    private CartService cartService;
    
    @Autowired
    private CourseService courseService;

    public NotificationsManager() {
        serviceRequestsById = new HashMap<Integer, ServiceRequest>();
        listeners = new ArrayList<NotificationsListener>();
    }

    public void addNotificationListener(NotificationsListener listener) {
        listeners.add(listener);
    }

    public void removeNotificationListener(NotificationsListener listener) {
        listeners.remove(listener);
    }

    public void serviceRequestAdded(ServiceRequest request) {
        serviceRequestsById.put(request.getId(), request);

        for(NotificationsListener listener: listeners)
            listener.serviceRequestReceived(request);
    }

    public void serviceRequestUpdated(ServiceRequest request) {
        serviceRequestsById.put(request.getId(), request);

        for(NotificationsListener listener: listeners)
            listener.serviceRequestUpdated(request);
    }

    public void serviceRequestDismissed(ServiceRequest request) {
        serviceRequestsById.put(request.getId(), request);

        for(NotificationsListener listener: listeners)
            listener.serviceRequestDismissed(request);
    }

    public void replyServiceRequest(ServiceRequest request, boolean confirm) {
        try {
            int oldStatus = request.getStatus();
            String message = confirm? "Your request has been confirmed."
                    : "Your request cannot be attended to at the moment.";
            cartService.replyRequest(request, confirm, message);

            if(oldStatus != request.getStatus()) {
                for (NotificationsListener listener: listeners) {
                    listener.serviceRequestUpdated(request);
                }
            }
        }
        catch (Exception ex) {
            log.error("replyServiceRequest: " + ex);
            log.debug("replyServiceRequest: " + ex, ex);
//            throw new RuntimeException("Error sending reply " + ex, ex);
            NotificationPopup.showErrorDialog("Error sending reply");
        }
    }

    public void dismissServiceRequest(ServiceRequest request) {
        int oldStatus = request.getStatus();
        cartService.dismissRequest(request);

        if(oldStatus != request.getStatus()) {
            for(NotificationsListener listener: listeners)
                listener.serviceRequestDismissed(request);
        }
    }

    public void dismissRepliedRequests() {
        for (ServiceRequest request: serviceRequestsById.values()) {
            if(request.getStatus() == RequestType.REQUEST_SERVICED
                    || request.getStatus() == RequestType.REQUEST_DENIED) {
                int oldStatus = request.getStatus();
                cartService.dismissRequest(request);

                if(oldStatus != request.getStatus()) {
                    for(NotificationsListener listener: listeners)
                        listener.serviceRequestDismissed(request);
                }
            }
        }
    }

    public void updateServiceRequests() {
        List<ServiceRequest> requests = cartService.listServiceRequests(RequestType.REQUEST_SERVICED);
        List<FnbOrder> fnbOrders = new ArrayList<>();
        for (ServiceRequest request : requests) {
            if (request.getType() == RequestType.FNB.getType()) {
                fnbOrders = courseService.getFnbOrders();
                break;
            }
        }
        
        for (ServiceRequest request: requests) {
            if (request.getType() == RequestType.FNB.getType()) {
                String additionalInfo = "";
                for (FnbOrder fnb : fnbOrders) {
                    if (fnb.getCartNumber() == request.getCartNumber() && 
                            fnb.getSendStatus().getCreated().equals(request.getTime())) {
                        additionalInfo = fnb.getItems();
                    }
                }
                request.setAdditionalInfo(additionalInfo);
            }
            if(request.getStatus() != RequestType.REQUEST_DISMISSED) {
                if(serviceRequestsById.containsKey(request.getId())) {
                    serviceRequestUpdated(request);
                }
                else {
                    serviceRequestAdded(request);
                    //serviceRequestsById.put(request.requestId, request);
                }
            }
            else {
                if(serviceRequestsById.containsKey(request.getId())) {
                    //serviceRequestsById.remove(request.requestId);
                    serviceRequestDismissed(request);
                }
            }
        }
    }

    public enum NotificationReply {CONFIRM, DENY};

    public interface NotificationsListener {
        public void serviceRequestReceived(ServiceRequest serviceRequest);
        public void serviceRequestUpdated(ServiceRequest serviceRequest);
        public void serviceRequestDismissed(ServiceRequest serviceRequest);
    }
}

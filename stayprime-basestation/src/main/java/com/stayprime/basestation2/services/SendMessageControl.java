/*
 *
 */
package com.stayprime.basestation2.services;

import com.stayprime.basestation2.ui.dialog.SendMessageActions;
import com.stayprime.basestation2.ui.dialog.SendMessageView;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.comm.gprs.request.RequestObserver;
import com.stayprime.hibernate.entities.MessageStatus;
import com.stayprime.hibernate.entities.OutgoingMessage;
import com.stayprime.storage.repos.OutgoingMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author benjamin
 */
@Component
public class SendMessageControl implements SendMessageActions {
    private int messageCounter = 0;

    private SendMessageView view;
    private RequestObserver requestObserver;

    @Autowired
    private OutgoingMessageRepo outgoingMessageRepo;

    public void setView(SendMessageView view) {
        this.view = view;
    }

    public void sendMessageToCart(int cartNumber, RequestObserver ackListener) {
        this.requestObserver = ackListener;
        view.setCartNumber(cartNumber);
        view.setup();
        view.setVisible(true);
    }

    @Override
    public void sendMessage(int cartNumber, String text) {
        try {
            OutgoingMessage msg = new OutgoingMessage(cartNumber, PacketType.TEXT_MESSAGE.id, 0, text);
            msg.getSendStatus().setMessageCounter(getMessageCounter());
            new CheckMessageThread(msg, requestObserver).start();
            view.setVisible(false);
        }
        catch (Exception ex) {
            view.showException(ex);
        }
    }

    private int getMessageCounter() {
        return messageCounter = (messageCounter + 1) % 255;
    }

    @Override
    public void cancel() {
        this.requestObserver = null;
        view.setVisible(false);
    }

    private class CheckMessageThread extends Thread {
        private final OutgoingMessage msg;
        private final RequestObserver requestObserver;

        public CheckMessageThread(OutgoingMessage msg, RequestObserver requestObserver) {
            super("CheckMessageThread-cart" + msg.getToCart());
            this.msg = msg;
            this.requestObserver = requestObserver;
        }

        @Override
        public void run() {
            int id = 0;
            try {
                id = outgoingMessageRepo.save(msg).getId();
                requestObserver.requestSent(null);
            }
            catch (Exception ex) {
                view.showException(ex);
            }

            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(5000);
                    OutgoingMessage upd = outgoingMessageRepo.findOne(id);

                    if (upd.getSendStatus().getStatus() == MessageStatus.SUCCESS) {
                        requestObserver.requestComplete(null, null);
                        return;
                    }
                    else if (upd.getSendStatus().getStatus() == MessageStatus.FAIL) {
                        requestObserver.requestFailed(null);
                        return;
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            requestObserver.requestFailed(null);
        }
    }

}

/*
 * 
 */
package com.stayprime.basestation2.ui.mainview;

import com.stayprime.basestation2.renderers.DashboardCartRenderer;
import com.stayprime.basestation2.renderers.DashboardCartRenderer.RenderInfo;
import com.stayprime.basestation2.ui.custom.NoViewPortCustomBalloonTip;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.hibernate.entities.CartInfo;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.CustomBalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import net.java.balloontip.utils.FadingUtils;

/**
 *
 * @author benjamin
 */
public class DashboardBalloonLayer implements DashboardCartRenderer.RenderListener<CartInfo> {
    private final Map<Integer, CustomBalloonTip> cartBalloons;
    private final JComponent dashboard;
    private final DashboardCartRenderer cartRenderer;

    public DashboardBalloonLayer(JComponent dashboard, DashboardCartRenderer cartRenderer) {
        this.dashboard = dashboard;
        this.cartRenderer = cartRenderer;
        cartBalloons = new HashMap<Integer, CustomBalloonTip>();
    }

    @Override
    public void renderedObjectsUpdated(Collection<RenderInfo<CartInfo>> updatedCarts) {
        for (RenderInfo<CartInfo> ri : updatedCarts) {
            CartInfo cart = ri.getObject();
            int cartNumber = cart.getCartNumber();
            CustomBalloonTip cartBalloon = cartBalloons.get(cartNumber);
            if (cartBalloon != null) {
                Rectangle bounds = cartRenderer.getBounds(cart);
                cartBalloon.setOffset(bounds);
            }
        }
    }

    public void updateBalloonTips(final Collection<CartInfo> status) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (final CartInfo cart : status) {
                    if (cart != null) {
                        updateCartBalloonTip(cart);
                    }
                }
            }
        });
    }

    private void updateCartBalloonTip(CartInfo cart) {
        NoViewPortCustomBalloonTip cartBalloon = (NoViewPortCustomBalloonTip) cartBalloons.get(cart.getCartNumber());
        StringBuilder message = new StringBuilder();
        if (cart.getStatusFlag(PacketType.STATUS_RESTRICTEDZONE)) {
            message.append("Cart ").append(cart.getCartNumber()).append(" in Restricted Zone.");
        }
        else if (cart.getStatusFlag(PacketType.STATUS_OUTOFCARTPATH)) {
            message.append("Cart ").append(cart.getCartNumber()).append(" out of Cart Path.");
        }
        if (cartBalloon == null) {
            JLabel component = new JLabel(message.toString());
            component.setFont(component.getFont().deriveFont(component.getFont().getSize() * 0.8f));
            cartBalloon = new NoViewPortCustomBalloonTip(dashboard, component, null, new RoundedBalloonStyle(4, 3, Color.white, Color.black), BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.ALIGNED, 7, 7, false);
            cartBalloon.setVisible(false);
            cartBalloons.put(cart.getCartNumber(), cartBalloon);
            final BalloonTip balloon = cartBalloon;
            cartBalloon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (balloon.getOpacity() > 0f) {
                        FadingUtils.fadeOutBalloon(balloon, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                balloon.setVisible(false);
                            }
                        }, 200, 24);
                    }
                }
            });
        }
        JLabel component = (JLabel) cartBalloon.getContents();
        Rectangle bounds = cartRenderer.getBounds(cart);
        if (message.length() > 0 && bounds != null) {
            //message.insert(0, "<html>");
            String newMessage = message.toString();
            String oldMessage = component.getText();
            cartBalloon.setOffset(bounds);
            //cartBalloon.setVisible(true);
            if (!newMessage.equals(oldMessage)) {
                component.setText(newMessage);
                if (!cartBalloon.isVisible() || cartBalloon.getOpacity() <= 0f) {
                    FadingUtils.fadeInBalloon(cartBalloon, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                        }
                    }, 200, 24);
                }
            }
        }
        else {
            component.setText(null);
            //cartBalloon.setVisible(false);
            final BalloonTip balloon = cartBalloon;
            if (cartBalloon.isVisible()) {
                if (balloon.getOpacity() > 0f) {
                    FadingUtils.fadeOutBalloon(cartBalloon, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            balloon.setVisible(false);
                        }
                    }, 200, 24);
                }
                else {
                    balloon.setVisible(false);
                }
            }
        }
    }

}

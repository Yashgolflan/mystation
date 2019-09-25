/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.renderers;

import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.aeben.elementos.mapview.BasicObjectRenderer;
import com.aeben.elementos.mapview.MapProjection;
import com.aeben.elementos.mapview.ObjectRenderer;
import com.stayprime.basestation2.ui.BaseStation2View;
import com.stayprime.basestation2.ui.mainview.CartInfoFilter;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.geo.Coordinates;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.view.golfcourse.GolfObjectsRenderer;
import com.stayprime.view.objects.Alignment;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 *
 * @author benjamin
 */
public class DashboardCartRenderer extends BasicObjectRenderer implements ObjectRenderer {
    private int paceOfPlayCautionThreshold = 0;
    private int paceOfPlayWarningThreshold = -600;

    private GolfObjectsRenderer golfObjectsRenderer;

    private RenderListener<CartInfo> renderListener;

    private Font font = new Font("Droid Sans", Font.BOLD, 14);
    private final Map<Integer, RenderInfo<CartInfo>> renderInfo;

    public DashboardCartRenderer() {
        golfObjectsRenderer = new GolfObjectsRenderer();
        golfObjectsRenderer.loadCartImages();
        golfObjectsRenderer.loadOverlayIcons();
        renderInfo = new HashMap<>();
    }

    public void setRenderListener(RenderListener renderListener) {
        this.renderListener = renderListener;
    }

    public void setPaceOfPlayThresholds(int caution, int warning) {
        paceOfPlayCautionThreshold = caution;
        paceOfPlayWarningThreshold = warning;
    }

    public void cartInfoUpdated(Collection<CartInfo> carts) {
        //TODO: check synchronization
        HashSet<Integer> notFound = new HashSet<>(renderInfo.keySet());

        for (CartInfo cart : carts) {
            notFound.remove(cart.getCartNumber());
            RenderInfo<CartInfo> ri = renderInfo.get(cart.getCartNumber());
            if (ri == null) {
                ri = new RenderInfo<>(cart);
            }
            ri.setObject(cart);
            renderInfo.put(cart.getCartNumber(), ri);
        }
    }

    @Override
    public void renderObjects(Graphics g, MapProjection p, boolean fast) {
        if(renderInfo.isEmpty() == false) {
            Graphics2D g2d = setupGraphics2d((Graphics2D) g.create(), fast);
            BaseStation2View view = BaseStation2App.getApplication().getMainView();
            CartInfoFilter filter = view.getCartInfoFilter();
            Stroke stroke = new BasicStroke(1f);

            for(RenderInfo<CartInfo> ri : renderInfo.values()) {
                CartInfo cart = ri.getObject();
                Coordinates pos = cart.getPosition();
                if (pos != null && (filter == null || filter.includeCart(cart))) {
                    Color paint = golfObjectsRenderer.getCartStatusPaint(getPaceIndex(cart));
                    renderCart(cart, paint, Color.black, Color.black, font, stroke, g2d, p);
                }
            }
            g2d.dispose();
            notifyObjectsRendered();
        }
    }

    private Graphics2D setupGraphics2d(Graphics2D g2d, boolean fast) {
//            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                    quickDraw? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR :
//                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    fast? RenderingHints.VALUE_ANTIALIAS_OFF :
                        RenderingHints.VALUE_ANTIALIAS_ON);
            return g2d;
    }

    private void renderCart(CartInfo cart, Color back, Color fore, Color text, Font font, Stroke stroke, Graphics2D g2d, MapProjection p) {
        Point2D pos = p.project(cart.getPosition());
        Integer mode = cart.getCartMode();

        if (mode == null || mode == PacketType.STATUS_APPMODE_GOLF) {
            renderColoredCartIcon(cart, font, g2d, pos, p.getScale());
        }
        else if (mode == PacketType.STATUS_APPMODE_GOLF_HANDICAP) {
            renderColoredCartIcon(cart, font, g2d, pos, p.getScale());
            golfObjectsRenderer.renderUnlockedIcon(g2d, pos, p.getScale());
        }
        else if (mode == PacketType.STATUS_APPMODE_RANGER) {
            renderColoredCartIcon(cart, font, g2d, pos, p.getScale());
            golfObjectsRenderer.renderRangerIcon(g2d, pos, p.getScale());
        }        
        else if (mode == PacketType.STATUS_APPMODE_ATAC) {
            renderColoredCartIcon(cart, font, g2d, pos, p.getScale());
            golfObjectsRenderer.renderRangerIcon(g2d, pos, p.getScale());
        }
    }

    private void renderColoredCartIcon(CartInfo cart, Font font, Graphics2D g2d, Point2D pos, double scale) {
        int paceIndex = getPaceIndex(cart);
        float heading = cart.getHeading() == null? 0f : cart.getHeading();
        String cartNumber = Integer.toString(cart.getCartNumber());
        Color paceColor = golfObjectsRenderer.getCartStatusPaint(paceIndex);

        golfObjectsRenderer.renderColoredCartGraphic(g2d, pos, heading, paceColor, scale);
        golfObjectsRenderer.renderBalloon(g2d, pos, cartNumber, font,
                Alignment.TOP_CENTER, null, null, 0, -10);
    }

    private int getPaceIndex(CartInfo cart) {
        if (cart.getLocationLastUpdated() == null) {
            return 0;
        }

        Integer mode = cart.getCartMode();
        if (mode != null) {
            if (mode == PacketType.STATUS_APPMODE_RANGER) {
                return 1;
            }
            else if (mode == PacketType.STATUS_APPMODE_ATAC) {
                return 1;
            }
        }

        CourseSettingsManager courseSettingsManager = BaseStation2App.getApplication().getCourseSettingsManager();
        int commLostTimeout = courseSettingsManager.getCommunicationLostTimeout();

        Calendar updateLimit = new GregorianCalendar();
        updateLimit.setTime(cart.getLocationLastUpdated());
        updateLimit.add(Calendar.SECOND, commLostTimeout);
        Integer pace = cart.getPaceOfPlay();

        if (updateLimit.before(new GregorianCalendar())) {
            return 0;
        }
        else if(cart.isPlaying()) {
            if (pace < paceOfPlayWarningThreshold) {
                return 4;
            }
            else if (pace < paceOfPlayCautionThreshold) {
                return 3;
            }
            else {
                return 2;
            }
        }

        return 1;
    }

    public Rectangle getBounds(CartInfo cart) {
        RenderInfo<CartInfo> ri = renderInfo.get(cart.getCartNumber());
        return ri == null? null : ri.getBounds();
    }

    private void notifyObjectsRendered() {
        if (renderListener != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    renderListener.renderedObjectsUpdated(renderInfo.values());
                }
            });
        }
    }

    public static class RenderInfo<T> {
        private T object;
        private Rectangle bounds;

        public RenderInfo(T obj) {
            this.object = obj;
        }

        public void setObject(T object) {
            this.object = object;
        }

        public T getObject() {
            return object;
        }

        public void setBounds(Rectangle bounds) {
            this.bounds = bounds;
        }

        public Rectangle getBounds() {
            return bounds;
        }

    }

    public interface RenderListener<T> {
        public void renderedObjectsUpdated(Collection<RenderInfo<T>> objects);
    }

}

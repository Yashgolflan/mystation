/*
 *
 */
package com.stayprime.view.golfcourse;

import com.stayprime.golf.course.Site;
import com.stayprime.model.golf.GolfCart;
import com.stayprime.golf.util.GolfColorRules;
import com.stayprime.model.golf.Position;
import com.stayprime.view.TransformView;
import com.stayprime.view.map.MapView;
import com.stayprime.view.objects.Alignment;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders a single cart (self) and a list of carts on a TransformView.
 * @author benjamin
 */
public class GolfCartsView extends GolfObjectsView {
    private GolfColorRules colorRules;
    Font font = new java.awt.Font("Droid Sans", Font.BOLD, 14);

    //Current golf club information
    private Site golfClub;

    //Position of this cart
    private GolfCart golfCart;

    //Current cart list
    private List<GolfCart> golfCartList;

    /*
     * Transformed objects
     */

    //Self golf cart
    private final TransformGolfCart mapGolfCart;

    //Dynamic cart ahead list
    private final List<GolfCart> mapGolfCarts;

    /*
     * Initialization
     */

    public GolfCartsView(MapView mapView, TransformView transformView) {
        super(mapView, transformView);

        mapGolfCart = new TransformGolfCart(transformView, mapView, 0, null, 0);
        mapGolfCarts = new ArrayList<GolfCart>();
        colorRules = new GolfColorRules();

        golfRenderer.loadCartImages();
        golfRenderer.loadCartAheadImages();
        golfRenderer.loadOverlayIcons();
    }

    /**
     * Set the current hole map and recalculate all the objects positions.
     * If the map is null it will clear the map and the objects.
     * @param golfClub the current hole map.
     */
    public void setGolfClub(Site golfClub) {
        this.golfClub = golfClub;

        if (golfClub != null) {
            setMap(golfClub.getMapImage());
        }
        else {
            setMap(null);
        }

        createMapGolfCartPosition();
        createMapGolfCartList();
    }

    public void setGolfCartPosition(GolfCart position) {
	this.golfCart = position;
        createMapGolfCartPosition();
    }

    /**
     * Set the current cart position and recalculate all the distance markers.
     * If the hole is null it will clear the objects.
     * @param position the current cart position.
     */
    private void createMapGolfCartPosition() {
        if(golfCart != null && golfCart.getPosition() != null && isValidMap()) {
            Position coord = golfCart.getPosition();
            double angle = golfCart.getHeading();

            mapGolfCart.setPosition(transPoint(coord.getY(), coord.getX()));
            mapGolfCart.setHeading(angle);
        }
        else {
            mapGolfCart.setPosition(null);
        }
    }

    /**
     * Set the current cart ahead list.
     * @param list the list of carts ahead to show.
     */
    public void setCartList(List<GolfCart> list) {
	this.golfCartList = list;
        createMapGolfCartList();
    }

    public void createMapGolfCartList() {
        mapGolfCarts.clear();

        if(golfCartList != null && isValidMap()) {
            for(GolfCart c: golfCartList) {
                Position pos = c.getPosition();
                if(pos==null)continue;
                TransformGolfCart cart = new TransformGolfCart(transformView, mapView, c.getNumber(), transPoint(pos.getY(), pos.getX()), c.getHeading());
                cart.setGolfRound(c.getGolfRound());
                mapGolfCarts.add(cart);
            }
        }

    }

//    /**
//     * Set the current cart ahead list.
//     * @param list the list of carts ahead to show.
//     */
//    public void setCartsStatus(Collection<CartStatus> list) {
//	golfCartList = new ArrayList<GolfCart>();
//        for (CartStatus s: list) {
//            double heading = s.heading == null? 0 : s.heading;
//            GolfCart c = new GolfCart(0, s.cartNumber, null, heading);
//
//            Coordinates l = s.location;
//            if (l != null) {
//                c.setPosition(new Point2D.Double(l.longitude, l.latitude));
//            }
//
//            golfCartList.add(c);
//        }
//    }

    /*
     * Render Golf Carts
     */

    @Override
    public boolean render(Graphics2D g2d, AffineTransform parentTransform, boolean quick) {
        double scale = getMapScale();

        for (GolfCart cart: mapGolfCarts) {
//            golfRenderer.renderCartGraphic(g2d, cart.getPosition(), cart.getHeading(), scale);
//            golfRenderer.render(g2d, cart.getPosition(), String.valueOf(cart.getCartNumber()), color);
            renderCart(g2d, cart, scale);
        }

        if(mapGolfCart.getPosition() != null) {
            Position position = mapGolfCart.getPosition();
            double heading = mapGolfCart.getHeading();
            golfRenderer.renderCartGraphic(g2d, position, heading, scale);
        }

        return false;
    }

    private void renderCart(Graphics2D g2d, GolfCart cart, double scale) {
//        Point2D pos = p.project(cart.location);

//        if (cart.mode == null || cart.mode == PacketType.STATUS_APPMODE_GOLF) {
            renderColoredCartIcon(cart, font, g2d, cart.getPosition(), scale);
//        }
//        else if (cart.mode == PacketType.STATUS_APPMODE_GOLF_HANDICAP) {
//            renderColoredCartIcon(cart, font, g2d, pos, p.getScale());
//            golfRenderer.renderUnlockedIcon(g2d, pos, p.getScale());
//        }
//        else if (cart.mode == PacketType.STATUS_APPMODE_RANGER) {
//            renderColoredCartIcon(cart, font, g2d, pos, p.getScale());
//            golfRenderer.renderRangerIcon(g2d, pos, p.getScale());
//        }
    }

    private void renderColoredCartIcon(GolfCart cart, Font font, Graphics2D g2d, Position pos, double scale) {
        Color color = colorRules == null? Color.white : colorRules.getColor(cart);
//        int paceIndex = getPaceIndex(cart);
        String cartNumber = Integer.toString(cart.getNumber());
//        Color paceColor = golfRenderer.getCartStatusPaint(paceIndex);

        golfRenderer.renderColoredCartGraphic(g2d, pos, cart.getHeading(), color, scale);
        golfRenderer.renderBalloon(g2d, pos, cartNumber, font,
                Alignment.TOP_CENTER, null, null, 0, -10);
    }
}

/*
 *
 */
package com.stayprime.tournament.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CartAssignments {

    private final TreeMap<Integer, CartAssignment> assignments;

    public CartAssignments() {
        assignments = new TreeMap<Integer, CartAssignment>();
    }

    public boolean isEmpty() {
        return assignments.isEmpty();
    }

    public List<Player> getCartPlayers(int cart) {
        CartAssignment ca = assignments.get(cart);
        if (ca != null) {
            return ca.getPlayers();
        }
        return null;
    }

    public void setCartPlayers(Integer cart, List<Player> players) {
        CartAssignment assignment = getOrCreateAssignment(cart);
        assignment.setPlayers(players);

        if (players == null) {
            assignments.remove(cart);
        }
    }

    public boolean isForScoring(int cartNumber) {
        CartAssignment a = assignments.get(cartNumber);
        return a != null && a.isForScoring();
    }

    public void setForScoring(int cart, boolean forScoring) {
        setForScoring(cart, forScoring, null);
    }

    private void setForScoring(int cart, boolean forScoring, Iterator<Integer> it) {
        if (forScoring) {
            CartAssignment a = getOrCreateAssignment(cart);
            a.setForScoring(true);
        }
        else {
            CartAssignment a = assignments.get(cart);
            if (a != null && a.getPlayers().isEmpty()) {
                a.setForScoring(false);
                if (it != null) {
                    it.remove();
                }
                else {
                    assignments.remove(cart);
                }
            }
        }
    }

    public String getCartsForScoring() {
        StringBuilder carts = new StringBuilder();
        for (CartAssignment a : getAssignmentsList()) {
//            if (a.isForScoring() && a.getPlayers().isEmpty()) {
            if (a.isForScoring() ) {
                carts.append(a.getCartNumber()).append(' ');
            }
        }

        int length = carts.length();
        if (length == 0) {
            return StringUtils.EMPTY;
        }
        else {
            return carts.deleteCharAt(length - 1).toString();
        }
    }

    public void setCartsForScoring(String s) {
        try {
            for (Iterator<Integer> i = assignments.keySet().iterator(); i.hasNext();) {
                Integer cart = i.next();
                setForScoring(cart, false, i);
            }

            String[] carts = StringUtils.split(s, ' ');
            for (String c : carts) {
                int cart = NumberUtils.toInt(c);
                if (cart > 0) {
                    setForScoring(cart, true);
                }
            }
        }
        catch (Exception ex) {
        }
    }

    public Collection<CartAssignment> getAssignmentsList() {
        return assignments.values();
    }

    public void clear() {
        assignments.clear();
    }

    public void addCartPlayer(Integer cart, Player p) {
        if (p != null) {
            getOrCreateAssignment(cart).addPlayer(p);
        }
    }

    private CartAssignment getOrCreateAssignment(Integer cart) {
        if (!validateCart(cart)) {
            return null;
        }

        CartAssignment players = assignments.get(cart);

        if (players == null) {
            players = new CartAssignment(cart);
            assignments.put(cart, players);
        }

        return players;
    }

    public static boolean validateCart(Integer cart) {
        return cart != null && cart > 0;
    }

    public void loadFromPlayersList(List<Player> players) {
        clear();
        if (CollectionUtils.isNotEmpty(players)) {
            for (Player p : players) {
                if (CartAssignments.validateCart(p.getCart())) {
                    addCartPlayer(p.getCart(), p);
                }
            }
        }
    }

}

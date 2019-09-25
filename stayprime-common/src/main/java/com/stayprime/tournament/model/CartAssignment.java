/*
 * 
 */
package com.stayprime.tournament.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benjamin
 */
public class CartAssignment implements Comparable {
    private final int cartNumber;
    private final List<Player> players;
    private boolean forScoring;

    public CartAssignment(int cartNumber) {
        this.cartNumber = cartNumber;
        this.players = new ArrayList<Player>(2);
    }

    public int getCartNumber() {
        return cartNumber;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> newPlayers) {
        for (Player p : players) {
            p.setCart(null);
        }

        players.clear();
        if (newPlayers != null) {
            for (Player p : newPlayers) {
                addPlayer(p);
            }
        }
    }

    public void removePlayer(Player p) {
        boolean removed = players.remove(p);
        if (removed) {
            p.setCart(null);
        }
    }

    public void addPlayer(Player p) {
        p.setCart(cartNumber);
        players.add(p);
        setForScoring(true);
    }

    public boolean isForScoring() {
        return forScoring;
    }

    public void setForScoring(boolean useForScoring) {
        this.forScoring = useForScoring || players.isEmpty() == false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof CartAssignment) {
            CartAssignment ca = (CartAssignment) o;
            return Integer.compare(cartNumber, ca.getCartNumber());
        }
        return Integer.MAX_VALUE;
    }

}

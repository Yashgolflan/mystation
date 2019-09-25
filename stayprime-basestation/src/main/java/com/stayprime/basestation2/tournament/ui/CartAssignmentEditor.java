/*
 *
 */
package com.stayprime.basestation2.tournament.ui;

import com.stayprime.ui.editor.dnd.GenericTransferHandler;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import com.stayprime.hibernate.entities.CartInfo;
import com.stayprime.tournament.model.CartAssignment;
import com.stayprime.tournament.model.CartAssignments;
import com.stayprime.tournament.model.Player;
import com.stayprime.tournament.model.PlayerScores;
import com.stayprime.tournament.model.TournamentRound;
import com.stayprime.tournament.util.TournamentUtil;
import com.stayprime.ui.editor.EditorPanel;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.activation.ActivationDataFlavor;
import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/**
 *
 * @author benjamin
 */
public class CartAssignmentEditor extends EditorPanel<TournamentRound> {

    /**
     * Creates new form CartAssignmentEditor
     */
    public CartAssignmentEditor() {
        initComponents();
    }

    public void init() {
        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        ActivationDataFlavor dataFlavor = new ActivationDataFlavor(Player.class, "application/x-stayprime-Player;class=com.stayprime.tournament.model.Player", "Player");

        JTable playerTable = playerList1.getTable();
        //playerTable.setColumnSelectionAllowed(true);
        playerTable.setDragEnabled(true);
        playerTable.setDropMode(DropMode.INSERT_ROWS);
        playerTable.setTransferHandler(new GenericTransferHandler<Player>(playerList1, dataFlavor));
        playerList1.setSelectionMode(DefaultEventSelectionModel.SINGLE_SELECTION);

        JTable cartTable = cartListEditor1.getTable();
        cartTable.setColumnSelectionAllowed(true);
        cartTable.setDragEnabled(true);
        cartTable.setDropMode(DropMode.ON);
        cartTable.setTransferHandler(new CartListTransferHandler(cartListEditor1, dataFlavor));
        cartListEditor1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void setCartCount(int count) {
        cartListEditor1.setCartCount(count);
    }

    public void setCarts(Collection<CartInfo> carts) {
        cartListEditor1.setCarts(carts);
    }

    @Override
    public void setEditingObject(TournamentRound t) {
        super.setEditingObject(t);
        clearCartPlayers();
        List<Player> playerList = t == null? null : t.getPlayersList();
        List<Player> players = new ArrayList<>(playerList);
        playerList1.setList(players);
    }

    public void clearCartPlayers() {
        cartListEditor1.clearCartPlayers();
    }

    public void setAssignmentsFromTournament() {
        TournamentRound r = getEditingObject();
        if (r != null) {
            cartListEditor1.takeCartsForScoring(r.getCartAssignments());

            //Temporary CartAssignments to keep track of players in carts
            CartAssignments tmpAssignments = new CartAssignments();

            //Loop to remove assigned players from playerListEditor
            for (Iterator<Player> i = playerList1.getSourceList().iterator(); i.hasNext();) {
                Player p = i.next();
                if (p.getCart() != null) {
                    int cart = p.getCart();
                    if (addPlayerToCart(tmpAssignments, p, cart)) {
                        i.remove();
                    }
                }
            }
        }
    }

    /**
     * Adds a player to a cart in the cartListEditor if there room in the cart.
     * @param assignments CartAssignments to keep track of players per cart
     * @param p Player to add to assignments and to the cartListEditor
     * @param cart cart number to add the player to
     * @return true if the player was added successfully
     */
    private boolean addPlayerToCart(CartAssignments assignments, Player p, int cart) {
        if (cart <= 0) {
            return false;
        }

        List<Player> cartPlayers = assignments.getCartPlayers(cart);
        if (cartPlayers == null || cartPlayers.size() < 2) {
            assignments.addCartPlayer(cart, p);
            cartListEditor1.addCartPlayer(cart, p);
            return true;
        }
        else {
            //Cart has already 2 players
            p.setCart(null);
            return false;
        }
    }

    @Override
    public boolean applyChanges() {
        TournamentRound round = getEditingObject();
        if (round == null) {
            return false;
        }

        //Clear cart assignment from the player list on the left
        for (Player p : playerList1.getSourceList()) {
            p.setCart(null);
        }

        //Set players cart assignment from cartListEditor
        for (CartAssignment assignment : cartListEditor1.getSourceList()) {
            int cart = assignment.getCartNumber();
            for (Player p : assignment.getPlayers()) {
                p.setCart(cart);
            }
        }

        //Update the tournament's cartAssignments from the players
        //(This wipes carts for scoring)
        round.updateCartAssignmentsFromPlayers();

        //Update the carts selected for tournament from cartListEditor
        CartAssignments ca = round.getCartAssignments();
        for (CartAssignment assignment : cartListEditor1.getSourceList()) {
            if (assignment.isForScoring()) {
                ca.setForScoring(assignment.getCartNumber(), true);
            }
        }

        return true;
    }

    /**
     * Copy assignments into the players of this playerList.
     * @param newAssignments the cartAssignments to copy.
     */
    public void copyAssignments(Collection<CartAssignment> newAssignments) {
        //Temporary asssingments to keep track of the max players per cart
        CartAssignments tmpAssignments = new CartAssignments();
        for (CartAssignment a : newAssignments) {
            int cart = a.getCartNumber();

            if (a.isForScoring()) {
                tmpAssignments.setForScoring(cart, true);
            }

            for (Player p1 : a.getPlayers()) {
                findPlayerAndSetCartAssignment(tmpAssignments, cart, p1);
            }
        }

        cartListEditor1.takeCartsForScoring(tmpAssignments);
    }

    private void findPlayerAndSetCartAssignment(CartAssignments tmpAssignments, int cart, Player oldP) {
        EventList<Player> players = playerList1.getSourceList();
        //Find the player in playerList that matches the extId
        int i = TournamentUtil.findPlayerByExtId(players, oldP.getExtId());

        if (i >= 0) {
            //Use the player from playerList because otherwise we set
            //the cart number on a Player instance that is not in the
            //player list that we are editing.
            Player p = players.get(i);
            if (addPlayerToCart(tmpAssignments, p, cart)) {
                players.remove(i);
            }
        }
    }

    public Collection<CartAssignment> getAssignments() {
        return cartListEditor1.getSourceList();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerList1 = new com.stayprime.basestation2.tournament.ui.PlayerListEditor();
        cartListEditor1 = new com.stayprime.basestation2.tournament.ui.CartListEditor();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        playerList1.setName("playerList1"); // NOI18N
        add(playerList1);

        cartListEditor1.setName("cartListEditor1"); // NOI18N
        add(cartListEditor1);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.stayprime.basestation2.tournament.ui.CartListEditor cartListEditor1;
    private com.stayprime.basestation2.tournament.ui.PlayerListEditor playerList1;
    // End of variables declaration//GEN-END:variables

    private static class CartListTransferHandler extends GenericTransferHandler<Player> {
        private final CartListEditor cartListEditor;
        private final int maxPlayers = 2;
        private final int player1Col = 2;
        private final int player2Col = player1Col + maxPlayers;

        public CartListTransferHandler(CartListEditor editor, DataFlavor dataFlavor) {
            super(editor, dataFlavor);
            this.cartListEditor = editor;
        }

        @Override
        protected Player getTransferableObject() {
            int col = table.getSelectedColumn();
            if (col >= player1Col && col <= player2Col) {
                int row = table.getSelectedRow();
                CartAssignment ca = cartListEditor.getViewList().get(row);
                if (ca != null && ca.getPlayers().size() > col - player1Col) {
                    return ca.getPlayers().get(col - player1Col);
                }
            }
            return null;
        }

        @Override
        protected boolean importData(Player p, int row, int col) {
            EventList<CartAssignment> list = cartListEditor.getViewList();
            CartAssignment ca = list.get(row);
            List<Player> players = ca.getPlayers();
            if (players.size() < maxPlayers && players.contains(p) == false) {
                ca.addPlayer(p);
                list.set(row, ca);
                importCol = player1Col + players.size() - 1;
                return true;
            }
            return false;
        }

        @Override
        protected void removeExported(Player p) {
            //We shouldn't use the selected row to find the exported object,
            //it could change! Instead we should track it at the moment of
            //creating the transferable (getSelectedObject)
            if (exportRow >= 0) {
                EventList<CartAssignment> list = cartListEditor.getViewList();
                Integer cart = p.getCart();
                CartAssignment ca = list.get(exportRow);
                ca.removePlayer(p);
                if (imported == exported) {
                    //The right cart is already set by importData
                    p.setCart(cart);
                }
                list.set(exportRow, ca);
            }
        }
    }

}

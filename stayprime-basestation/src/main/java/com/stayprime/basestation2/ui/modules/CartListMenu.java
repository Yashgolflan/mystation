/*
 *
 */
package com.stayprime.basestation2.ui.modules;

import com.alee.laf.menu.WebMenuItem;
import com.alee.managers.hotkey.HotkeyData;
import com.stayprime.comm.encoder.PacketType;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 *
 * @author benjamin
 */
public class CartListMenu extends MouseAdapter implements ActionListener {
    private JPopupMenu popupMenu;
    private int cartNumber = 0;

    public CartListMenu() {
        popupMenu = new JPopupMenu();
        for (CartListMenu.Action a: CartListMenu.Action.values()) {
            JMenuItem m = new JMenuItem(a.description);
            m.addActionListener(this);
            popupMenu.add(m);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
//            Application.getInstance().getContext()
//                    .getActionMap(cartManagement).get("editCart").actionPerformed(
//                            new ActionEvent(null, ActionEvent.ACTION_PERFORMED, "editCart"));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        processPopupTrigger(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        processPopupTrigger(e);
    }

    private void processPopupTrigger(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JTable source = (JTable) e.getSource();
            int row = source.rowAtPoint(e.getPoint());
            int column = source.columnAtPoint(e.getPoint());
            if (!source.isRowSelected(row)) {
                source.changeSelection(row, column, false, false);
            }
            if (source.isRowSelected(row)) {
                showPopup(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WebMenuItem menu = (WebMenuItem) e.getSource();
        Action action = Action.getByDescription(menu.getText());
        if (action != null && action.command > 0) {
            sendCommand(action);
        }
    }

    public void setCartNumber(int cartNumber) {
        this.cartNumber = cartNumber;
    }

    private void sendCommand(Action action) {
        if (cartNumber > 0) {
            //TODO: need to send command through UDP server
//            CommServer commServer = BaseStation2App.getApplication().getManager().getCommServer();
//            commServer.sendPredefinedMessage(cartNumber, action.command);
        }
    }

    public void showPopup(Component invoker, int x, int y) {
        popupMenu.show(invoker, x, y);
    }

    public enum Action {
        //DELETE_CART("Delete Cart", Hotkey.CTRL_D, 0),
        //PING_CART("Ping Cart", Hotkey.CTRL_P, null),
        CALIBRATE("Calibrate", null, PacketType.CALIBRATE_COMMAND),
//        WIFI_ENABLE("Wifi Enable", Hotkey.CTRL_W, PacketType.WIFI_ENABLE),
        RESTART_UNIT("Restart Unit", null, PacketType.RESTART_COMMAND);
//        ENABLE_CARTKILL("Enable CartKill", Hotkey.CTRL_SHIFT_Z, PacketType.ENABLE_CARTKILL),
//        DISABLE_CARTKILL("Disable CartKill", Hotkey.CTRL_SHIFT_S, PacketType.DISABLE_CARTKILL);

        public final String description;
        public final HotkeyData hotkey;
        public final int command;

        private Action(String description, HotkeyData hotkey, int command) {
            this.description = description;
            this.hotkey = hotkey;
            this.command = command;
        }

        @Override
        public String toString() {
            return description;
        }

        public static Action getByDescription(String desc) {
            for (Action a: values()) {
                if (a.description.equals(desc)) {
                    return a;
                }
            }
            return null;
        }
    }

}

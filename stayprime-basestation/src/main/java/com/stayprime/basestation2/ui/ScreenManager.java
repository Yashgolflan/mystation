/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.basestation2.ui;

import com.stayprime.legacy.screen.Screen;
import com.stayprime.legacy.screen.ScreenParent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JToggleButton;

/**
 *
 * @author benjamin
 */
public class ScreenManager implements ItemListener, ActionListener, ScreenParent {
    //private List<Pair<Screen, List<JComponent>>> screens;
    private Map<AbstractButton, Screen> buttonScreens;
    private Map<Screen, List<AbstractButton>> screenButtons;
    private ScreenParent screenParent;
    private Screen pendingScreen, defaultScreen;

    protected Screen selectedScreen;
    public static final String PROP_SELECTEDSCREEN = "selectedScreen";

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


    public ScreenManager(ScreenParent screenParent, Screen defaultScreen) {
        this.screenParent = screenParent;
        this.defaultScreen = defaultScreen;
        buttonScreens = new HashMap<AbstractButton, Screen>();
        screenButtons = new HashMap<Screen, List<AbstractButton>>();
    }

    public boolean showScreen(Screen screen) {
        if(screen == selectedScreen) {
            setButtonsForScreenSelected(selectedScreen, true);
            return true;
        }
        else if(selectedScreen == null || selectedScreen.exitScreen()) {
            screen.enterScreen(this);

            pendingScreen = null;
            Screen previousScreen = selectedScreen;
            setSelectedScreen(screen);

            setButtonsForScreenSelected(previousScreen, false);

            setButtonsForScreenSelected(selectedScreen, true);

            screenParent.showScreen(selectedScreen);
            return true;
        }
        else {
 //           setButtonsForScreenSelected(screen, false);
            pendingScreen = screen;
            return false;
        }
    }

    public void exitScreen(Screen screen) {
        if(screen == selectedScreen) {
            if(pendingScreen != null)
                showScreen(pendingScreen);
            else if(defaultScreen != null)
                showScreen(defaultScreen);
        }
    }

    public void addScreenButton(Screen screen, AbstractButton button, boolean manageEvents) {
      //  button.setFocusPainted(Boolean.FALSE);
        buttonScreens.put(button, screen);
        if(!screenButtons.containsKey(screen)) {
            List<AbstractButton> list = new ArrayList<AbstractButton>(1);
            list.add(button);
            screenButtons.put(screen, list);
        }
        else {
            screenButtons.get(screen).add(button);
        }

        if(manageEvents) {
            if(button instanceof JToggleButton) {
                button.addItemListener(this);
            }
            else {
                button.addActionListener(this);
            }
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) e.getSource();
            Screen screen = buttonScreens.get(button);

            if(e.getStateChange() == ItemEvent.SELECTED) {
                if(showScreen(screen) == false)
                    button.setSelected(false);//change to false bc not shown
            }
            else {
                if(screen == selectedScreen) {
                    button.setSelected(true);
                  //  button.setFocusPainted(false);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) e.getSource();
            Screen screen = buttonScreens.get(button);
            showScreen(screen);
        }
    }

    private void setButtonsForScreenSelected(Screen screen, boolean selected) {
        if(screenButtons.get(screen) != null) {
            for(AbstractButton button: screenButtons.get(screen))
            {
                if(button instanceof JToggleButton && button.isSelected() != selected)
                {   button.setSelected(selected);
                //    button.setFocusPainted(Boolean.FALSE);
                }
                if(button instanceof JButton && button.isSelected()!=selected){
                    button.setSelected(selected);
                 //   button.setFocusPainted(Boolean.TRUE);
                    
                }
            }
        }

    }

    public Screen getSelectedScreen() {
        return selectedScreen;
    }

    public void setSelectedScreen(Screen selectedScreen) {
        Screen oldSelectedScreen = this.selectedScreen;
        this.selectedScreen = selectedScreen;
        propertyChangeSupport.firePropertyChange(PROP_SELECTEDSCREEN, oldSelectedScreen, selectedScreen);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.ui.swingx;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.PasswordStore;
import org.jdesktop.swingx.auth.UserNameStore;

/**
 *
 * @author benjamin
 */
public class CustomLoginPane extends JXLoginPane {
    private JXLabel bannerLabel;

    public CustomLoginPane() {
        this(null);
    }

    public CustomLoginPane(LoginService service) {
        this(service, null, null);
    }

    public CustomLoginPane(LoginService service, PasswordStore passwordStore, UserNameStore userStore) {
        this(service, passwordStore, userStore, null);
    }

    public CustomLoginPane(LoginService service, PasswordStore passwordStore, UserNameStore userStore, List<String> servers) {
        super(service, passwordStore, userStore, servers);

        BorderLayout layout = (BorderLayout) getLayout();
        layout.removeLayoutComponent(layout.getLayoutComponent("North"));

        bannerLabel = new JXLabel();
        bannerLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(bannerLabel, "North");
    }

    public JXLabel getBannerLabel() {
        return bannerLabel;
    }
}

/*
 * 
 */
package com.stayprime.cartapp.comm.asset;

import com.stayprime.localservice.CartUnitCommunication;
import com.stayprime.localservice.Constants;
import com.stayprime.cartapp.menu.Menu;
import com.stayprime.cartapp.menu.MenuItem;
import com.stayprime.cartapp.menu.MenuXMLLoader;
import com.stayprime.util.file.FileLocator;
import java.util.List;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class MenuLoader implements AssetInfoLoader<Menu> {

    private static final Logger log = LoggerFactory.getLogger(MenuLoader.class);

    public MenuLoader() {
    }

    @Override
    public boolean shouldUpdateFromServer(CartUnitCommunication comm, Menu menuList) {
        String remoteVersion = comm.getAssetVersion(Constants.menuUpdated);

        if (remoteVersion == null) {
            log.debug("Server returned null: don't update");
            return false;
        }

        if (menuList == null) {
            log.debug("Menu is null: update from server");
            return true;
        }

        String localVersion = menuList.getVersion();
        if (ObjectUtils.notEqual(localVersion, remoteVersion)) {
            log.debug("Menu out of date: update form server");
            return true;
        }

        log.trace("Menu is up to date.");
        return false;
    }

    @Override
    public Menu load(FileLocator fileLocator, ProgressCallback callback) {
        Menu menu = MenuXMLLoader.loadMenu(fileLocator.getFile("Menu.xml"));
        if (menu != null) {
            loadImages(menu.getFood(), fileLocator);
            loadImages(menu.getDrinks(), fileLocator);
            loadImages(menu.getSnacks(), fileLocator);
        }
        return menu;
    }

    private void loadImages(List<MenuItem> items, FileLocator fileLocator) {
        if (items != null) {
            for (MenuItem i : items) {
                if (StringUtils.isNotBlank(i.getIcon())) {
                    fileLocator.getFile(i.getIcon());
                }
                if (StringUtils.isNotBlank(i.getImage())) {
                    fileLocator.getFile(i.getImage());
                }
            }
        }
    }

}

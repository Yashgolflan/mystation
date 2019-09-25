/*
 * 
 */
package com.stayprime.cartapp.menu;

import com.stayprime.legacy.Xml;
import java.io.File;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.math.NumberUtils;
import org.w3c.dom.Element;

/**
 *
 * @author benjamin
 */
public class MenuXMLLoader {
    public static Menu loadMenu(File xmlFile) {
        String value;
        Element rootElement = (Element) Xml.rootElement(xmlFile, "Menu");

        if (rootElement != null) {
            Xml menuXml = new Xml(rootElement);
            Menu menu = new Menu();

            if ((value = menuXml.string("lastUpdated")) != null) {
                long time = NumberUtils.toLong(value);
                if (time > 0) {
                    menu.setLastUpdated(new Date(time));
                    menu.setVersion(Long.toString(time));
                }
            }

            Xml foodXml = menuXml.child("Food");
            loadItems(menu.getFood(), foodXml);

            Xml drinkXml = menuXml.child("Drinks");
            loadItems(menu.getDrinks(), drinkXml);

            Xml snacksXml = menuXml.child("Snacks");
            loadItems(menu.getSnacks(), snacksXml);

            Xml hutsXml = menuXml.child("Huts");
            loadHuts(menu.getHuts(), hutsXml);

            return menu;
        }
        return null;
    }

    public static void loadItems(List<MenuItem> items, Xml itemsXml) {
        if (itemsXml != null) {
            String value;
            List<Xml> itemsXmlList = itemsXml.children("Item");

            for (Xml itemXml : itemsXmlList) {
                MenuItem item = new MenuItem();

                if ((value = itemXml.string("id")) != null && !value.trim().equals("")) {
                    item.setId(Integer.parseInt(value));
                }
                if ((value = itemXml.string("name")) != null) {
                    item.setCode(value);
                }
                if ((value = itemXml.string("description")) != null) {
                    item.setDescription(value);
                }
                if ((value = itemXml.string("icon")) != null && !value.trim().equals("")) {
                    item.setIcon(value);
                }
                if ((value = itemXml.string("image")) != null && !value.trim().equals("")) {
                    item.setImage(value);
                }
                if ((value = itemXml.string("price")) != null && !value.trim().equals("")) {
                    item.setPrice(Float.parseFloat(value));
                }

                items.add(item);
            }
        }
    }

    public static void loadHuts(List<Hut> huts, Xml hutsXml) {
        if (hutsXml != null) {
            List<Xml> hutsXmlList = hutsXml.children("Hut");

            for (Xml itemXml : hutsXmlList) {
                int hutNumber = NumberUtils.toInt(itemXml.string("hutNumber"));
                if (hutNumber > 0) {
                    Hut hut = new Hut(hutNumber);
                    List<Xml> courses = itemXml.children("Course");

                    if (courses != null) {
                        for (Xml course : courses) {
                            List<Xml> holeList = course.children("Hole");
                            for (Xml itemHole : holeList) {
                                int index = huts.indexOf(hut);
                                if (index >= 0) {
                                    hut = huts.get(index);
                                }
                                else {
                                    huts.add(hut);
                                }
                                int holeNumber = NumberUtils.toInt(itemHole.string("holeNumber"));
                                if (holeNumber > 0) {
                                    hut.setForHole(holeNumber);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

/*
 * 
 */
package com.stayprime.tournament.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author benjamin
 */
public class XmlDateAdapter extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String marshal(Date v) throws Exception {
        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(String v) throws Exception {
        return dateFormat.parse(v);
    }

}

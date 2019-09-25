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
public class XmlDateTimeAdapter extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String marshal(Date v) throws Exception {
        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(String v) throws Exception {
        return dateFormat.parse(v);
    }

}

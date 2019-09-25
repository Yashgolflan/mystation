/*
 * 
 */
package com.stayprime.tournament.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author benjamin
 */
public class XmlScoresAdapter extends XmlAdapter<String, Integer[]> {

    @Override
    public String marshal(Integer[] v) throws Exception {
        return StringUtils.join(v, ',');
    }

    @Override
    public Integer[] unmarshal(String v) throws Exception {
        Integer[] result = null;
        String[] values = StringUtils.split(v, ',');
        if (values != null) {
            result = new Integer[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = NumberUtils.toInt(values[i]);
            }
        }
        return result;
    }

}

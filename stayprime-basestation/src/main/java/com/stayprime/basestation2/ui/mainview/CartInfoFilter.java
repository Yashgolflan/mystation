/*
 * 
 */
package com.stayprime.basestation2.ui.mainview;

import ca.odell.glazedlists.matchers.Matcher;
import com.aeben.golfclub.utils.Formatters;
import com.stayprime.basestation2.BaseStation2App;
import com.stayprime.basestation2.CourseSettingsManager;
import com.stayprime.comm.encoder.PacketType;
import com.stayprime.hibernate.entities.CartInfo;
import java.util.Date;

/**
 *
 * @author benjamin
 */
public class CartInfoFilter implements Matcher<CartInfo> {

    private PaceFilter filter = PaceFilter.All;
    private int communicationLostThreshold = 660;

    public void setCommunicationLostThreshold(int communicationLostThreshold) {
        this.communicationLostThreshold = communicationLostThreshold;
    }

    public void setFilter(PaceFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean matches(CartInfo item) {
        return includeCart(item);
    }

    public boolean includeCart(CartInfo cartStatus) {
        if (cartStatus != null) {
            return includeCart(filter, cartStatus.getLocationLastUpdated(),
                    cartStatus.getCartMode(), cartStatus.isPlaying(), cartStatus.getPaceOfPlay());
        }
        return false;
    }

    public boolean includeCart(PaceFilter filter, Date lastUpdated, Integer mode,
            boolean playing, Integer paceOfPlaySeconds) {
        if (filter == PaceFilter.All) {
            return true;
        }

        if (isCommunicationLost(lastUpdated)) {
            return false;
        }

        boolean isMarshall = mode != null && (mode == PacketType.STATUS_APPMODE_ATAC
                || mode == PacketType.STATUS_APPMODE_RANGER);

        if (isMarshall) {
            return true;
        }
        else if (filter == PaceFilter.Marshall) {
            return false;
        }

        CourseSettingsManager settings = BaseStation2App.getApplication().getCourseSettingsManager();
        int[] threshold = Formatters.getPaceOfPlayThresholdArray(settings.getPaceOfPlayThreshold());
        if (threshold == null) {
            return true;
        }
        if (playing == false) {
            return false;
        }
        else if (filter == PaceFilter.Caution) {
            return paceOfPlaySeconds < threshold[0];
        }
        else if (filter == PaceFilter.Warning) {
            return paceOfPlaySeconds < threshold[1];
        }
        else {
            return true;
        }

    }

    private boolean isCommunicationLost(Date lastUpdated) {
        if (lastUpdated == null) {
            return true;
        }

        long seconds = (System.currentTimeMillis() - lastUpdated.getTime()) / 1000;
        return seconds > communicationLostThreshold;
    }

}

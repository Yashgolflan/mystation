/*
 * 
 */

package com.stayprime.oncourseads;

/**
 *
 * @author benjamin
 */
public interface AdClient {
    public Integer getId();
    public String getName();
    public String getContactInfo();
    public String getEmail();
    public ReportPreferences getReportPreferences();
    public boolean isActive();
    public long getClientSince();
    public AdList getAdList();
}

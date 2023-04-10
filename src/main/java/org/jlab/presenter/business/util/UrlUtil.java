package org.jlab.presenter.business.util;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.presenter.persistence.entity.IFrameSlide;
import org.jlab.presenter.persistence.entity.Slide;

/**
 *
 * @author ryans
 */
public class UrlUtil {

    private final static Logger logger =
            Logger.getLogger(UrlUtil.class.getName());    
    
    private static final String hostname;
    
    static {
            // Wildfly, if configured to acknowledge proxy, may report proxy hostname via ServletRequest.getServerName, but env lookup means we don't have to be in servlet request context
            hostname = System.getenv("FRONTEND_SERVER_URL");

            logger.log(Level.FINEST, "Using FRONTEND_SERVER_URL: {0}", hostname);
    }
    
    private UrlUtil() {
        // Can't instantiate publicly
    }
    
    public static String getSrmUrl() {
        return System.getenv("SRM_URL") + "/reports/overall-status?print=Y&fullscreen=Y&qualified=";
    }    
     
    public static String getWorkmapUrl(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return System.getenv("WORKMAP_URL") + "/" + formatter.format(date);
    }     
 
    public static String getCalendarUrl(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("?'year='yyyy'&week='w");
        return System.getenv("CALENDAR_URL") + "/view-outlook" + formatter.format(date);
    }    
    
    public static String getPresentationUrl(BigInteger presentationId) {
        return hostname + "/presenter/presentation/" + presentationId + "#1";
    }    
    
    public static String getPresentationELogBodyUrl(BigInteger presentationId) {
        return hostname + "/presenter/elog-html?presentationId=" + presentationId;
    }        
    
    public static String getDailyBeamAccountingUrl(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 7); // Start is inclusive; end is exclusive so last hour is 6
        cal.set(Calendar.MINUTE, 0);
        Date end = cal.getTime();
        cal.add(Calendar.DATE, -1); // Grab 24 hour period
        Date start = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy'+'HH'%3A'mm");
        return System.getenv("BTM_URL") + "/reports/physics-summary?start=" + formatter.format(start) + "&end=" + formatter.format(end) + "&physics-data=available&print=Y&fullscreen=Y";
    }    
    
    public static String getDailyFsdUrl(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 7); // Start is inclusive; end is exclusive so last hour is 6
        cal.set(Calendar.MINUTE, 0);
        Date end = cal.getTime();
        cal.add(Calendar.DATE, -1); // Grab 24 hour period
        Date start = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy'+'HH'%3A'mm");
        return System.getenv("DTM_URL") + "/reports/fsd-summary?start=" + formatter.format(start) + "&end=" + formatter.format(end) + "&maxDuration=5&maxDurationUnits=Minutes&sadTrips=N&rateBasis=program&chart=bar&grouping=cause&binSize=HOUR&legendData=rate&legendData=lost&maxY=30&qualified=&print=Y&fullscreen=Y";
    }      
    
    public static String getWeeklyBeamAccountingUrl(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 7); // Start is inclusive; end is exclusive so last hour is 6
        cal.set(Calendar.MINUTE, 0);
        Date end = cal.getTime();
        cal.add(Calendar.DATE, -7); // Grab 7 day period (last week)
        Date start = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy'+'HH'%3A'mm");
        return System.getenv("BTM_URL") + "/reports/physics-summary?start=" + formatter.format(start) + "&end=" + formatter.format(end) + "&physics-data=available&print=Y&fullscreen=Y";
    }       
    
    public static String getWeeklyFsdUrl(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 7); // Start is inclusive; end is exclusive so last hour is 6
        cal.set(Calendar.MINUTE, 0);
        Date end = cal.getTime();
        cal.add(Calendar.DATE, -7); // Grab 7 day period (last week)
        Date start = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy'+'HH'%3A'mm");
        return System.getenv("DTM_URL") + "/reports/fsd-summary?start=" + formatter.format(start) + "&end=" + formatter.format(end) + "&maxDuration=5&maxDurationUnits=Minutes&sadTrips=N&rateBasis=program&chart=bar&grouping=cause&binSize=DAY&legendData=count&legendData=rate&legendData=lost&legendData=mins&maxY=30&qualified=&print=Y&fullscreen=Y";
    }

    public static String getChargeUrl(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); // Long term schedule is daily (not hourly)
        cal.set(Calendar.MINUTE, 0);
        Date end = cal.getTime();
        cal.add(Calendar.DATE, -7); // Grab 7 day period (last week)
        Date start = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return System.getenv("BTM_URL") + "/reports/charge?start=" + formatter.format(start) + "&end=" + formatter.format(end) + "&scale=0.5&print=Y&fullscreen=Y";
    }
    
    public static String getWeatherUrl() {
        return System.getenv("WEATHER_URL");
    }

    public static String getWhiteboardUrl() { return System.getenv("WHITEBOARD_URL"); }

    public static String getPowerUrl(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return System.getenv("POWER_URL") + "/presenter-graph.html?date=" + formatter.format(date);
    }
    
    public static Slide getHcoSlide() {
        return new IFrameSlide(getSrmUrl(), "HCO Readiness");
    }    
    
    public static IFrameSlide getWorkmapSlide(Date date) {
        return new IFrameSlide(getWorkmapUrl(date), "ATLis Workmap");
    }
    
    public static IFrameSlide getCalendarSlide(Date date) {
        return new IFrameSlide(getCalendarUrl(date), "SAD Calendar");
    }
    
    public static Slide getDailyBeamAccountingSlide(Date date) {
        return new IFrameSlide(getDailyBeamAccountingUrl(date), "Daily Beam Time Accounting");
    }
    
    public static Slide getDailyFsdSlide(Date date) {
        return new IFrameSlide(getDailyFsdUrl(date), "Daily FSD Summary");
    }    
    
    public static Slide getWeeklyBeamAccountingSlide(Date date) {
        return new IFrameSlide(getWeeklyBeamAccountingUrl(date), "Weekly Beam Time Accounting");
    }    
    
    public static Slide getWeeklyFsdSlide(Date date) {
        return new IFrameSlide(getWeeklyFsdUrl(date), "Weekly FSD Summary");
    }

    public static Slide getChargeSlide(Date date) {
        return new IFrameSlide(getChargeUrl(date), "Accumulated Charge Chart");
    }
    
    public static Slide getWeatherSlide() {
        return new IFrameSlide(getWeatherUrl(), "Newport News Weather");
    }
    
    public static Slide getPowerSlide(Date date) {
        return new IFrameSlide(getPowerUrl(date), "Power");
    }

    public static Slide getWhiteboardSlide() {
        return new IFrameSlide(getWhiteboardUrl(), "MCC Schedule");
    }
}

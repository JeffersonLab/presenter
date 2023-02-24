package org.jlab.presenter.business.util;

import java.util.Calendar;
import java.util.Date;
import org.jlab.presenter.persistence.enumeration.Shift;

/**
 *
 * @author ryans
 */
public class TimeUtil {

    public static Date getShiftDay(Date ymd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(ymd);
        
        if(cal.get(Calendar.HOUR_OF_DAY) == 23) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 0);
        }
        
        return cal.getTime();
    }

    private TimeUtil() {
        // Can't instantiate publicly
    }

    public static Date getCurrentYearMonthDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    public static Shift getCurrentShift() {
        Shift shift;

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 7 || hour >= 23) {
            shift = Shift.OWL;
        } else if (hour < 15) {
            shift = Shift.DAY;
        } else {
            shift = Shift.SWING;
        }

        return shift;
    }

    public static int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static Date getOneWeekLater(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);
        return c.getTime();
    }

    public static Date getOneWeekBefore(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -7);
        return c.getTime();
    }

    public static Date getTwoWeekBefore(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -14);
        return c.getTime();
    }

    public static Date addDays(Date currentYmd, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(currentYmd);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    public static Date getLSDContentDate(Date deliveryDate) {
        Date contentDate;
        if (getDayOfWeek(deliveryDate) == Calendar.MONDAY) {
            contentDate = addDays(deliveryDate, -3);
        } else {
            contentDate = addDays(deliveryDate, -1);
        }
        return contentDate;
    }
    
        public static Date nowWithoutMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}

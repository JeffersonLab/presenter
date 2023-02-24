package org.jlab.presenter.presentation.util;

import java.util.Calendar;
import java.util.Date;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.entity.Staff;

/**
 *
 * @author ryans
 */
public final class Functions {
    private Functions() {
        // Hide constructor
    }
    
    public static boolean isFriday(Date date) {
        return TimeUtil.getDayOfWeek(date) == Calendar.FRIDAY;
    }
    
    public static String formatStaff(Staff staff) {
        StringBuilder builder = new StringBuilder();

        if (staff != null) {
            builder.append(staff.getLastname());
            builder.append(", ");
            builder.append(staff.getFirstname());
            builder.append(" (");
            builder.append(staff.getUsername());
            builder.append(")");
        } else {
            builder.append("ANONYMOUS");
        }

        return builder.toString();
    }

    public static Date addDays(Date date, int days) {
        return TimeUtil.addDays(date, days);
    }
}

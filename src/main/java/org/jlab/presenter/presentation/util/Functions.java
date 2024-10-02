package org.jlab.presenter.presentation.util;

import java.util.Calendar;
import java.util.Date;
import org.jlab.presenter.business.util.TimeUtil;

/**
 * @author ryans
 */
public final class Functions {
  private Functions() {
    // Hide constructor
  }

  public static boolean isFriday(Date date) {
    return TimeUtil.getDayOfWeek(date) == Calendar.FRIDAY;
  }

  public static Date addDays(Date date, int days) {
    return TimeUtil.addDays(date, days);
  }
}

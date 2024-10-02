package org.jlab.presenter.presentation.util;

import java.util.Date;
import org.jlab.presenter.business.util.UrlUtil;
import org.jlab.presenter.persistence.entity.Slide;

public class DailyTimeAccountingGraphSlideGenerator implements DailySlideGenerator {

  @Override
  public Slide getSlideForDay(Date day) {
    return UrlUtil.getDailyBeamAccountingSlide(day);
  }
}

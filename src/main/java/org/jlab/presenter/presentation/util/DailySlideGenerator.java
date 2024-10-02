package org.jlab.presenter.presentation.util;

import java.util.Date;
import org.jlab.presenter.persistence.entity.Slide;

public interface DailySlideGenerator {
  Slide getSlideForDay(Date day);
}

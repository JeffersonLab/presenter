package org.jlab.presenter.presentation.util;

import org.jlab.presenter.persistence.entity.BodySlide;
import org.jlab.presenter.persistence.entity.PdInfoSlide;
import org.jlab.presenter.persistence.entity.ShiftInfoSlide;
import org.jlab.presenter.persistence.entity.Slide;
import org.jlab.presenter.persistence.entity.TitleBodySlide;
import org.jlab.presenter.persistence.enumeration.BodySlideType;
import org.jlab.presenter.persistence.enumeration.PdInfoSlideType;
import org.jlab.presenter.persistence.enumeration.ShiftSlideType;
import org.jlab.presenter.persistence.enumeration.TitleBodySlideType;

/**
 * @author ryans
 */
public class TemplateFinder {

  private static final String TEMPLATE_PATH = "/WEB-INF/slide-templates/";

  public String getPath(Slide slide) {
    String fileName;

    switch (slide.getSlideType()) {
      case TITLE_BODY_SLIDE:
        TitleBodySlideType titleBodySlideType = ((TitleBodySlide) slide).getTitleBodySlideType();
        switch (titleBodySlideType) {
          case DYNAMIC_TWO_COLUMN:
            fileName = "title-dynamic-two-columns.jsp";
            break;
          case SINGLE_COLUMN:
            fileName = "title-text.jsp";
            break;
          default:
            throw new IllegalArgumentException(
                "TitleBodySlideType not recognized: " + titleBodySlideType);
        }
        break;
      case BODY_SLIDE:
        BodySlideType bodySlideType = ((BodySlide) slide).getBodySlideType();
        switch (bodySlideType) {
          case PD_SUMMARY_OVERFLOW:
            fileName = "pd-summary-overflow.jsp";
            break;
          case SHIFT_OVERFLOW:
            fileName = "cebaf-shift-log-overflow.jsp";
            break;
          case PD_SUMMARY_PART_FOUR:
            fileName = "sum2-4.jsp";
            break;
          default:
            throw new IllegalArgumentException("BodySlideType not recognized: " + bodySlideType);
        }
        break;
      case IMAGE_SLIDE:
        fileName = "image.jsp";
        break;
      case TITLE_IMAGE_SLIDE:
        fileName = "title-image.jsp";
        break;
      case TITLE_BODY_IMAGE_SLIDE:
        fileName = "title-text-image.jsp";
        break;
      case IFRAME_SLIDE:
        fileName = "iframe.jsp";
        break;
      case SHIFT_INFO_SLIDE:
        ShiftSlideType shiftSlideType = ((ShiftInfoSlide) slide).getShiftSlideType();
        switch (shiftSlideType) {
          case LASO:
            fileName = "laso-shift-log.jsp";
            break;
          case LO:
            fileName = "lo-shift-log.jsp";
            break;
          case UITF:
            fileName = "uitf-shift-log.jsp";
            break;
          case HCO:
          case PD:
          case LSD:
            fileName = "pd-shift-summary.jsp";
            break;
          default:
            throw new IllegalArgumentException("ShiftSlideType not recognized: " + shiftSlideType);
        }
        break;
      case BTA_SHIFT_INFO_SLIDE:
        fileName = "cebaf-shift-log.jsp";
        break;
      case PD_INFO_SLIDE:
        PdInfoSlideType pdInfoSlideType = ((PdInfoSlide) slide).getPdInfoSlideType();
        switch (pdInfoSlideType) {
          case FIRST_SUMMARY:
            fileName = "sum1.jsp";
            break;
          case SECOND_SUMMARY:
            fileName = "sum2-1.jsp";
            break;
          default:
            throw new IllegalArgumentException(
                "PdInfoSlideType not recognized: " + pdInfoSlideType);
        }
        break;
      case PD_BEAM_ACC_SLIDE:
        fileName = "sum2-2.jsp";
        break;
      case PD_ACCESS_SLIDE:
        fileName = "sum2-3.jsp";
        break;
      default:
        throw new IllegalArgumentException("SlideType not recognized: " + slide.getSlideType());
    }

    return TEMPLATE_PATH + fileName;
  }
}

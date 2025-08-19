package org.jlab.presenter.presentation.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.business.util.UrlUtil;
import org.jlab.presenter.persistence.entity.*;
import org.jlab.presenter.persistence.enumeration.BodySlideType;
import org.jlab.presenter.persistence.enumeration.PDPresentationType;
import org.jlab.presenter.persistence.enumeration.PdInfoSlideType;
import org.jlab.presenter.persistence.enumeration.Shift;
import org.jlab.presenter.persistence.enumeration.ShiftSlideType;
import org.jlab.presenter.persistence.enumeration.TitleBodySlideType;

/**
 * @author ryans
 */
public abstract class ShowInfo {

  protected static final Logger logger = Logger.getLogger(ShowInfo.class.getName());

  public static String[] getCcTags() {
    return new String[] {"ShiftSummary"};
  }

  public static String[] getPdTags(PDPresentationType type) {
    String[] tags = null;

    switch (type) {
      case SUM1:
      case SUM2:
      case SUM3:
        tags = new String[] {"PDWeeklySummary"};
        break;
      case SAM:
      case RUN:
      case LSD:
      case HCO:
      case PD:
        tags = new String[] {"MCC8am"};
    }

    return tags;
  }

  public static String[] getLasoTags() {
    return new String[] {"ShiftSummary"};
  }

  public static String[] getLoTags() {
    return new String[] {"ShiftSummary"};
  }

  public static String[] getUitfTags() {
    return new String[] {"ShiftSummary"};
  }

  protected List<Slide> generalTemplates =
      Arrays.asList(
          new TitleBodySlide(),
          new TitleBodySlide(TitleBodySlideType.DYNAMIC_TWO_COLUMN),
          new ImageSlide(),
          new TitleImageSlide(),
          new TitleBodyImageSlide(),
          new IFrameSlide());
  protected boolean editable;
  protected String logbookHostname;

  public static String getLoLogbooks() {
    return "ELOG, LERFLOG";
  }

  public static String getCcLogbooks() {
    return "ELOG";
  }

  public static String getLasoLogbooks() {
    return "ELOG";
  }

  public static String getPdLogbooks() {
    return "ELOG";
  }

  public static String getUitfLogbooks() {
    return "ELOG, UITFLOG";
  }

  public String getLogbookHostname() {
    return logbookHostname;
  }

  public static ShowInfo getShowInfo(
      Presentation presentation, boolean editable, String logbookHostname) {
    ShowInfo info = null;

    switch (presentation.getPresentationType()) {
      case PD_PRESENTATION:
        info = new PDShowInfo((PDPresentation) presentation);
        break;
      case CC_PRESENTATION:
        info = new CCShowInfo((CCPresentation) presentation);
        break;
      case LASO_PRESENTATION:
        info = new LASOShowInfo((LASOPresentation) presentation);
        break;
      case LO_PRESENTATION:
        info = new LOShowInfo((LOPresentation) presentation);
        break;
      case UITF_PRESENTATION:
        info = new UITFShowInfo((UITFPresentation) presentation);
        break;
      default:
        throw new RuntimeException(
            "Presentation type not recognized: " + presentation.getPresentationType());
    }

    info.editable = editable;
    info.logbookHostname = logbookHostname;

    return info;
  }

  public abstract String getName();

  public abstract List<Slide> getTemplateList();

  public abstract String getMenuUrl();

  public abstract boolean isShiftImport();

  public abstract boolean isShiftImportIncludeGraphs();

  public abstract boolean isSummaryImport();

  public Integer getShiftLogDays() {
    return null;
  }

  public boolean isEditable() {
    return editable;
  }

  public static String getPdShowName(PDPresentationType type, Date deliveryYmd) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String name;

    switch (type) {
      case SUM1:
      case SUM2:
      case SUM3:
        name = "Program Deputy Weekly Summary, presented " + formatter.format(deliveryYmd);
        break;
      default:
        name = "Program Deputy Summary, presented " + formatter.format(deliveryYmd);
    }

    return name;
  }

  public static String getCcShowName(Date ymd, Shift shift) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return "Crew Chief Shift Log, " + formatter.format(ymd) + " " + shift;
  }

  public static String getLasoShowName(Date ymd, Shift shift) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return "LASO Shift Log, " + formatter.format(ymd) + " " + shift;
  }

  public static String getLoShowName(Date ymd, Shift shift) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return "LERF Operator Shift Log, " + formatter.format(ymd) + " " + shift;
  }

  public static String getUitfShowName(Date ymd, Shift shift) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return "UITF Shift Log, " + formatter.format(ymd) + " " + shift;
  }
}

class PDShowInfo extends ShowInfo {

  private final String name;
  private final boolean allowShiftImport;
  private final boolean includeGraphsInShiftImport;
  private final boolean allowSummaryImport;
  List<Slide> templates;
  private final Integer shiftLogDays;

  public PDShowInfo(PDPresentation presentation) {
    name = getPdShowName(presentation.getPdPresentationType(), presentation.getDeliveryYmd());

    /* Determine allowSummaryImport */
    allowSummaryImport = presentation.getPdPresentationType() == PDPresentationType.SUM3;

    /* Determine allowShiftImport */
    switch (presentation.getPdPresentationType()) {
      case RUN:
        allowShiftImport = true;
        includeGraphsInShiftImport = true;
        break;
      case SAM:
      case PD:
      case HCO:
        allowShiftImport = true;
        includeGraphsInShiftImport = false;
        break;
      default:
        allowShiftImport = false;
        includeGraphsInShiftImport = false;
    }

    Date dayBeforeDelivery = TimeUtil.addDays(presentation.getDeliveryYmd(), -1);

    templates = new ArrayList<>();
    /* Determine templates */
    switch (presentation.getPdPresentationType()) {
      case RUN:
        templates.add(
            new ShiftInfoSlide(
                TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()),
                Shift.DAY,
                ShiftSlideType.PD));
        templates.add(UrlUtil.getDailyBeamAccountingSlide(presentation.getDeliveryYmd()));
        /*try {
        templates.add(ExternalImageUtil.getDailyGraph());
        } catch (IOException e) {
        logger.log(Level.WARNING, "Unable to load daily graph");
        }*/
        templates.add(UrlUtil.getDailyFsdSlide(presentation.getDeliveryYmd()));
        /*try {
        templates.add(ExternalImageUtil.getFsdGraph());
        } catch (IOException e) {
        logger.log(Level.WARNING, "Unable to load fsd graph");
        }*/
        /*try {
        templates.add(ExternalImageUtil.getChargeGraph());
        } catch (IOException e) {
        logger.log(Level.WARNING, "Unable to load charge graph");
        }*/

        templates.add(UrlUtil.getWeatherSlide());
        templates.add(UrlUtil.getWhiteboardSlide());
        templates.add(UrlUtil.getPowerSlide(dayBeforeDelivery));

        templates.addAll(generalTemplates);
        break;
      case SAM:
        templates.add(
            new ShiftInfoSlide(
                TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()),
                Shift.DAY,
                ShiftSlideType.PD));
        templates.add(UrlUtil.getWeatherSlide());
        templates.add(UrlUtil.getWorkmapSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getCalendarSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getDailyFsdSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getWhiteboardSlide());
        templates.add(UrlUtil.getPowerSlide(dayBeforeDelivery));
        templates.addAll(generalTemplates);
        break;
      case LSD:
        templates.add(
            new ShiftInfoSlide(
                TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()), Shift.DAY));
        templates.add(UrlUtil.getWorkmapSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getCalendarSlide(presentation.getDeliveryYmd()));
        templates.addAll(generalTemplates);
        break;
      case HCO:
        templates.add(
            new ShiftInfoSlide(
                TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()),
                Shift.DAY,
                ShiftSlideType.HCO));
        templates.add(UrlUtil.getWorkmapSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getCalendarSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getDailyFsdSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getHcoSlide());
        templates.add(UrlUtil.getWhiteboardSlide());
        templates.add(UrlUtil.getPowerSlide(dayBeforeDelivery));
        templates.addAll(generalTemplates);
        break;
      case PD:
        templates.add(
            new ShiftInfoSlide(
                TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()),
                Shift.DAY,
                ShiftSlideType.PD));
        templates.add(UrlUtil.getWorkmapSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getCalendarSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getHcoSlide());
        templates.addAll(generalTemplates);
        templates.add(UrlUtil.getWhiteboardSlide());
        break;
      case SUM1:
        templates.add(new BodySlide(BodySlideType.PD_SUMMARY_OVERFLOW));
        templates.add(
            new PdInfoSlide(
                PdInfoSlideType.FIRST_SUMMARY,
                presentation.getDeliveryYmd(),
                TimeUtil.getOneWeekLater(presentation.getDeliveryYmd())));
        templates.add(UrlUtil.getWeeklyBeamAccountingSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getWeeklyFsdSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getChargeSlide(presentation.getDeliveryYmd()));
        templates.addAll(generalTemplates);
        templates.add(UrlUtil.getWhiteboardSlide());
        break;
      case SUM2:
        templates.add(new BodySlide(BodySlideType.PD_SUMMARY_OVERFLOW));
        templates.add(
            new PdInfoSlide(
                PdInfoSlideType.SECOND_SUMMARY,
                TimeUtil.getOneWeekBefore(presentation.getDeliveryYmd()),
                presentation.getDeliveryYmd()));
        templates.add(new PdBeamAccSlide());
        templates.add(new PdAccessSlide());
        templates.add(new BodySlide(BodySlideType.PD_SUMMARY_PART_FOUR));
        templates.add(UrlUtil.getWeeklyBeamAccountingSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getWeeklyFsdSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getChargeSlide(presentation.getDeliveryYmd()));
        templates.addAll(generalTemplates);
        templates.add(UrlUtil.getWhiteboardSlide());
        break;
      case SUM3:
        templates.add(new BodySlide(BodySlideType.PD_SUMMARY_OVERFLOW));
        templates.add(
            new PdInfoSlide(
                PdInfoSlideType.SECOND_SUMMARY,
                TimeUtil.getOneWeekBefore(presentation.getDeliveryYmd()),
                presentation.getDeliveryYmd()));
        templates.add(new PdBeamAccSlide());
        templates.add(new PdAccessSlide());
        templates.add(UrlUtil.getWeeklyBeamAccountingSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getWeeklyFsdSlide(presentation.getDeliveryYmd()));
        templates.add(UrlUtil.getChargeSlide(presentation.getDeliveryYmd()));
        templates.addAll(generalTemplates);
        templates.add(UrlUtil.getWhiteboardSlide());
        break;
    }

    shiftLogDays = presentation.getShiftLogDays();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<Slide> getTemplateList() {
    return templates;
  }

  @Override
  public String getMenuUrl() {
    return "pd-menu";
  }

  @Override
  public boolean isShiftImport() {
    return allowShiftImport;
  }

  @Override
  public boolean isShiftImportIncludeGraphs() {
    return includeGraphsInShiftImport;
  }

  @Override
  public boolean isSummaryImport() {
    return allowSummaryImport;
  }

  @Override
  public Integer getShiftLogDays() {
    return shiftLogDays;
  }
}

class CCShowInfo extends ShowInfo {

  private final String name;
  List<Slide> templates;

  public CCShowInfo(CCPresentation presentation) {
    name = ShowInfo.getCcShowName(presentation.getYmd(), presentation.getShift());

    templates = new ArrayList<>();
    templates.add(new BodySlide());
    templates.add(new BtaShiftInfoSlide(presentation.getYmd(), presentation.getShift()));
    templates.addAll(generalTemplates);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<Slide> getTemplateList() {
    return templates;
  }

  @Override
  public String getMenuUrl() {
    return "cc-menu";
  }

  @Override
  public boolean isShiftImport() {
    return false;
  }

  @Override
  public boolean isShiftImportIncludeGraphs() {
    return false;
  }

  @Override
  public boolean isSummaryImport() {
    return false;
  }
}

class LASOShowInfo extends ShowInfo {

  private final String name;
  List<Slide> templates;

  public LASOShowInfo(LASOPresentation presentation) {
    name = ShowInfo.getLasoShowName(presentation.getYmd(), presentation.getShift());

    templates = new ArrayList<>();
    templates.add(
        new ShiftInfoSlide(presentation.getYmd(), presentation.getShift(), ShiftSlideType.LASO));
    templates.addAll(generalTemplates);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<Slide> getTemplateList() {
    return templates;
  }

  @Override
  public String getMenuUrl() {
    return "laso-menu";
  }

  @Override
  public boolean isShiftImport() {
    return false;
  }

  @Override
  public boolean isShiftImportIncludeGraphs() {
    return false;
  }

  @Override
  public boolean isSummaryImport() {
    return false;
  }
}

class LOShowInfo extends ShowInfo {

  private final String name;
  List<Slide> templates;

  public LOShowInfo(LOPresentation presentation) {
    name = ShowInfo.getLoShowName(presentation.getYmd(), presentation.getShift());

    templates = new ArrayList<>();
    templates.add(
        new ShiftInfoSlide(presentation.getYmd(), presentation.getShift(), ShiftSlideType.LO));
    templates.addAll(generalTemplates);

    /*for(Slide s: templates) {
        s.setSyncFromPresentationType(PresentationType.LO_PRESENTATION);
    }*/
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<Slide> getTemplateList() {
    return templates;
  }

  @Override
  public String getMenuUrl() {
    return "lo-menu";
  }

  @Override
  public boolean isShiftImport() {
    return false;
  }

  @Override
  public boolean isShiftImportIncludeGraphs() {
    return false;
  }

  @Override
  public boolean isSummaryImport() {
    return false;
  }
}

class UITFShowInfo extends ShowInfo {

  private final String name;
  List<Slide> templates;

  public UITFShowInfo(UITFPresentation presentation) {
    name = ShowInfo.getLoShowName(presentation.getYmd(), presentation.getShift());

    templates = new ArrayList<>();
    templates.add(
        new ShiftInfoSlide(presentation.getYmd(), presentation.getShift(), ShiftSlideType.UITF));
    templates.addAll(generalTemplates);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<Slide> getTemplateList() {
    return templates;
  }

  @Override
  public String getMenuUrl() {
    return "uitf-menu";
  }

  @Override
  public boolean isShiftImport() {
    return false;
  }

  @Override
  public boolean isShiftImportIncludeGraphs() {
    return false;
  }

  @Override
  public boolean isSummaryImport() {
    return false;
  }
}

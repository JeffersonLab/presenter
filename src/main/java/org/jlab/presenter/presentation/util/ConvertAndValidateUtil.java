package org.jlab.presenter.presentation.util;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.jlab.presenter.persistence.entity.AccActivityRecord;
import org.jlab.presenter.persistence.entity.AccessRecord;
import org.jlab.presenter.persistence.entity.BeamToHallRecord;
import org.jlab.presenter.persistence.entity.BtaRecord;
import org.jlab.presenter.persistence.entity.BtaShiftInfoSlide;
import org.jlab.presenter.persistence.entity.PdAccessSlide;
import org.jlab.presenter.persistence.entity.PdBeamAccSlide;
import org.jlab.presenter.persistence.enumeration.BodySlideType;
import org.jlab.presenter.persistence.enumeration.PDPresentationType;
import org.jlab.presenter.persistence.enumeration.Shift;
import org.jlab.presenter.persistence.enumeration.ShiftSlideType;
import org.jlab.presenter.persistence.enumeration.SlideType;
import org.jlab.presenter.persistence.enumeration.TitleBodySlideType;

/**
 * @author ryans
 */
public class ConvertAndValidateUtil {

  private ConvertAndValidateUtil() {
    // Can't instantiate publicly
  }

  public static Boolean convertYNBoolean(HttpServletRequest request, String name)
      throws IllegalArgumentException {
    String valueStr = request.getParameter(name);
    Boolean value = null;

    if (valueStr != null && !valueStr.isEmpty()) {
      if ("N".equals(valueStr)) {
        value = false;
      } else if ("Y".equals(valueStr)) {
        value = true;
      } else {
        throw new IllegalArgumentException("Value must be one of 'Y' or 'N'");
      }
    }

    return value;
  }

  public static BigInteger convertAndValidateBigIntegerParam(
      HttpServletRequest request, String name) {
    BigInteger value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = new BigInteger(valueStr);
    }

    return value;
  }

  public static Long convertAndValidateLongParam(HttpServletRequest request, String name)
      throws NumberFormatException {
    Long value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = Long.parseLong(valueStr);
    }

    return value;
  }

  public static Date convertAndValidateYMDParam(HttpServletRequest request, String name)
      throws ParseException {
    Date value = null;

    String valueStr = request.getParameter(name);

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    if (valueStr != null && !valueStr.isEmpty()) {
      value = formatter.parse(valueStr);
    }

    return value;
  }

  public static Date convertAndValidateShiftInfoDateParam(HttpServletRequest request, String name)
      throws ParseException {
    Date value = null;

    String valueStr = request.getParameter(name);

    SimpleDateFormat formatter = new SimpleDateFormat("EEEE MMMM d, yyyy");

    if (valueStr != null && !valueStr.isEmpty()) {
      value = formatter.parse(valueStr);
    }

    return value;
  }

  public static ShiftSlideType convertAndValidatePdShiftSlideTypeParam(
      HttpServletRequest request, String name) {
    ShiftSlideType value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = ShiftSlideType.valueOf(valueStr);
    }

    return value;
  }

  public static BodySlideType convertAndValidateBodySlideTypeParam(
      HttpServletRequest request, String name) {
    BodySlideType value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = BodySlideType.valueOf(valueStr);
    }

    return value;
  }

  public static TitleBodySlideType convertAndValidateTitleBodySlideTypeParam(
      HttpServletRequest request, String name) {
    TitleBodySlideType value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = TitleBodySlideType.valueOf(valueStr);
    }

    return value;
  }

  public static Shift convertAndValidateShift(HttpServletRequest request, String name)
      throws IllegalArgumentException {
    Shift value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = Shift.valueOf(valueStr.toUpperCase());
    }

    return value;
  }

  public static SlideType convertAndValidateSlideType(HttpServletRequest request, String name) {
    SlideType value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = SlideType.valueOf(valueStr);
    }

    return value;
  }

  public static PDPresentationType convertAndValidatePDType(
      HttpServletRequest request, String name) {
    PDPresentationType value = null;

    String valueStr = request.getParameter(name);

    if (valueStr != null && !valueStr.isEmpty()) {
      value = PDPresentationType.valueOf(valueStr);
    }

    return value;
  }

  public static List<BtaRecord> convertAndValidateBtaRecordParams(
      HttpServletRequest request, BtaShiftInfoSlide slide) {
    List<BtaRecord> records = new ArrayList<BtaRecord>();

    final int MAX_ROWS = 9;

    String[] row;
    long i = 1;
    while ((row = request.getParameterValues("bta[" + i + "][]")) != null) {

      if (i > MAX_ROWS) {
        throw new IllegalArgumentException(
            "You may not save more than " + MAX_ROWS + " rows of BTA.");
      }

      if (row.length != 5) {
        throw new IllegalArgumentException(
            "Each BTA record must contain five fields; found: " + row.length);
      }

      BtaRecord record = new BtaRecord();
      record.setHallProgram(row[0]);
      record.setScheduled(ConvertAndValidateUtil.parseNullableFloat(row[1]));
      record.setActual(ConvertAndValidateUtil.parseNullableFloat(row[2]));
      record.setAbu(ConvertAndValidateUtil.parseNullableFloat(row[3]));
      record.setBanu(ConvertAndValidateUtil.parseNullableFloat(row[4]));

      record.setOrderId(i);
      record.setSlideId(slide);

      records.add(record);

      /*for(String td: row) {
          System.out.print(td + "\t\t");
      }
      System.out.println();*/

      i++;
    }

    return records;
  }

  public static List<BeamToHallRecord> convertAndValidateBeamToHallRecordParams(
      HttpServletRequest request, PdBeamAccSlide slide) {
    List<BeamToHallRecord> records = new ArrayList<BeamToHallRecord>();

    final int MAX_ROWS = 6;

    String[] row;
    long i = 1;
    while ((row = request.getParameterValues("beam[" + i + "][]")) != null) {

      if (i > MAX_ROWS) {
        throw new IllegalArgumentException(
            "You may not save more than " + MAX_ROWS + " rows of Beam-To-Hall.");
      }

      if (row.length != 6) {
        throw new IllegalArgumentException(
            "Each Beam-To-Hall record must contain six fields; found: " + row.length);
      }

      BeamToHallRecord record = new BeamToHallRecord();
      record.setHall(row[0]);
      record.setScheduled(ConvertAndValidateUtil.parseNullableFloat(row[1]));
      record.setActual(ConvertAndValidateUtil.parseNullableFloat(row[2]));
      record.setAccAvail(ConvertAndValidateUtil.parseNullableFloat(row[3]));
      record.setAccept(ConvertAndValidateUtil.parseNullableFloat(row[4]));
      record.setHallAvail(ConvertAndValidateUtil.parseNullableFloat(row[5]));

      record.setOrderId(i);
      record.setSlideId(slide);

      records.add(record);

      /*for(String td: row) {
          System.out.print(td + "\t\t");
      }
      System.out.println();*/

      i++;
    }

    return records;
  }

  public static List<AccActivityRecord> convertAndValidateAccActivityRecordParams(
      HttpServletRequest request, PdBeamAccSlide slide) {
    List<AccActivityRecord> records = new ArrayList<AccActivityRecord>();

    final int MAX_ROWS = 6;

    String[] row;
    long i = 1;
    while ((row = request.getParameterValues("activity[" + i + "][]")) != null) {

      if (i > MAX_ROWS) {
        throw new IllegalArgumentException(
            "You may not save more than " + MAX_ROWS + " rows of Accelerator Activity.");
      }

      if (row.length != 3) {
        throw new IllegalArgumentException(
            "Each Accelerator Activity record must contain three fields; found: " + row.length);
      }

      AccActivityRecord record = new AccActivityRecord();
      record.setActivityType(row[0]);
      record.setScheduled(ConvertAndValidateUtil.parseNullableFloat(row[1]));
      record.setActual(ConvertAndValidateUtil.parseNullableFloat(row[2]));

      record.setOrderId(i);
      record.setSlideId(slide);

      records.add(record);

      /*for(String td: row) {
          System.out.print(td + "\t\t");
      }
      System.out.println();*/

      i++;
    }

    return records;
  }

  public static List<AccessRecord> convertAndValidateAccessRecordParams(
      HttpServletRequest request, PdAccessSlide slide) {
    List<AccessRecord> records = new ArrayList<AccessRecord>();

    final int MAX_ROWS = 6;

    String[] row;
    long i = 1;
    while ((row = request.getParameterValues("access[" + i + "][]")) != null) {

      if (i > MAX_ROWS) {
        throw new IllegalArgumentException(
            "You may not save more than " + MAX_ROWS + " rows of access records.");
      }

      if (row.length != 6) {
        throw new IllegalArgumentException(
            "Each access record must contain six fields; found: " + row.length);
      }

      AccessRecord record = new AccessRecord();
      record.setAccessType(row[0]);
      record.setHallA(ConvertAndValidateUtil.parseNullableFloat(row[1]));
      record.setHallB(ConvertAndValidateUtil.parseNullableFloat(row[2]));
      record.setHallC(ConvertAndValidateUtil.parseNullableFloat(row[3]));
      record.setHallD(ConvertAndValidateUtil.parseNullableFloat(row[4]));
      record.setAccel(ConvertAndValidateUtil.parseNullableFloat(row[5]));

      record.setOrderId(i);
      record.setSlideId(slide);

      records.add(record);

      /*for(String td: row) {
          System.out.print(td + "\t\t");
      }
      System.out.println();*/

      i++;
    }

    return records;
  }

  public static Float parseNullableFloat(String str) {
    Float number = null;

    if (str != null && !str.isEmpty()) {
      number = Float.parseFloat(str);
    }

    return number;
  }
}

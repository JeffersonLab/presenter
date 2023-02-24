package org.jlab.presenter.presentation.util;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.jlab.presenter.persistence.entity.AccActivityRecord;
import org.jlab.presenter.persistence.entity.AccessRecord;
import org.jlab.presenter.persistence.entity.BeamToHallRecord;
import org.jlab.presenter.persistence.entity.BodySlide;
import org.jlab.presenter.persistence.entity.BtaRecord;
import org.jlab.presenter.persistence.entity.BtaShiftInfoSlide;
import org.jlab.presenter.persistence.entity.IFrameSlide;
import org.jlab.presenter.persistence.entity.ImageSlide;
import org.jlab.presenter.persistence.entity.PdAccessSlide;
import org.jlab.presenter.persistence.entity.PdBeamAccSlide;
import org.jlab.presenter.persistence.entity.PdInfoSlide;
import org.jlab.presenter.persistence.entity.ShiftInfoSlide;
import org.jlab.presenter.persistence.entity.TitleBodyImageSlide;
import org.jlab.presenter.persistence.entity.TitleBodySlide;
import org.jlab.presenter.persistence.entity.TitleImageSlide;
import org.jlab.presenter.persistence.enumeration.BodySlideType;
import org.jlab.presenter.persistence.enumeration.ShiftSlideType;
import org.jlab.presenter.persistence.enumeration.Shift;
import org.jlab.presenter.persistence.enumeration.TitleBodySlideType;

/**
 *
 * @author ryans
 */
public class SlideUtil {

    public static void update(HttpServletRequest request, TitleBodySlide titleBodySlide) {
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        TitleBodySlideType titleBodySlideType = ConvertAndValidateUtil.convertAndValidateTitleBodySlideTypeParam(request, "slideSubType");

        if (titleBodySlideType == null) {
            throw new IllegalArgumentException("slideSubType cannot be null");
        }

        titleBodySlide.setTitle(title);
        titleBodySlide.setBody(body);
        titleBodySlide.setTitleBodySlideType(titleBodySlideType);
    }

    public static void update(HttpServletRequest request, BodySlide bodySlide) {
        String body = request.getParameter("body");
        BodySlideType bodySlideType = ConvertAndValidateUtil.convertAndValidateBodySlideTypeParam(request, "slideSubType");

        if (bodySlideType == null) {
            throw new IllegalArgumentException("slideSubType cannot be null");
        }

        bodySlide.setBody(body);
        bodySlide.setBodySlideType(bodySlideType);
    }

    public static void update(HttpServletRequest request, ImageSlide imageSlide) {
        String image = request.getParameter("image");

        imageSlide.setImageUrl(image);
    }

    public static void update(HttpServletRequest request, TitleImageSlide titleImageSlide) {
        String title = request.getParameter("title");
        String image = request.getParameter("image");

        titleImageSlide.setTitle(title);
        titleImageSlide.setImageUrl(image);
    }

    public static void update(HttpServletRequest request, TitleBodyImageSlide titleBodyImageSlide) {
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String image = request.getParameter("image");

        titleBodyImageSlide.setTitle(title);
        titleBodyImageSlide.setBody(body);
        titleBodyImageSlide.setImageUrl(image);
    }

    public static void update(HttpServletRequest request, IFrameSlide iframeSlide) {
        String iframeUrl = request.getParameter("iframe");
        
        if(iframeUrl == null || iframeUrl.isEmpty()) {
            throw new IllegalArgumentException("iframe cannot be null");
        }
        
        iframeSlide.setIframeUrl(iframeUrl);
    }
    
    public static void update(HttpServletRequest request, ShiftInfoSlide shiftInfoSlide) throws ParseException {
        ShiftSlideType shiftSlideType = ConvertAndValidateUtil.convertAndValidatePdShiftSlideTypeParam(request, "slideSubType");

        if (shiftSlideType == null) {
            throw new IllegalArgumentException("slideSubType cannot be null");
        }        
        
        String body = request.getParameter("body");
        Date ymd = ConvertAndValidateUtil.convertAndValidateShiftInfoDateParam(request, "date");
        Shift shift = ConvertAndValidateUtil.convertAndValidateShift(request, "shift");
        String team = request.getParameter("team");
        String program = request.getParameter("program");
        
        if(team != null && team.length() > 48) {
            throw new IllegalArgumentException("Team cannot be longer than 48 characters");
        }
        
        if(program != null && program.length() > 48) {
            throw new IllegalArgumentException("Program cannot be longer than 48 characters");
        }        

        shiftInfoSlide.setShiftSlideType(shiftSlideType);
        shiftInfoSlide.setBody(body);
        shiftInfoSlide.setYmd(ymd);
        shiftInfoSlide.setShift(shift);
        shiftInfoSlide.setTeam(team);
        shiftInfoSlide.setProgram(program);
    }

    public static void update(HttpServletRequest request, BtaShiftInfoSlide btaShiftInfoSlide) throws ParseException {
        SlideUtil.update(request, (ShiftInfoSlide) btaShiftInfoSlide);
        List<BtaRecord> btaRecords = ConvertAndValidateUtil.convertAndValidateBtaRecordParams(request, btaShiftInfoSlide);

        btaShiftInfoSlide.setBtaRecordList(btaRecords);
    }
    
    public static void update(HttpServletRequest request, PdInfoSlide pdInfoSlide) throws ParseException {
        String body = request.getParameter("body");
        String pd = request.getParameter("team");
        Date from = ConvertAndValidateUtil.convertAndValidateShiftInfoDateParam(request, "from");
        Date to = ConvertAndValidateUtil.convertAndValidateShiftInfoDateParam(request, "to");
        
        pdInfoSlide.setFromDate(from);
        pdInfoSlide.setToDate(to);
        pdInfoSlide.setBody(body);
        pdInfoSlide.setPd(pd);
    }    
    
    public static void update(HttpServletRequest request, PdBeamAccSlide pdBeamAccSlide) {
        List<BeamToHallRecord> beamRecords = ConvertAndValidateUtil.convertAndValidateBeamToHallRecordParams(request, pdBeamAccSlide);
        List<AccActivityRecord> accRecords = ConvertAndValidateUtil.convertAndValidateAccActivityRecordParams(request, pdBeamAccSlide);        
        
        pdBeamAccSlide.setBeamToHallRecordList(beamRecords);
        pdBeamAccSlide.setAccActivityRecordList(accRecords);
    }
    
    public static void update(HttpServletRequest request, PdAccessSlide pdAccessSlide) {
        String body = request.getParameter("body");
        List<AccessRecord> accessRecords = ConvertAndValidateUtil.convertAndValidateAccessRecordParams(request, pdAccessSlide);        
        
        pdAccessSlide.setBody(body);
        pdAccessSlide.setAccessRecordList(accessRecords);
    }    
}

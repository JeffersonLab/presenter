package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jlab.presenter.business.session.*;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.business.util.UrlUtil;
import org.jlab.presenter.persistence.entity.BodySlide;
import org.jlab.presenter.persistence.entity.IFrameSlide;
import org.jlab.presenter.persistence.entity.PDPresentation;
import org.jlab.presenter.persistence.entity.PdAccessSlide;
import org.jlab.presenter.persistence.entity.PdBeamAccSlide;
import org.jlab.presenter.persistence.entity.PdInfoSlide;
import org.jlab.presenter.persistence.entity.ShiftInfoSlide;
import org.jlab.presenter.persistence.entity.Slide;
import org.jlab.presenter.persistence.enumeration.BodySlideType;
import org.jlab.presenter.persistence.enumeration.PDPresentationType;
import org.jlab.presenter.persistence.enumeration.PdInfoSlideType;
import org.jlab.presenter.persistence.enumeration.PresentationType;
import org.jlab.presenter.persistence.enumeration.ShiftSlideType;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;
import org.jlab.presenter.presentation.util.DailyFsdGraphSlideGenerator;
import org.jlab.presenter.presentation.util.DailySlideGenerator;
import org.jlab.presenter.presentation.util.DailyTimeAccountingGraphSlideGenerator;
import org.jlab.presenter.presentation.util.PDPresentationUtil;
import org.jlab.presenter.presentation.util.PresentationMenuUtil;
import org.jlab.presenter.presentation.util.ShowInfo;

/**
 *
 * @author ryans
 */
@WebServlet(name = "PDMenu", urlPatterns = {"/pd-menu"})
public class PDMenu extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            PDMenu.class.getName());
    @EJB
    PresentationFacade presentationFacade;
    @EJB
    PDPresentationFacade pdPresentationFacade;
    @EJB
    CCPresentationFacade ccPresentationFacade;
    @EJB
    LASOPresentationFacade lasoPresentationFacade;
    @EJB
    LOPresentationFacade loPresentationFacade;
    @EJB
    UITFPresentationFacade uitfPresentationFacade;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        List<PDPresentationType> typeList = new ArrayList<>(Arrays.asList(
                PDPresentationType.values()));

        typeList.remove(PDPresentationType.LSD); // Mask
        typeList.remove(PDPresentationType.PD); // Mask

        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        HttpSession session = request.getSession(true);
        PDPresentationType pdType = (PDPresentationType) session.getAttribute("pdType");
        Date pdDate = (Date) session.getAttribute("pdDate");

        /*if (pdType == null) { // Don't have default anymore so we force PD to make a choice
         pdType = PDPresentationType.RUN;
         }*/
        if (pdDate == null) {
            pdDate = TimeUtil.getCurrentYearMonthDay();
        }

        request.setAttribute("typeList", typeList);
        request.setAttribute("defaultType", pdType);
        request.setAttribute("defaultDate", pdDate);

        List<PDPresentation> recentList = pdPresentationFacade.findRecent();

        request.setAttribute("recentList", recentList);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/pd-menu.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Date ymd = null;
        PDPresentationType type = null;

        Map<String, String> messages = new HashMap<>();
        request.setAttribute("messages", messages);

        try {
            ymd = ConvertAndValidateUtil.convertAndValidateYMDParam(request,
                    "date");

            if (ymd == null) {
                messages.put("date", "Date must not be empty");
            }
        } catch (ParseException e) {
            messages.put("date", "Invalid date format");
        }

        try {
            type = ConvertAndValidateUtil.convertAndValidatePDType(request,
                    "type");

            if (type == null) {
                messages.put("type", "Type must not be empty");
            }
        } catch (IllegalArgumentException e) {
            messages.put("type", "Invalid type");
        }

        if (type != null && ymd != null) {
            // Verify valid delivery date for type
            int dayOfWeek = TimeUtil.getDayOfWeek(ymd);
            switch (type) {
                case RUN:
                case SAD:
                case LSD:
                case HCO:
                case PD:
                    if (dayOfWeek == Calendar.SATURDAY
                            || dayOfWeek == Calendar.SUNDAY) {
                        messages.put("date",
                                "Selected type can only be delivered Monday-Friday");
                    }
                    break;
                case SUM1:
                case SUM2:
                case SUM3:
                    if (dayOfWeek != Calendar.WEDNESDAY) {
                        messages.put("date",
                                "Selected type can only be delivered Wednesday");
                    }
                    break;
            }
        }
        
        PDPresentation presentation = null;
        BigInteger presentationId = null;

        if (messages.isEmpty()) {
            presentation = pdPresentationFacade.findByYmdAndPDType(ymd,
                    type);

            if (presentation != null) {
                presentationId = presentation.getPresentationId();
            }

            if (presentationId == null && !presentationFacade.isAuthorized(
                    PresentationType.PD_PRESENTATION)) {
                messages.put("error",
                        "No presentation found for specified criteria.  Login as PD to create.");
            }
        }
        
        if (!messages.isEmpty()) {
            List<PDPresentationType> typeList = new ArrayList<>(Arrays.asList(
                    PDPresentationType.values()));

            typeList.remove(PDPresentationType.LSD); // Mask
            typeList.remove(PDPresentationType.PD); // Mask

            request.setAttribute("typeList", typeList);

            List<PDPresentation> recentList = pdPresentationFacade.findRecent();

            request.setAttribute("recentList", recentList);

            getServletConfig().getServletContext().getRequestDispatcher(
                    "/WEB-INF/views/pd-menu.jsp").forward(request, response);/*This is bad.  Should not forward from POST.   This POST should be done via AJAX*/

        } else {

            /* Remember user selection */
 /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
            HttpSession session = request.getSession(true);
            session.setAttribute("pdType", type);
            session.setAttribute("pdDate", ymd);

            if (presentationId == null) {

                presentation = new PDPresentation();
                presentation.setDeliveryYmd(ymd);
                presentation.setPdPresentationType(type);
                presentation.setShiftLogDays(pdPresentationFacade.getDefaultPrecedingDays(ymd));

                List<Slide> slides = null;

                switch (type) {
                    case RUN:
                        slides = getNewRunSlides(request, response, presentation);
                        break;
                    case SAD:
                        slides = getNewSADSlides(request, response, presentation);
                        break;
                    case LSD:
                        slides = getNewLSDSlides(request, response, presentation);
                        break;
                    case HCO:
                        slides = getNewHCOSlides(request, response, presentation);
                        break;
                    case PD:
                        slides = getNewPDSlides(request, response, presentation);
                        break;
                    case SUM1:
                        slides = getNewSUM1Slides(request, response, presentation);
                        break;
                    case SUM2:
                        slides = getNewSUM2Slides(request, response, presentation);
                        break;
                    case SUM3:
                        slides = getNewSUM3Slides(request, response, presentation);
                        break;
                }

                presentation.setSlideList(slides);

                presentationFacade.create(presentation); // This method sets last modified by

                presentationId = presentation.getPresentationId();
            } else { // Refresh shift logs (If RUN, HCO, SAD, or SUM3)
                int precedingDayCount = pdPresentationFacade.getPrecedingDays(presentation);

                switch (type) {
                    case RUN:
                        pdPresentationFacade.importShiftLogs(presentationId, precedingDayCount, true);
                        break;
                    case HCO:
                    case SAD:
                        pdPresentationFacade.importShiftLogs(presentationId, precedingDayCount,
                                false);
                        break;
                    case SUM3:
                        pdPresentationFacade.importIncomingToOutgoing(presentationId);
                        break;
                }
            }

            if ("Delete".equals(request.getParameter("action"))) {
                pdPresentationFacade.delete(presentationId);
                PresentationMenuUtil.delete(request, response, presentationId);
            } else {
                PresentationMenuUtil.openOrExport(request, response, presentationId);
            }
        }
    }

    private List<Slide> getNewLSDSlides(HttpServletRequest request,
            HttpServletResponse response, PDPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        // Slide 1        
        ShiftInfoSlide lsdSlide = new ShiftInfoSlide();
        lsdSlide.setYmd(TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()));
        lsdSlide.setOrderId(1L);
        lsdSlide.setPresentation(presentation);

        slides.add(lsdSlide);

        // Slide 2
        IFrameSlide workmapSlide = UrlUtil.getWorkmapSlide(presentation.getDeliveryYmd());
        workmapSlide.setOrderId(2L);
        workmapSlide.setPresentation(presentation);

        slides.add(workmapSlide);

        // Slide 3
        IFrameSlide calendarSlide = UrlUtil.getCalendarSlide(presentation.getDeliveryYmd());
        calendarSlide.setOrderId(3L);
        calendarSlide.setPresentation(presentation);

        slides.add(calendarSlide);

        return slides;
    }

    private List<Slide> getNewHCOSlides(HttpServletRequest request,
            HttpServletResponse response, PDPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        final boolean copySlides = true;

        int precedingDayCount = presentation.getShiftLogDays();

        slides.addAll(PDPresentationUtil.getShiftLogsInterleaved(presentation,
                new ShiftPresentationFacade[]{ccPresentationFacade, lasoPresentationFacade}, 1,
                precedingDayCount, copySlides, null));

        long order = slides.size() + 1;

        // Summary Slide        
        ShiftInfoSlide summarySlide = new ShiftInfoSlide();
        summarySlide.setShiftSlideType(ShiftSlideType.HCO);
        summarySlide.setProgram("HCO");
        summarySlide.setYmd(TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()));
        summarySlide.setOrderId(order++);
        summarySlide.setPresentation(presentation);
        slides.add(summarySlide);

        // Workmap Slide
        IFrameSlide workmapSlide = UrlUtil.getWorkmapSlide(presentation.getDeliveryYmd());
        workmapSlide.setOrderId(order++);
        workmapSlide.setPresentation(presentation);
        slides.add(workmapSlide);

        // Calendar Slide
        IFrameSlide calendarSlide = UrlUtil.getCalendarSlide(presentation.getDeliveryYmd());
        calendarSlide.setOrderId(order++);
        calendarSlide.setPresentation(presentation);
        slides.add(calendarSlide);

        // HCO Graph Slide
        Slide hcoSlide = UrlUtil.getHcoSlide();
        hcoSlide.setOrderId(order++);
        hcoSlide.setPresentation(presentation);
        slides.add(hcoSlide);

        // LERF Slides
        List<Slide> lerfSlides = PDPresentationUtil.getLerfSlides(presentation, loPresentationFacade, order, precedingDayCount, copySlides);

        slides.addAll(lerfSlides);

        // UITF
        order = slides.size() + 1;

        List<Slide> uitfSlides = PDPresentationUtil.getUitfSlides(presentation, uitfPresentationFacade, order, precedingDayCount, copySlides);

        slides.addAll(uitfSlides);

        order = slides.size() + 1;

        Slide whiteboardSlide = UrlUtil.getWhiteboardSlide();
        whiteboardSlide.setOrderId(order++);
        whiteboardSlide.setPresentation(presentation);
        slides.add(whiteboardSlide);

        return slides;
    }

    private List<Slide> getNewPDSlides(HttpServletRequest request,
            HttpServletResponse response, PDPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        final boolean copySlides = true;

        // Crew Chief Shift Log Slides
        int precedingDayCount = presentation.getShiftLogDays();

        slides.addAll(PDPresentationUtil.getShiftLogsInterleaved(presentation,
                new ShiftPresentationFacade[]{ccPresentationFacade, lasoPresentationFacade}, 1,
                precedingDayCount, copySlides, null));

        long order = slides.size() + 1;

        // Summary Slide        
        ShiftInfoSlide summarySlide = new ShiftInfoSlide();
        summarySlide.setShiftSlideType(ShiftSlideType.PD);
        summarySlide.setProgram("");
        summarySlide.setYmd(TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()));
        summarySlide.setOrderId(order++);
        summarySlide.setPresentation(presentation);
        slides.add(summarySlide);

        // Workmap Slide
        IFrameSlide workmapSlide = UrlUtil.getWorkmapSlide(presentation.getDeliveryYmd());
        workmapSlide.setOrderId(order++);
        workmapSlide.setPresentation(presentation);
        slides.add(workmapSlide);

        // Calendar Slide
        IFrameSlide calendarSlide = UrlUtil.getCalendarSlide(presentation.getDeliveryYmd());
        calendarSlide.setOrderId(order++);
        calendarSlide.setPresentation(presentation);
        slides.add(calendarSlide);

        return slides;
    }

    private List<Slide> getNewRunSlides(HttpServletRequest request,
            HttpServletResponse response, PDPresentation presentation) {
        ArrayList<Slide> slides = new ArrayList<>();

        final boolean copySlides = true;

        int precedingDayCount = presentation.getShiftLogDays();

        DailySlideGenerator[] dailySlideGeneratorArray = new DailySlideGenerator[]{
            new DailyTimeAccountingGraphSlideGenerator(), new DailyFsdGraphSlideGenerator()};

        slides.addAll(PDPresentationUtil.getShiftLogsInterleaved(presentation,
                new ShiftPresentationFacade[]{ccPresentationFacade, lasoPresentationFacade}, 1,
                precedingDayCount, copySlides, dailySlideGeneratorArray));

        long order = slides.size() + 1;

        List<Slide> lerfSlides = PDPresentationUtil.getLerfSlides(presentation, loPresentationFacade, order, precedingDayCount, copySlides);

        slides.addAll(lerfSlides);

        // UITF
        order = slides.size() + 1;

        List<Slide> uitfSlides = PDPresentationUtil.getUitfSlides(presentation, uitfPresentationFacade, order, precedingDayCount, copySlides);

        slides.addAll(uitfSlides);

        order = slides.size() + 1;

        Slide whiteboardSlide = UrlUtil.getWhiteboardSlide();
        whiteboardSlide.setOrderId(order++);
        whiteboardSlide.setPresentation(presentation);
        slides.add(whiteboardSlide);

        return slides;
    }

    private List<Slide> getNewSADSlides(HttpServletRequest request,
            HttpServletResponse response, PDPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        boolean copySlides = true;

        int precedingDayCount = presentation.getShiftLogDays();

        slides.addAll(PDPresentationUtil.getShiftLogsInterleaved(presentation,
                new ShiftPresentationFacade[]{ccPresentationFacade, lasoPresentationFacade}, 1,
                precedingDayCount, copySlides, null));

        long order = slides.size() + 1;

        // Summary Slide        
        ShiftInfoSlide summarySlide = new ShiftInfoSlide();
        summarySlide.setShiftSlideType(ShiftSlideType.PD);
        summarySlide.setProgram("");
        summarySlide.setYmd(TimeUtil.getLSDContentDate(presentation.getDeliveryYmd()));
        summarySlide.setOrderId(order++);
        summarySlide.setPresentation(presentation);
        slides.add(summarySlide);

        // Workmap
        IFrameSlide workmapSlide = UrlUtil.getWorkmapSlide(presentation.getDeliveryYmd());
        workmapSlide.setOrderId(order++);
        workmapSlide.setPresentation(presentation);
        slides.add(workmapSlide);

        // Calendar
        IFrameSlide calendarSlide = UrlUtil.getCalendarSlide(presentation.getDeliveryYmd());
        calendarSlide.setOrderId(order++);
        calendarSlide.setPresentation(presentation);
        slides.add(calendarSlide);

        // LERF Slides
        List<Slide> lerfSlides = PDPresentationUtil.getLerfSlides(presentation, loPresentationFacade, order, precedingDayCount, copySlides);
        slides.addAll(lerfSlides);

        order = slides.size() + 1;

        // UITF
        List<Slide> uitfSlides = PDPresentationUtil.getUitfSlides(presentation, uitfPresentationFacade, order, precedingDayCount, copySlides);

        slides.addAll(uitfSlides);

        // Weather
        /*Slide weatherSlide = UrlUtil.getWeatherSlide();
        weatherSlide.setOrderId(order++);
        weatherSlide.setPresentation(presentation);
        slides.add(weatherSlide);*/

        // Power
        /*int previousDays = -1;
        if (TimeUtil.getDayOfWeek(presentation.getDeliveryYmd()) == Calendar.MONDAY) {
            previousDays = -3;
        }

        for (int i = previousDays; i < 0; i++) {
            Date dayBeforeDelivery = TimeUtil.addDays(presentation.getDeliveryYmd(), i);
            Slide powerSlide = UrlUtil.getPowerSlide(dayBeforeDelivery);
            powerSlide.setOrderId(order++);
            powerSlide.setPresentation(presentation);
            slides.add(powerSlide);
        }      */  

        return slides;
    }

    private List<Slide> getNewSUM1Slides(HttpServletRequest request,
            HttpServletResponse response, PDPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        // Slide 1        
        PdInfoSlide sum1Slide = new PdInfoSlide();
        sum1Slide.setFromDate(presentation.getDeliveryYmd());
        sum1Slide.setToDate(TimeUtil.getOneWeekLater(presentation.getDeliveryYmd()));
        sum1Slide.setOrderId(1L);
        sum1Slide.setPresentation(presentation);

        slides.add(sum1Slide);

        long order = slides.size() + 1;

        Slide whiteboardSlide = UrlUtil.getWhiteboardSlide();
        whiteboardSlide.setOrderId(order++);
        whiteboardSlide.setPresentation(presentation);
        slides.add(whiteboardSlide);

        return slides;
    }

    private List<Slide> getNewSUM2Slides(HttpServletRequest request,
            HttpServletResponse response, PDPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        // Slide 1        
        PdInfoSlide sum2P1Slide = new PdInfoSlide(PdInfoSlideType.SECOND_SUMMARY);
        sum2P1Slide.setFromDate(TimeUtil.getOneWeekBefore(presentation.getDeliveryYmd()));
        sum2P1Slide.setToDate(presentation.getDeliveryYmd());
        sum2P1Slide.setOrderId(1L);
        sum2P1Slide.setPresentation(presentation);

        // Check if goals were set last week and copy them over if found
        PdInfoSlide previousGoalSlide = pdPresentationFacade.findFirstGoalSlide(TimeUtil.getOneWeekBefore(presentation.getDeliveryYmd()));

        if(previousGoalSlide != null) {
            sum2P1Slide.setPd(previousGoalSlide.getPd());
            sum2P1Slide.setBody(previousGoalSlide.getBody().replaceAll("\\(upcoming week\\)", "(previous week)"));
        }

        slides.add(sum2P1Slide);

        // Slide 2        
        Slide sum2P2Slide = new PdBeamAccSlide();
        sum2P2Slide.setOrderId(2L);
        sum2P2Slide.setPresentation(presentation);

        slides.add(sum2P2Slide);

        // Slide 3        
        Slide sum2P3Slide = new PdAccessSlide();
        sum2P3Slide.setOrderId(3L);
        sum2P3Slide.setPresentation(presentation);

        slides.add(sum2P3Slide);

        // Slide 4
        Slide timeSlide = UrlUtil.getWeeklyBeamAccountingSlide(presentation.getDeliveryYmd());
        timeSlide.setPresentation(presentation);
        timeSlide.setOrderId(4L);
        slides.add(timeSlide);

        // Slide 5
        Slide fsdSlide = UrlUtil.getWeeklyFsdSlide(presentation.getDeliveryYmd());
        fsdSlide.setPresentation(presentation);
        fsdSlide.setOrderId(5L);
        slides.add(fsdSlide);

        // Slide 6
        Slide chargeSlide = UrlUtil.getChargeSlide(presentation.getDeliveryYmd());
        chargeSlide.setPresentation(presentation);
        chargeSlide.setOrderId(6L);
        slides.add(chargeSlide);

        long order = slides.size() + 1;

        // Slide 7
        Slide sum2P4Slide = new BodySlide(BodySlideType.PD_SUMMARY_PART_FOUR);
        sum2P4Slide.setOrderId(order);
        sum2P4Slide.setPresentation(presentation);

        slides.add(sum2P4Slide);

        order = slides.size() + 1;

        Slide whiteboardSlide = UrlUtil.getWhiteboardSlide();
        whiteboardSlide.setOrderId(order++);
        whiteboardSlide.setPresentation(presentation);
        slides.add(whiteboardSlide);

        return slides;
    }

    private List<Slide> getNewSUM3Slides(HttpServletRequest request,
            HttpServletResponse response, PDPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        // Slide 1        
        PdInfoSlide sum2P1Slide = new PdInfoSlide(PdInfoSlideType.SECOND_SUMMARY);
        sum2P1Slide.setFromDate(TimeUtil.getOneWeekBefore(presentation.getDeliveryYmd()));
        sum2P1Slide.setToDate(presentation.getDeliveryYmd());
        sum2P1Slide.setOrderId(1L);
        sum2P1Slide.setPresentation(presentation);

        // Check if goals were set last week and copy them over if found
        BodySlide previousGoalSlide = pdPresentationFacade.findSecondGoalSlide(TimeUtil.getOneWeekBefore(presentation.getDeliveryYmd()));

        if(previousGoalSlide != null) {
            sum2P1Slide.setBody(previousGoalSlide.getBody().replaceAll("\\(upcoming week\\)", "(previous week)"));
        }

        // Let's see if we can automatically fill in PD name using first goal slide from two weeks ago:
        PdInfoSlide info = pdPresentationFacade.findFirstGoalSlide(TimeUtil.getTwoWeekBefore(presentation.getDeliveryYmd()));

        if(info != null) {
            sum2P1Slide.setPd(info.getPd());
        }

        slides.add(sum2P1Slide);

        // Slide 2        
        Slide sum2P2Slide = new PdBeamAccSlide();
        sum2P2Slide.setOrderId(2L);
        sum2P2Slide.setPresentation(presentation);

        slides.add(sum2P2Slide);

        // Slide 3        
        Slide sum2P3Slide = new PdAccessSlide();
        sum2P3Slide.setOrderId(3L);
        sum2P3Slide.setPresentation(presentation);

        slides.add(sum2P3Slide);

        // Slide 4
        Slide timeSlide = UrlUtil.getWeeklyBeamAccountingSlide(presentation.getDeliveryYmd());
        timeSlide.setPresentation(presentation);
        timeSlide.setOrderId(4L);
        slides.add(timeSlide);

        // Slide 5
        Slide fsdSlide = UrlUtil.getWeeklyFsdSlide(presentation.getDeliveryYmd());
        fsdSlide.setPresentation(presentation);
        fsdSlide.setOrderId(5L);
        slides.add(fsdSlide);

        // Slide 6
        Slide chargeSlide = UrlUtil.getChargeSlide(presentation.getDeliveryYmd());
        chargeSlide.setPresentation(presentation);
        chargeSlide.setOrderId(6L);
        slides.add(chargeSlide);

        long order = slides.size() + 1;

        // Slide 7+ Incoming PD slides
        List<Slide> incomingSlides = pdPresentationFacade.getIncomingSlides(presentation, order,
                true);

        slides.addAll(incomingSlides);

        return slides;
    }
}

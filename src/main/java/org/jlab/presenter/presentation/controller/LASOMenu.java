package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jlab.presenter.business.session.LASOPresentationFacade;
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.entity.LASOPresentation;
import org.jlab.presenter.persistence.entity.ShiftInfoSlide;
import org.jlab.presenter.persistence.entity.Slide;
import org.jlab.presenter.persistence.enumeration.PresentationType;
import org.jlab.presenter.persistence.enumeration.Shift;
import org.jlab.presenter.persistence.enumeration.ShiftSlideType;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;
import org.jlab.presenter.presentation.util.PresentationMenuUtil;
import org.jlab.presenter.presentation.util.ShowInfo;

/**
 *
 * @author ryans
 */
@WebServlet(name = "LASOMenu", urlPatterns = {"/laso-menu"})
public class LASOMenu extends HttpServlet {

    @EJB
    PresentationFacade presentationFacade;
    @EJB
    LASOPresentationFacade lasoPresentationFacade;

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

        /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
        HttpSession session = request.getSession(true);
        Shift lasoShift = (Shift) session.getAttribute("lasoShift");
        Date lasoDate = (Date) session.getAttribute("lasoDate");

        if (lasoShift == null) {
            lasoShift = TimeUtil.getCurrentShift();
        }

        if (lasoDate == null) {
            lasoDate = TimeUtil.getCurrentYearMonthDay();
        }

        request.setAttribute("defaultDate", lasoDate);
        request.setAttribute("defaultShift", lasoShift);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/laso-menu.jsp").forward(request, response);
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
        Shift shift = null;

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
            shift = ConvertAndValidateUtil.convertAndValidateShift(request,
                    "shift");

            if (shift == null) {
                messages.put("shift", "Shift must not be empty");
            }
        } catch (IllegalArgumentException e) {
            messages.put("shift", "Invalid shift");
        }

        BigInteger presentationId = null;

        if (messages.isEmpty()) {
            presentationId = lasoPresentationFacade.findIdByYmdAndShift(ymd,
                    shift);

            if (presentationId == null && !presentationFacade.isAuthorized(
                    PresentationType.LASO_PRESENTATION)) {
                messages.put("error",
                        "No presentation found for specified criteria.  Login as LASO to create.");
            }
        }

        if (!messages.isEmpty()) {
            getServletConfig().getServletContext().getRequestDispatcher(
                    "/WEB-INF/views/laso-menu.jsp").forward(request, response);
        } else {

            /* Remember user selection */
            /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
            HttpSession session = request.getSession(true);
            session.setAttribute("lasoShift", shift);
            session.setAttribute("lasoDate", ymd);

            if (presentationId == null) {

                LASOPresentation presentation = new LASOPresentation();
                presentation.setYmd(ymd);
                presentation.setShift(shift);

                List<Slide> slides = getNewShiftLogSlides(request, response,
                        presentation);

                presentation.setSlideList(slides);

                presentationFacade.create(presentation); // This method sets last modified by

                presentationId = presentation.getPresentationId();
            }

            if ("Send to eLog".equals(request.getParameter("action"))) {
                presentationFacade.checkAuthorized();
                List<String> imageData = new ArrayList<>();
                String body = presentationFacade.getPresentationHTML(request,
                        response, presentationId, imageData);
                PresentationMenuUtil.logAndRedirect(request, response,
                        presentationId,
                        "LASO Shift Log " + ShowInfo.getLasoShowName(ymd, shift),
                        body,
                        ShowInfo.getLasoTags(),
                        ShowInfo.getLasoLogbooks(),
                        "/laso-menu",
                        presentationFacade,
                        imageData,
                        PresentationType.LASO_PRESENTATION);
            } else {
                PresentationMenuUtil.openOrExport(request, response, presentationId);
            }
        }
    }

    public List<Slide> getNewShiftLogSlides(HttpServletRequest request,
            HttpServletResponse response, LASOPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        // Slide 1
        ShiftInfoSlide shiftSlide = new ShiftInfoSlide(presentation.getYmd(),
                presentation.getShift(), ShiftSlideType.LASO);
        shiftSlide.setOrderId(1L);
        shiftSlide.setPresentation(presentation);

        slides.add(shiftSlide);

        return slides;
    }
}

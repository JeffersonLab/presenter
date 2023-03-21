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
import org.jlab.presenter.business.session.CCPresentationFacade;
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.entity.BtaShiftInfoSlide;
import org.jlab.presenter.persistence.entity.CCPresentation;
import org.jlab.presenter.persistence.entity.Slide;
import org.jlab.presenter.persistence.enumeration.PresentationType;
import org.jlab.presenter.persistence.enumeration.Shift;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;
import org.jlab.presenter.presentation.util.PresentationMenuUtil;
import org.jlab.presenter.presentation.util.ShowInfo;

/**
 *
 * @author ryans
 */
@WebServlet(name = "CCMenu", urlPatterns = {"/cc-menu"})
public class CCMenu extends HttpServlet {

    @EJB
    PresentationFacade presentationFacade;
    @EJB
    CCPresentationFacade ccPresentationFacade;

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
        Shift ccShift = (Shift) session.getAttribute("ccShift");
        Date ccDate = (Date) session.getAttribute("ccDate");

        if (ccShift == null) {
            ccShift = TimeUtil.getCurrentShift();
        }

        if (ccDate == null) {
            ccDate = TimeUtil.getCurrentYearMonthDay();
        }

        request.setAttribute("defaultDate", ccDate);
        request.setAttribute("defaultShift", ccShift);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/cc-menu.jsp").forward(request, response);
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
            presentationId = ccPresentationFacade.findIdByYmdAndShift(ymd,
                    shift);

            if (presentationId == null && !presentationFacade.isAuthorized(
                    PresentationType.CC_PRESENTATION)) {
                messages.put("error",
                        "No presentation found for specified criteria.  Login as CC to create.");
            }
        }

        if (!messages.isEmpty()) {
            getServletConfig().getServletContext().getRequestDispatcher(
                    "/WEB-INF/views/cc-menu.jsp").forward(request, response);
        } else {

            /* Remember user selection */
            /* Note: We use a 'SECURE' cookie so session changes every request unless over SSL/TLS */
            HttpSession session = request.getSession(true);
            session.setAttribute("ccShift", shift);
            session.setAttribute("ccDate", ymd);

            if (presentationId == null) {

                CCPresentation presentation = new CCPresentation();
                presentation.setYmd(ymd);
                presentation.setShift(shift);

                List<Slide> slides = getNewShiftLogSlides(request, response,
                        presentation);

                presentation.setSlideList(slides);

                presentationFacade.create(presentation); // This method sets last modified by

                presentationId = presentation.getPresentationId();
            }


            PresentationMenuUtil.openOrExport(request, response, presentationId);
        }
    }

    public List<Slide> getNewShiftLogSlides(HttpServletRequest request,
            HttpServletResponse response, CCPresentation presentation)
            throws ServletException, IOException {
        ArrayList<Slide> slides = new ArrayList<>();

        // Slide 1
        BtaShiftInfoSlide shiftSlide = new BtaShiftInfoSlide();
        shiftSlide.setYmd(presentation.getYmd());
        shiftSlide.setShift(presentation.getShift());
        shiftSlide.setOrderId(1L);
        shiftSlide.setPresentation(presentation);

        slides.add(shiftSlide);

        return slides;
    }
}

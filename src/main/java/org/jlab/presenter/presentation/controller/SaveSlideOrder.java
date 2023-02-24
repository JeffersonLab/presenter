package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
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
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.business.session.PresentationFacade.AuditRecord;
import org.jlab.presenter.business.session.SlideFacade;
import org.jlab.presenter.persistence.entity.Presentation;
import org.jlab.presenter.persistence.entity.Slide;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;

/**
 *
 * @author ryans
 */
@WebServlet(name = "SaveSlideOrder", urlPatterns = {"/save-slide-order"})
public class SaveSlideOrder extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            SaveSlideOrder.class.getName());
    @EJB
    PresentationFacade presentationFacade;
    @EJB
    SlideFacade slideFacade;

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
        String errorReason = null;
        AuditRecord audit = null;

        if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
            errorReason = "Your session has expired.  Please re-login.";
            logger.log(Level.INFO, "Session expired: {0}", request.getRequestedSessionId());
        }

        if (errorReason == null) {
            try {
                BigInteger presentationId = ConvertAndValidateUtil
                        .convertAndValidateBigIntegerParam(request, "presentationId");

                if (presentationId == null) {
                    throw new IllegalArgumentException("Parameter 'presentationid' "
                            + "must not be null");
                }

                List<BigInteger> slideIds = convertAndValidateSlideOrder(request);

                Presentation presentation = presentationFacade.findWithSlides(
                        presentationId);

                if (presentation == null) {
                    throw new IllegalArgumentException("Presentation with Id "
                            + presentationId + " not found");
                }

                List<Slide> reorderedSlides = convertAndValidateReorderedSlideList(
                        slideIds, presentation);

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "New Slide Order: ");
                    for (BigInteger id : slideIds) {
                        logger.log(Level.FINE, "SlideId: {0}", id);
                    }
                }

                presentation.setSlideList(reorderedSlides);

                presentation = presentationFacade.edit(presentation);

                logger.log(Level.FINE, "Saved presentation slide order; "
                        + "Presentation ID: {0}", presentationId);

                audit = presentationFacade.getAuditRecord(presentation);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unable to save new slide order", e);
                errorReason = e.getMessage();
            }
        }

        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span>";

            if (audit != null) {
                logger.log(Level.FINEST, "UPDATE SLIDE ORDER - Last modified is {0}",
                        audit.getLastModifiedMillis());
                xml = xml + "<span class=\"presentation\" data-last-modified-millis=\""
                        + audit.getLastModifiedMillis() + "\" data-user=\"" + audit.getUser()
                        + "\"></span>";
            }

            xml = xml + "</response>";
        } else {
            xml = "<response><span class=\"status\">Error</span><span "
                    + "class=\"reason\">" + errorReason + "</span></response>";
        }

        pw.write(xml);

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            logger.log(Level.SEVERE, "PrintWriter Error in "
                    + "SaveSlideOrder.doPost");
        }
    }

    private List<BigInteger> convertAndValidateSlideOrder(
            HttpServletRequest request) {
        ArrayList<BigInteger> slideIds = new ArrayList<BigInteger>();
        int numSlides = Integer.valueOf(request.getParameter("numSlides"));

        for (int i = 0; i < numSlides; i++) {
            BigInteger id = ConvertAndValidateUtil.convertAndValidateBigIntegerParam(
                    request, "slide[" + i + "]");
            if (id == null) {
                throw new IllegalArgumentException("Parameter 'slide[" + i
                        + "]' must not be null");
            }
            slideIds.add(id);
        }

        return slideIds;
    }

    private List<Slide> convertAndValidateReorderedSlideList(
            List<BigInteger> slideIds, Presentation presentation) {
        List<Slide> slides = presentation.getSlideList();
        List<Slide> reorderedSlides = new ArrayList<Slide>();

        if (slideIds.size() != slides.size()) {
            throw new IllegalArgumentException(
                    "Submitted slide count doesn't match database");
        }

        Map<BigInteger, Slide> slideMap = createSlideMap(slides);

        long order = 1;

        for (BigInteger slideId : slideIds) {
            Slide slide = slideMap.remove(slideId);

            if (slide == null) {
                throw new IllegalArgumentException(
                        "Slide ID not associated with this presentation: "
                        + slideId);
            }

            slide.setOrderId(order);

            reorderedSlides.add(slide);
            order++;
        }

        if (slideMap.size() > 0) {
            throw new IllegalArgumentException(
                    "Submitted slide list missing slide with ID: "
                    + slideMap.values().iterator().next().getSlideId());
        }

        return reorderedSlides;
    }

    private Map<BigInteger, Slide> createSlideMap(List<Slide> slides) {
        Map<BigInteger, Slide> slideMap = new HashMap<BigInteger, Slide>();

        for (Slide slide : slides) {
            slideMap.put(slide.getSlideId(), slide);
        }

        return slideMap;
    }
}

package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
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
import org.jlab.presenter.persistence.entity.BodySlide;
import org.jlab.presenter.persistence.entity.BtaShiftInfoSlide;
import org.jlab.presenter.persistence.entity.IFrameSlide;
import org.jlab.presenter.persistence.entity.ImageSlide;
import org.jlab.presenter.persistence.entity.PdAccessSlide;
import org.jlab.presenter.persistence.entity.PdBeamAccSlide;
import org.jlab.presenter.persistence.entity.PdInfoSlide;
import org.jlab.presenter.persistence.entity.Presentation;
import org.jlab.presenter.persistence.entity.ShiftInfoSlide;
import org.jlab.presenter.persistence.entity.Slide;
import org.jlab.presenter.persistence.entity.TitleBodyImageSlide;
import org.jlab.presenter.persistence.entity.TitleBodySlide;
import org.jlab.presenter.persistence.entity.TitleImageSlide;
import org.jlab.presenter.persistence.enumeration.SlideType;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;
import org.jlab.presenter.presentation.util.SlideUtil;

/**
 *
 * @author ryans
 */
@WebServlet(name = "NewSlide", urlPatterns = {"/new-slide"})
public class NewSlide extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            NewSlide.class.getName());
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
        BigInteger slideId = new BigInteger("0");
        AuditRecord audit = null;
        
        if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
            errorReason = "Your session has expired.  Please re-login.";
            LOGGER.log(Level.INFO, "Session expired: {0}", request.getRequestedSessionId());
        }

        if (errorReason == null) {
            try {
                BigInteger presentationId = ConvertAndValidateUtil
                        .convertAndValidateBigIntegerParam(request, "presentationId");
                SlideType type = ConvertAndValidateUtil.
                        convertAndValidateSlideType(request, "slideType");

                if (presentationId == null) {
                    throw new IllegalArgumentException("Parameter 'presentationId' "
                            + "must not be null");
                }

                if (type == null) {
                    throw new IllegalArgumentException("Parameter 'type' "
                            + "must not be null");
                }

                Slide slide = null;

                switch (type) {
                    case TITLE_BODY_SLIDE:
                        slide = new TitleBodySlide();
                        SlideUtil.update(request, (TitleBodySlide) slide);
                        break;
                    case BODY_SLIDE:
                        slide = new BodySlide();
                        SlideUtil.update(request, (BodySlide) slide);
                        break;
                    case IMAGE_SLIDE:
                        slide = new ImageSlide();
                        SlideUtil.update(request, (ImageSlide) slide);
                        break;
                    case TITLE_IMAGE_SLIDE:
                        slide = new TitleImageSlide();
                        SlideUtil.update(request, (TitleImageSlide) slide);
                        break;
                    case TITLE_BODY_IMAGE_SLIDE:
                        slide = new TitleBodyImageSlide();
                        SlideUtil.update(request, (TitleBodyImageSlide) slide);
                        break;
                    case IFRAME_SLIDE:
                        slide = new IFrameSlide();
                        SlideUtil.update(request, (IFrameSlide) slide);
                        break;
                    case SHIFT_INFO_SLIDE:
                        slide = new ShiftInfoSlide();
                        SlideUtil.update(request, (ShiftInfoSlide) slide);
                        break;
                    case BTA_SHIFT_INFO_SLIDE:
                        slide = new BtaShiftInfoSlide();
                        SlideUtil.update(request, (BtaShiftInfoSlide) slide);
                        break;
                    case PD_INFO_SLIDE:
                        slide = new PdInfoSlide();
                        SlideUtil.update(request, (PdInfoSlide) slide);
                        break;
                    case PD_BEAM_ACC_SLIDE:
                        slide = new PdBeamAccSlide();
                        SlideUtil.update(request, (PdBeamAccSlide) slide);
                        break;
                    case PD_ACCESS_SLIDE:
                        slide = new PdAccessSlide();
                        SlideUtil.update(request, (PdAccessSlide) slide);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Unknown SlideType: " + type);
                }

                String label = request.getParameter("label");
                slide.setLabel(label);

                Presentation presentation = slideFacade.create(slide, presentationId);
                slideId = slide.getSlideId();

                audit = presentationFacade.getAuditRecord(presentation);
                
                LOGGER.log(Level.FINE, "Created new slide with ID: {0}", slideId);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Unable to create new slide", e);
                errorReason = "Unable to create new slide; presentationId must be "
                        + "a number";
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unable to create new slide", e);
                errorReason = "Unable to create slide";
            }
        }

        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span>";
            xml = xml + "<span class=\"slideId\">" + slideId + "</span>";
            if (audit != null) {
                LOGGER.log(Level.FINEST, "UPDATE SLIDE ORDER - Last modified is {0}",
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
            LOGGER.log(Level.SEVERE, "PrintWriter Error in NewSlide.doPost");
        }
    }
}

package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.jlab.presenter.business.session.BodySlideFacade;
import org.jlab.presenter.business.session.BtaShiftInfoSlideFacade;
import org.jlab.presenter.business.session.IFrameSlideFacade;
import org.jlab.presenter.business.session.ImageSlideFacade;
import org.jlab.presenter.business.session.PdAccessSlideFacade;
import org.jlab.presenter.business.session.PdBeamAccSlideFacade;
import org.jlab.presenter.business.session.PdInfoSlideFacade;
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.business.session.PresentationFacade.AuditRecord;
import org.jlab.presenter.business.session.ShiftInfoSlideFacade;
import org.jlab.presenter.business.session.SlideFacade;
import org.jlab.presenter.business.session.TitleBodyImageSlideFacade;
import org.jlab.presenter.business.session.TitleBodySlideFacade;
import org.jlab.presenter.business.session.TitleImageSlideFacade;
import org.jlab.presenter.business.util.ExceptionUtil;
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
@WebServlet(name = "SaveSlide", urlPatterns = {"/save-slide"})
public class SaveSlide extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            SaveSlide.class.getName());
    @EJB
    TitleBodySlideFacade titleBodySlideFacade;
    @EJB
    BodySlideFacade bodySlideFacade;
    @EJB
    ImageSlideFacade imageSlideFacade;
    @EJB
    TitleImageSlideFacade titleImageSlideFacade;
    @EJB
    TitleBodyImageSlideFacade titleBodyImageSlideFacade;
    @EJB
    IFrameSlideFacade iframeSlideFacade;
    @EJB
    ShiftInfoSlideFacade shiftInfoSlideFacade;
    @EJB
    BtaShiftInfoSlideFacade btaShiftInfoSlideFacade;
    @EJB
    PdInfoSlideFacade pdInfoSlideFacade;
    @EJB
    PdBeamAccSlideFacade pdBeamAccSlideFacade;
    @EJB
    PdAccessSlideFacade pdAccessSlideFacade;
    @EJB
    SlideFacade slideFacade;
    @EJB
    PresentationFacade presentationFacade;
    
    /**
     * Handles the HTTP
     * <code>POST</code> method.
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
        String errorCode = null;
        
        BigInteger slideId = null;
        SlideType type = null;
       
        AuditRecord audit = null;        
        
        if(request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
            errorReason = "Your session has expired.  Please re-login.";
            errorCode = "SESSION_EXPIRED";
            logger.log(Level.INFO, "Session expired: {0}", request.getRequestedSessionId());
        }
        
        logger.log(Level.FINEST, "requestedSessionId: {0}", request.getRequestedSessionId());
        logger.log(Level.FINEST, "isRequestedSessionIdValid: {0}", request.isRequestedSessionIdValid());
        logger.log(Level.FINEST, "isNew: {0}", request.getSession().isNew());

        try {
            slideId = ConvertAndValidateUtil
                    .convertAndValidateBigIntegerParam(request, "slideId");

            if (slideId == null) {
                errorReason = "Parameter 'slideId' "
                        + "must not be null";
            }
        } catch (NumberFormatException e) {
            errorReason = "SlideId must be a number";
        }

        try {
            type = ConvertAndValidateUtil.
                    convertAndValidateSlideType(request, "slideType");

            if (type == null) {
                errorReason = "Parameter 'slideType' "
                        + "must not be null";
            }
        } catch (Exception e) {
            errorReason = e.getMessage();
        }

        Slide slide = null;
        
        if (errorReason == null) {
            try {
                switch (type) {
                    case TITLE_BODY_SLIDE:
                        slide = titleBodySlideFacade.find(slideId);
                        SlideUtil.update(request, (TitleBodySlide)slide);
                        break;
                    case BODY_SLIDE:
                        slide = bodySlideFacade.find(slideId);
                        SlideUtil.update(request, (BodySlide)slide);
                        break;
                    case IMAGE_SLIDE:
                        slide = imageSlideFacade.find(slideId);
                        SlideUtil.update(request, (ImageSlide)slide);
                        break;
                    case TITLE_IMAGE_SLIDE:
                        slide = titleImageSlideFacade.find(slideId);
                        SlideUtil.update(request, (TitleImageSlide)slide);
                        break;
                    case TITLE_BODY_IMAGE_SLIDE:
                        slide = titleBodyImageSlideFacade.find(slideId);
                        SlideUtil.update(request, (TitleBodyImageSlide)slide);
                        break;
                    case IFRAME_SLIDE:
                        slide = iframeSlideFacade.find(slideId);
                        SlideUtil.update(request, (IFrameSlide)slide);
                        break;
                    case SHIFT_INFO_SLIDE:
                        slide = shiftInfoSlideFacade.find(slideId);
                        SlideUtil.update(request, (ShiftInfoSlide)slide);
                        break;
                    case BTA_SHIFT_INFO_SLIDE:
                        slide = btaShiftInfoSlideFacade.find(slideId);
                        SlideUtil.update(request, (BtaShiftInfoSlide)slide);
                        break;
                    case PD_INFO_SLIDE:
                        slide = pdInfoSlideFacade.find(slideId);
                        SlideUtil.update(request, (PdInfoSlide)slide);
                        break;
                    case PD_BEAM_ACC_SLIDE:
                        slide = pdBeamAccSlideFacade.find(slideId);
                        SlideUtil.update(request, (PdBeamAccSlide)slide);
                        break;
                    case PD_ACCESS_SLIDE:
                        slide = pdAccessSlideFacade.find(slideId);
                        SlideUtil.update(request, (PdAccessSlide)slide);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Unknown SlideType: " + type);
                }

                Presentation presentation = slideFacade.update(slide);
                
                audit = presentationFacade.getAuditRecord(presentation);
                
                logger.log(Level.FINE, "Saved slide with ID: {0}", slideId);
            } catch(EJBAccessException e) {
                logger.log(Level.WARNING, "Access Denied", e);
                errorCode = "NOT_AUTHORIZED";
                errorReason = "Not Logged in / authorized";
            } catch(EJBException e) {
                Throwable t = ExceptionUtil.getRootCause(e);
                if(t instanceof ConstraintViolationException) {
                    ConstraintViolationException cve = (ConstraintViolationException)t;
                    errorReason = "Input validation error: ";
                    for(ConstraintViolation<?> violation: cve.getConstraintViolations()) {
                        errorReason = errorReason + violation.getPropertyPath() + " " + violation.getMessage() + ". ";
                    }
                } else {
                    errorReason = t.getClass().getSimpleName();
                }
            } catch(NumberFormatException e) {
                errorReason = "Invalid number format";
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unable to save slide", e);
                errorReason = "Unable to save slide";
            }
        }
        
        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span>";

            if (audit != null) {
                logger.log(Level.FINEST, "SAVE SLIDE - Last modified is {0}",
                        audit.getLastModifiedMillis());
                xml = xml + "<span class=\"presentation\" data-last-modified-millis=\""
                        + audit.getLastModifiedMillis() + "\" data-user=\"" + audit.getUser()
                        + "\"></span>";
            }

            xml = xml + "</response>";
        } else {
            xml = "<response><span class=\"status\">Error</span><span "
                    + "class=\"reason\">" + errorReason + "</span><span class=\"code\">" + errorCode + "</span></response>";
        }

        pw.write(xml);

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            logger.log(Level.SEVERE, "PrintWriter Error in SaveSlide.doPost");
        }
    }
}

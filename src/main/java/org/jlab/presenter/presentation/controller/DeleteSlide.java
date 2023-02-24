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
import org.jlab.presenter.persistence.entity.Presentation;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;

/**
 *
 * @author ryans
 */
@WebServlet(name = "DeleteSlide", urlPatterns = {"/delete-slide"})
public class DeleteSlide extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            DeleteSlide.class.getName());
    @EJB
    SlideFacade slideFacade;
    @EJB
    PresentationFacade presentationFacade;

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
            LOGGER.log(Level.INFO, "Session expired: {0}", request.getRequestedSessionId());
        }

        if (errorReason == null) {
            try {
                BigInteger slideId = ConvertAndValidateUtil.convertAndValidateBigIntegerParam(
                        request, "slideId");

                if (slideId == null) {
                    errorReason = "Parameter 'slideId' must not be null";
                } else {
                    LOGGER.log(Level.FINE, "deleting slideId: {0}", slideId);

                    Presentation presentation = slideFacade.delete(slideId);
                    
                    audit = presentationFacade.getAuditRecord(presentation);                    
                }
            } catch (NumberFormatException e) {
                errorReason = "SlideId must be a number";
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unable to delete slide", e);
                errorReason = e.getMessage();
            }
        }

        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span>";

            if (audit != null) {
                LOGGER.log(Level.FINEST, "DELETE SLIDE - Last modified is {0}", audit.getLastModifiedMillis());
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
            LOGGER.log(Level.SEVERE, "PrintWriter Error in DeleteSlide.doPost");
        }
    }
}

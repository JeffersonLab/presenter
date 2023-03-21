package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.presenter.business.exception.WebAppException;
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;

/**
 *
 * @author ryans
 */
@WebServlet(name = "SendToElog", urlPatterns = {"/send-to-elog"})
public class SendToElog extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            SendToElog.class.getName());
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
        String errorCode = null;
        long logId = 0;

        if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
            errorReason = "Your session has expired.  Please re-login.";
            errorCode = "SESSION_EXPIRED";
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

                logId = presentationFacade.publicSendELogRequest(presentationId, request, response);

                logger.log(Level.FINE, "Sending to elog; "
                        + "Presentation ID: {0}", presentationId);
            } catch (EJBAccessException e) {
                logger.log(Level.WARNING, "Access denied", e);
                errorCode = "NOT_AUTHORIZED";
                errorReason = e.getMessage();
            } catch (WebAppException e) {
                logger.log(Level.WARNING, "Unable to send to elog", e);
                errorReason = e.getMessage();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unable to send to elog", e);
                errorReason = "Unexpected Error";
            }
        }

        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span><span class=\"logId\">" + logId + "</span></response>";
        } else {
            xml = "<response><span class=\"status\">Error</span><span "
                    + "class=\"reason\">" + errorReason + "</span><span class=\"code\">" + errorCode + "</span></response>";
        }

        pw.write(xml);

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            logger.log(Level.SEVERE, "PrintWriter Error in "
                    + "SendToElog.doPost");
        }
    }
}

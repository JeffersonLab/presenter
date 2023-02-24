package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
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
import org.jlab.presenter.business.util.IOUtil;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;

/**
 *
 * @author ryans
 */
@WebServlet(name = "KeepAlive", urlPatterns = {"/keep-alive"})
public class KeepAlive extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            KeepAlive.class.getName());

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
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String errorReason = null;

        BigInteger presentationId = ConvertAndValidateUtil
                .convertAndValidateBigIntegerParam(
                        request, "presentationId");

        List<AuditRecord> auditList = null;

        if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
            errorReason = "Your session has expired.  Please re-login.";
            logger.log(Level.INFO, "Session expired: {0}", request.getRequestedSessionId());
        } else {
            auditList = presentationFacade.getAuditRecordList(presentationId);
        }

        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span>";

            for (AuditRecord audit : auditList) {

                String description = audit.getDescription();

                if(description == null) {
                    description = "";
                }

                /*logger.log(Level.FINEST, "KEEP ALIVE - Last modified is {0}",
                        audit.getLastModifiedMillis());*/
                xml = xml + "<span class=\"presentation\" data-id=\"" + audit.getPresentationId()
                        + "\" data-last-modified-millis=\""
                        + audit.getLastModifiedMillis() + "\" data-user=\"" + IOUtil.escapeXml(audit.getUser())
                        + "\" data-description=\"" + IOUtil.escapeXml(description) + "\"></span>";
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
            logger.log(Level.SEVERE, "PrintWriter Error in KeepAlive.doPost");
        }
    }
}

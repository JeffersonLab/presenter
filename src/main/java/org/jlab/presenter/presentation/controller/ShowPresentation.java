package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.persistence.entity.Presentation;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;
import org.jlab.presenter.presentation.util.ShowInfo;
import org.jlab.presenter.presentation.util.TemplateFinder;

/**
 *
 * @author ryans
 */
@WebServlet(name = "ShowPresentation", urlPatterns = {"/presentation"})
public class ShowPresentation extends HttpServlet {

    @EJB
    PresentationFacade presentationFacade;

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

        BigInteger presentationId = ConvertAndValidateUtil
                .convertAndValidateBigIntegerParam(
                        request, "presentationId");

        Presentation presentation;

        if (presentationId != null) {
            presentation = presentationFacade.findWithSlides(presentationId);

            if (presentation == null) {
                throw new IllegalArgumentException("Presentation with Id "
                        + presentationId + " not found");
            }

        } else {
            throw new IllegalArgumentException("Parameter 'presentationId' "
                    + "must not be null");
        }

        boolean editable = presentationFacade.isAuthorized(presentation.getPresentationType());

        String logbookHostname = System.getenv("LOGBOOK_HOSTNAME");

        ShowInfo info = ShowInfo.getShowInfo(presentation, editable, logbookHostname);

        // As an extra sanity check let's verify that the slides are ordered 
        // sequentially.  Otherwise Next/Previous buttons will be broken.  This
        // Should be extremely rare, but I have seen it.  As an alternative to 
        // this check we could probably create a database trigger that ensures 
        // the slide order is always sequential
        presentationFacade.checkSlideOrder(presentation);

        // We need to populate transient syncedFromPresentationId and syncedFromPresentationType fields
        presentationFacade.populateSyncedInfo(presentation);
        
        request.setAttribute("presentation", presentation);
        request.setAttribute("show", info);
        request.setAttribute("finder", new TemplateFinder());
        
        request.setAttribute("auditList", presentationFacade.getAuditRecordList(
                presentation.getPresentationId()));
        
        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/presentation.jsp").forward(request, response);
    }
}

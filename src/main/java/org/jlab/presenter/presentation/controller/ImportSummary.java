package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.presenter.business.session.PDPresentationFacade;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;

/**
 *
 * @author ryans
 */
@WebServlet(name = "ImportSummary", urlPatterns = {"/import-summary"})
public class ImportSummary extends HttpServlet {

    @EJB
    PDPresentationFacade pdPresentationFacade;
    
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BigInteger presentationId = ConvertAndValidateUtil
                .convertAndValidateBigIntegerParam(
                request, "presentationId");

        if (presentationId == null) {
            throw new IllegalArgumentException("Parameter 'presentationId' "
                    + "must not be null");
        }

        pdPresentationFacade.importIncomingToOutgoing(presentationId);
        
        response.sendRedirect(response.encodeRedirectURL(
                request.getContextPath() + "/presentation?presentationId="
                + presentationId));
    }
}

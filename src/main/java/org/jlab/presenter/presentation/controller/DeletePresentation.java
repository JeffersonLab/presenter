package org.jlab.presenter.presentation.controller;

import java.io.IOException;
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
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;

/**
 *
 * @author ryans
 */
@WebServlet(name = "DeletePresentation", urlPatterns = {"/delete-presentation"})
public class DeletePresentation extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            DeletePresentation.class.getName());
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

        BigInteger presentationId = null;

        try {
            presentationId = ConvertAndValidateUtil.convertAndValidateBigIntegerParam(
                    request, "presentationId");

            if (presentationId == null) {
                throw new IllegalArgumentException("Parameter 'presentationId' "
                        + "must not be null");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parameter 'presentationId' "
                    + "must be a number", e);
        }

        LOGGER.log(Level.FINE, "deleting presentationId: {0}", presentationId);

        presentationFacade.delete(presentationId);

        response.sendRedirect("staff-menu");
    }
}

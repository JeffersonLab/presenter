package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.presenter.business.session.PDPresentationFacade;
import org.jlab.presenter.persistence.enumeration.PDPresentationType;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;
import org.jlab.presenter.presentation.util.PresentationMenuUtil;

/**
 *
 * @author ryans
 */
@WebServlet(name = "PDAfternoon", urlPatterns = {"/pd-afternoon"})
public class PDAfternoon extends HttpServlet {

    @EJB
    PDPresentationFacade pdPresentationFacade;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BigInteger presentationId = null;

        boolean pdf = "Y".equals(request.getParameter("pdf"));        
        
        Date ymd = null;

        String error = null;

        try {
            ymd = ConvertAndValidateUtil.convertAndValidateYMDParam(request,
                    "date");

            if (ymd == null) {
                error = "Date must not be empty";
            }
        } catch (ParseException e) {
            error = "Invalid date format";
        }

        if (error == null) {

            PDPresentationType[] typeArray = {PDPresentationType.SUM3, PDPresentationType.SUM2,
                PDPresentationType.SUM1};
            for (PDPresentationType type : typeArray) {
                presentationId = pdPresentationFacade.findIdByYmdAndPDType(ymd,
                        type);

                if (presentationId != null) {
                    break;
                }
            }

            if (presentationId != null) {
                if(pdf) {
                    response.sendRedirect(PresentationMenuUtil.getPdfUrl(presentationId));
                } else{
                    response.sendRedirect(response.encodeRedirectURL(
                        request.getContextPath() + "/presentation?presentationId="
                        + presentationId + "#1"));
                }
            } else {
                error = "No afternoon presentation exists for this date";
            }
        }

        if (error != null) {
            throw new ServletException(error);
        }
    }
}

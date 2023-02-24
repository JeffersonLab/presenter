package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.presenter.business.session.StaffPresentationFacade;
import org.jlab.presenter.persistence.entity.StaffPresentation;

/**
 *
 * @author ryans
 */
@WebServlet(name = "StaffMenu", urlPatterns = {"/staff-menu"})
public class StaffMenu extends HttpServlet {

    @EJB
    StaffPresentationFacade staffPresentationFacade;

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /*@Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        List<StaffPresentation> staffPresentations = staffPresentationFacade.findAll();

        request.setAttribute("staffPresentations", staffPresentations);

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/views/staff-menu.jsp").forward(request, response);
    }*/

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /*@Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, String> messages = new HashMap<String, String>();
        request.setAttribute("messages", messages);

        String title = request.getParameter("title");

        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title must not be empty");
        }

        StaffPresentation presentation = new StaffPresentation();
        presentation.setTitle(title);
        presentation.setStaffId(BigInteger.valueOf(9304L));

        try {
            staffPresentationFacade.create(presentation);
        } catch (Exception e) {
            Throwable root = e;

            while (root.getCause() != null) {
                root = root.getCause();
            }

            if (root instanceof SQLIntegrityConstraintViolationException && root.getMessage().contains("unique constraint")) {
                messages.put("title", "Duplicate titles are not allowed");
            } else {
                messages.put("title", "Unable to create presentation");
            }
        }

        if (!messages.isEmpty()) {
            List<StaffPresentation> staffPresentations = staffPresentationFacade.findAll();

            request.setAttribute("staffPresentations", staffPresentations);            
            
            getServletConfig().getServletContext().getRequestDispatcher(
                    "/WEB-INF/views/staff-menu.jsp").forward(request, response);
        } else {
            response.sendRedirect(response.encodeRedirectURL(
                    request.getContextPath() + "/presentation?presentationId="
                    + presentation.getPresentationId()));
        }
    }*/
}

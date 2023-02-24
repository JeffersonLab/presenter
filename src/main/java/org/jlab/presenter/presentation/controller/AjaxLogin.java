package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ryans
 */
@WebServlet(name = "AjaxLogin", urlPatterns = {"/ajax-login"})
public class AjaxLogin extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            AjaxLogin.class.getName());    
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String errorReason = null;
        
        if (request.getRemoteUser() != null) {
            errorReason = "You're already logged in";
        }else {
            try {
                request.login(username, password);
                // If here then login success
            } catch (ServletException e) {
                logger.log(Level.WARNING, "Unable to login", e);
                errorReason = "Invalid Username or Password";
            }
        }
        
        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span></response>";
        } else {
            xml = "<response><span class=\"status\">Error</span><span "
                    + "class=\"reason\">" + errorReason + "</span></response>";
        }

        pw.write(xml);

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            logger.log(Level.SEVERE, "PrintWriter Error in SaveSlide.doPost");
        }        
        
    }
}

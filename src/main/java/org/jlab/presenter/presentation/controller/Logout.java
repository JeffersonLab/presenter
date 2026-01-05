package org.jlab.presenter.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ryans
 */
@WebServlet(
    name = "Logout",
    urlPatterns = {"/logout"})
public class Logout extends HttpServlet {

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
    request.logout();
    request.getSession().invalidate();
    String returnUrl = request.getParameter("returnUrl");
    if (returnUrl == null || returnUrl.isEmpty()) {
      returnUrl = request.getContextPath();
    }
    response.sendRedirect(response.encodeRedirectURL(returnUrl));
  }
}

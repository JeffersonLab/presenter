package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ryans
 */
@WebServlet(
    name = "Relogin",
    urlPatterns = {"/relogin"})
public class Relogin extends HttpServlet {

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
    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/relogin.jsp")
        .forward(request, response);
  }
}

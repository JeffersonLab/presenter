package org.jlab.presenter.presentation.controller;

import jakarta.ejb.EJBAccessException;
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
    name = "Error",
    urlPatterns = {"/error"})
public class Error extends HttpServlet {

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
    Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
    Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    String servletName = (String) request.getAttribute("jakarta.servlet.error.servlet_name");
    String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");

    String message = "An unknown error has occurred.";

    if (throwable != null) {
      System.err.println("Throwable Class: " + throwable.getClass());

      if (throwable instanceof EJBAccessException) {
        message =
            "You are not authorized to perform the requested action.  Your session may have expired.  Please re-login.";
      } else {
        message = throwable.getMessage();
      }
    }

    System.err.println("Status Code: " + statusCode);
    System.err.println("Servlet Name: " + servletName);
    System.err.println("Request URI: " + requestUri);

    request.setAttribute("message", message);
    getServletConfig()
        .getServletContext()
        .getRequestDispatcher("/WEB-INF/views/error.jsp")
        .forward(request, response);
  }

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
    doGet(request, response);
  }
}

package org.jlab.presenter.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "ElogHTML",
    urlPatterns = {"/elog-html"})
public class ElogHtml extends HttpServlet {

  @EJB PresentationFacade presentationFacade;

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

    BigInteger presentationId =
        ConvertAndValidateUtil.convertAndValidateBigIntegerParam(request, "presentationId");

    // Note: we ignore image data array that is filled in here.
    List<String> imageData = new ArrayList<>();
    String body =
        presentationFacade.getPresentationHTML(request, response, presentationId, imageData);

    response.setContentType("text/html");

    PrintWriter out = response.getWriter();
    out.println(body);
  }
}

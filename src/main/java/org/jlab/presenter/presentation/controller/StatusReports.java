package org.jlab.presenter.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jlab.presenter.business.session.TeamFacade;
import org.jlab.presenter.persistence.entity.Team;

import java.io.IOException;
import java.util.List;

/**
 * @author ryans
 */
@WebServlet(
    name = "Status Reports",
    urlPatterns = {"/status-reports"})
public class StatusReports extends HttpServlet {

  @EJB
  TeamFacade teamFacade;

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

    List<Team> teamList = teamFacade.findAllWithMembers(null);

    request.setAttribute("teamList", teamList);

    request.getRequestDispatcher("/WEB-INF/views/status-reports.jsp").forward(request, response);
  }
}

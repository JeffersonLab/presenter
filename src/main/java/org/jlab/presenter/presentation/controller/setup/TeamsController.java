package org.jlab.presenter.presentation.controller.setup;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.jlab.presenter.business.session.TeamFacade;
import org.jlab.presenter.persistence.entity.Team;

/**
 * @author ryans
 */
@WebServlet(
    name = "TeamsController",
    urlPatterns = {"/setup/teams"})
public class TeamsController extends HttpServlet {

  @EJB TeamFacade teamFacade;

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

    String name = request.getParameter("name");

    List<Team> teamList = teamFacade.findAllWithMembers(null);

    String selectionMessage = null;

    if (name != null) {
      selectionMessage = "Team \"" + name + "\"";
    }

    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("teamList", teamList);

    request.getRequestDispatcher("/WEB-INF/views/setup/teams.jsp").forward(request, response);
  }
}

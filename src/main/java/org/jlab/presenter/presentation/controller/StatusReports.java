package org.jlab.presenter.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.jlab.presenter.business.session.TeamFacade;
import org.jlab.presenter.business.session.TeamStatusReportFacade;
import org.jlab.presenter.persistence.entity.Team;
import org.jlab.presenter.persistence.entity.TeamStatusReport;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "Status Reports",
    urlPatterns = {"/status-reports"})
public class StatusReports extends HttpServlet {

  @EJB TeamFacade teamFacade;

  @EJB TeamStatusReportFacade teamStatusReportFacade;

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

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int max = ParamUtil.convertAndValidateNonNegativeInt(request, "max", 10);

    if (max > 1000) {
      max = 1000;
    }

    BigInteger teamId = ParamConverter.convertBigInteger(request, "teamId");

    Team selectedTeam = null;

    if (teamId != null) {
      selectedTeam = teamFacade.find(teamId);

      if (selectedTeam == null) {
        throw new ServletException("Team with id " + teamId + " not found");
      }
    }

    List<Team> teamList = teamFacade.findAllWithMembers(null);
    List<TeamStatusReport> teamStatusReportList =
        teamStatusReportFacade.filterList(selectedTeam, offset, max);

    Long totalRecords = teamStatusReportFacade.countList(selectedTeam);

    Paginator paginator = new Paginator(totalRecords.intValue(), offset, max);

    String selectionMessage = createSelectionMessage(paginator, selectedTeam);

    request.setAttribute("teamList", teamList);
    request.setAttribute("teamStatusReportList", teamStatusReportList);
    request.setAttribute("paginator", paginator);
    request.setAttribute("selectionMessage", selectionMessage);

    request.getRequestDispatcher("/WEB-INF/views/status-reports.jsp").forward(request, response);
  }

  private String createSelectionMessage(Paginator paginator, Team team) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    StringBuilder selectionMessage = new StringBuilder("All Reports");

    List<String> filters = new ArrayList<>();

    if (team != null) {
      filters.add("Team \"" + team.getName() + "\"");
    }

    if (!filters.isEmpty()) {
      selectionMessage = new StringBuilder(filters.get(0));

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        selectionMessage.append(" and ").append(filter);
      }
    }

    if (paginator.getTotalRecords() < paginator.getMaxPerPage() && paginator.getOffset() == 0) {
      selectionMessage
          .append(" {")
          .append(formatter.format(paginator.getTotalRecords()))
          .append("}");
    } else {
      selectionMessage
          .append(" {")
          .append(formatter.format(paginator.getStartNumber()))
          .append(" - ")
          .append(formatter.format(paginator.getEndNumber()))
          .append(" of ")
          .append(formatter.format(paginator.getTotalRecords()))
          .append("}");
    }

    return selectionMessage.toString();
  }
}

package org.jlab.presenter.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.presenter.business.exception.WebAppException;
import org.jlab.presenter.business.session.CCPresentationFacade;
import org.jlab.presenter.business.session.LASOPresentationFacade;
import org.jlab.presenter.business.session.PDPresentationFacade;
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.business.session.SlideFacade;
import org.jlab.presenter.persistence.entity.PDPresentation;
import org.jlab.presenter.persistence.enumeration.PresentationType;
import org.jlab.presenter.presentation.util.ConvertAndValidateUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "ImportShiftLogs",
    urlPatterns = {"/import-shift-logs"})
public class ImportShiftLogs extends HttpServlet {

  @EJB PDPresentationFacade pdPresentationFacade;
  @EJB PresentationFacade presentationFacade;
  @EJB CCPresentationFacade ccPresentationFacade;
  @EJB SlideFacade slideFacade;
  @EJB LASOPresentationFacade lasoPresentationFacade;

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

    BigInteger presentationId =
        ConvertAndValidateUtil.convertAndValidateBigIntegerParam(request, "presentationId");

    BigInteger precedingDayCountBig =
        ConvertAndValidateUtil.convertAndValidateBigIntegerParam(request, "preceding-day-count");

    int precedingDayCount;

    if (precedingDayCountBig == null) { // Just use default
      PDPresentation p = pdPresentationFacade.find(presentationId);

      if (p == null) {
        throw new IllegalArgumentException("Presentation with ID " + presentationId + " not found");
      }

      precedingDayCount = p.getShiftLogDays();
      // throw new IllegalArgumentException("Preceding day count must not be null");
    } else { // must be authenticated and authorized to make changes
      precedingDayCount = precedingDayCountBig.intValue();

      if (!presentationFacade.isAuthorized(PresentationType.PD_PRESENTATION)) {
        throw new IllegalArgumentException("Login as PD to select / refresh shift logs");
      }
    }

    Boolean includeGraphs = ConvertAndValidateUtil.convertYNBoolean(request, "includeGraphs");

    if (includeGraphs == null) {
      throw new IllegalArgumentException("includeGraphs must not be null");
    }

    try {
      pdPresentationFacade.updatePrecedingDayCount(presentationId, precedingDayCount);
    } catch (WebAppException e) {
      throw new ServletException(e);
    }

    pdPresentationFacade.importShiftLogs(presentationId, precedingDayCount, includeGraphs);

    response.sendRedirect(
        response.encodeRedirectURL(
            request.getContextPath()
                + "/presentation?presentationId="
                + presentationId.toString()));
  }
}

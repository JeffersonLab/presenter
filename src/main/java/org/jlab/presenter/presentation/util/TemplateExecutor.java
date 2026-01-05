package org.jlab.presenter.presentation.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jlab.presenter.persistence.entity.Slide;

public class TemplateExecutor {

  public static String execute(
      HttpServletRequest request, HttpServletResponse response, Slide slide)
      throws ServletException, IOException {
    request.setAttribute("slide", slide);
    InternalResponseWrapper responseWrapper = new InternalResponseWrapper(response);
    request
        .getServletContext()
        .getRequestDispatcher(new TemplateFinder().getPath(slide))
        .include(request, responseWrapper);
    return responseWrapper.getHTML();
  }
}

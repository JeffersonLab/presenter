package org.jlab.presenter.business.session;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import org.jlab.presenter.business.util.IOUtil;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.business.util.UrlUtil;
import org.jlab.presenter.persistence.enumeration.PDPresentationType;
import org.jlab.presenter.presentation.util.PresentationMenuUtil;

@Singleton
@Startup
public class DailyScheduledLogger {

  private static final Logger logger = Logger.getLogger(DailyScheduledLogger.class.getName());

  private Timer timer;
  @Resource private TimerService timerService;
  @EJB PDPresentationFacade pdPresentationFacade;
  @EJB PresentationFacade presentationFacade;

  @PostConstruct
  private void init() {
    clearTimer();
    startTimer();
  }

  private void clearTimer() {
    logger.log(Level.FINEST, "Clearing Daily Timer");
    for (Timer t : timerService.getTimers()) {
      t.cancel();
    }
    timer = null;
  }

  private void startTimer() {
    logger.log(Level.FINEST, "Starting Daily Timer");
    ScheduleExpression schedExp = new ScheduleExpression();
    schedExp.second("0");
    schedExp.minute("30");
    schedExp.hour("8");
    schedExp.dayOfWeek("Mon, Tue, Wed, Thu, Fri");
    timer = timerService.createCalendarTimer(schedExp);
  }

  @Timeout
  private void handleTimeout(Timer timer) {
    logger.log(Level.FINEST, "Auto-Send-To-Elog Daily");
    send8AMElog();
  }

  private void send8AMElog() {
    Date ymd = TimeUtil.getCurrentYearMonthDay();
    PDPresentationType[] typeArray = {
      PDPresentationType.RUN, PDPresentationType.HCO, PDPresentationType.SAD
    };
    for (PDPresentationType type : typeArray) {
      BigInteger presentationId = pdPresentationFacade.findIdByYmdAndPDType(ymd, type);

      if (presentationId != null) {
        // Make an HTTP Request as we need the Servlet Engine to do this
        String url = UrlUtil.getPresentationELogBodyUrl(presentationId);
        try {
          String response = IOUtil.doHtmlGet(url, 10000, 10000);
          logger.log(Level.FINEST, "AutoLog Response: {0}", response);
          if (response.contains("Error")) {
            logger.log(
                Level.WARNING,
                "Unable to automatically create 8:00 presentation log entry: {0}",
                response);
          }

          List<String> images = presentationFacade.getPresentationImages(presentationId);

          long logId =
              PresentationMenuUtil.logPd(
                  pdPresentationFacade.find(presentationId), presentationFacade, response, images);

          if (logId == -1) {
            logger.log(Level.WARNING, "Unable to create 1:30 log entry, result is -1");
          }
        } catch (Exception e) {
          logger.log(
              Level.WARNING, "Unable to automatically create 8:00 presentation log entry", e);
        }
      }
    }
  }
}

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
public class WeeklyScheduledLogger {

    private static final Logger logger = Logger.getLogger(
            WeeklyScheduledLogger.class.getName());

    private Timer timer;
    @Resource
    private TimerService timerService;
    @EJB
    PDPresentationFacade pdPresentationFacade;

    @EJB
   PresentationFacade presentationFacade;

    @PostConstruct
    private void init() {
        clearTimer();
        startTimer();
    }

    private void clearTimer() {
        logger.log(Level.FINEST, "Clearing Weekly Timer");
        for (Timer t : timerService.getTimers()) {
            t.cancel();
        }
        timer = null;
    }

    private void startTimer() {
        logger.log(Level.FINEST, "Starting Weekly Timer");
        ScheduleExpression schedExp = new ScheduleExpression();
        schedExp.second("0");
        schedExp.minute("0");
        schedExp.hour("14");
        schedExp.dayOfWeek("Wed");
        timer = timerService.createCalendarTimer(schedExp);
    }

    @Timeout
    private void handleTimeout(Timer timer) {
        logger.log(Level.FINEST, "Auto-Send-To-Elog Weekly");
        send130Elog();
    }

    private void send130Elog() {
        Date ymd = TimeUtil.getCurrentYearMonthDay();
        PDPresentationType[] typeArray = {PDPresentationType.SUM3, PDPresentationType.SUM2,
            PDPresentationType.SUM1};
        for (PDPresentationType type : typeArray) {
            BigInteger presentationId = pdPresentationFacade.findIdByYmdAndPDType(ymd,
                    type);

            if (presentationId != null) {
                // Make an HTTP Request as we need the Servlet Engine to do this
                String url = UrlUtil.getPresentationELogBodyUrl(presentationId);
                try {
                    boolean strictChecking = !url.contains("accwebtest") && !url.contains("localhost");

                    String response = IOUtil.doHtmlGet(url, 10000, 10000, strictChecking);

                    //String response = IOUtil.doHtmlPost(url, 10000, 10000, strictChecking);
                    logger.log(Level.FINEST, "AutoLog Response: {0}", response);
                    if(response.contains("Error")) {
                        logger.log(Level.WARNING, "Unable to automatically create 1:30 presentation log entry: {0}", response);
                    }

                    List<String> images = presentationFacade.getPresentationImages(presentationId);

                    long logId = PresentationMenuUtil.logPd(
                            pdPresentationFacade.find(presentationId),
                            presentationFacade,
                            response,
                            images);

                    if(logId == -1) {
                        logger.log(Level.WARNING, "Unable to create 1:30 log entry, result is -1");
                    }

                } catch (Exception e) {
                    logger.log(Level.WARNING,
                            "Unable to automatically create 1:30 presentation log entry", e);
                }
                break; // We don't need to look at other presentations
            }
        }
    }
}

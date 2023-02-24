package org.jlab.presenter.presentation.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.jlab.presenter.business.session.LOPresentationFacade;
import org.jlab.presenter.business.session.ShiftPresentationFacade;
import org.jlab.presenter.business.session.UITFPresentationFacade;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.entity.PDPresentation;
import org.jlab.presenter.persistence.entity.Presentation;
import org.jlab.presenter.persistence.entity.Slide;
import org.jlab.presenter.persistence.enumeration.Shift;

/**
 *
 * @author ryans
 */
public class PDPresentationUtil {

    protected static final Logger logger = Logger.getLogger(
            PDPresentationUtil.class.getName());

    private PDPresentationUtil() {
        // Can't instantiate publicly
    }

    public static List<Slide> getLerfSlides(PDPresentation presentation,
            LOPresentationFacade loPresentationFacade, long order,
            int precedingDayCount, boolean copySlides) {
        Date currentYmd = presentation.getDeliveryYmd();
        List<Slide> slides = new ArrayList<>();

        for (int i = precedingDayCount; i > 0; i--) {
            // DAY SHIFT
            List<Slide> currentSlides = getShiftLogForShiftAndDate(presentation,
                    loPresentationFacade, order, Shift.DAY, TimeUtil.addDays(
                            currentYmd, -i), copySlides);
            slides.addAll(currentSlides);
            order = order + currentSlides.size();
            // SWING SHIFT
            currentSlides = getShiftLogForShiftAndDate(presentation,
                    loPresentationFacade, order, Shift.SWING, TimeUtil.addDays(
                            currentYmd, -i), copySlides);
            slides.addAll(currentSlides);
            order = order + currentSlides.size();
            // OWL SHIFT
            currentSlides = getShiftLogForShiftAndDate(presentation,
                    loPresentationFacade, order, Shift.OWL, TimeUtil.addDays(
                            currentYmd, (-i + 1)), copySlides);
            slides.addAll(currentSlides);
            order = order + currentSlides.size();
        }

        return slides;
    }

    public static List<Slide> getUitfSlides(PDPresentation presentation,
                                            UITFPresentationFacade uitfPresentationFacade, long order,
                                            int precedingDayCount, boolean copySlides) {
        Date currentYmd = presentation.getDeliveryYmd();
        List<Slide> slides = new ArrayList<>();

        for (int i = precedingDayCount; i > 0; i--) {
            // DAY SHIFT
            List<Slide> currentSlides = getShiftLogForShiftAndDate(presentation,
                    uitfPresentationFacade, order, Shift.DAY, TimeUtil.addDays(
                            currentYmd, -i), copySlides);
            slides.addAll(currentSlides);
            order = order + currentSlides.size();
            // SWING SHIFT
            currentSlides = getShiftLogForShiftAndDate(presentation,
                    uitfPresentationFacade, order, Shift.SWING, TimeUtil.addDays(
                            currentYmd, -i), copySlides);
            slides.addAll(currentSlides);
            order = order + currentSlides.size();
            // OWL SHIFT
            currentSlides = getShiftLogForShiftAndDate(presentation,
                    uitfPresentationFacade, order, Shift.OWL, TimeUtil.addDays(
                            currentYmd, (-i + 1)), copySlides);
            slides.addAll(currentSlides);
            order = order + currentSlides.size();
        }

        return slides;
    }

    public static List<Slide> getShiftLogsInterleaved(PDPresentation presentation,
            ShiftPresentationFacade[] shiftPresentationFacadeArray, long order,
            int precedingDayCount, boolean copySlides,
            DailySlideGenerator[] dailySlideGeneratorArray) {
        Date currentYmd = presentation.getDeliveryYmd();
        List<Slide> slides = new ArrayList<Slide>();

        for (int i = precedingDayCount; i > 0; i--) {
            for (int j = 0; j < shiftPresentationFacadeArray.length; j++) {
                List<Slide> currentSlides = getShiftLogForShiftAndDate(presentation,
                        shiftPresentationFacadeArray[j], order, Shift.DAY, TimeUtil.addDays(
                                currentYmd, -i), copySlides);
                slides.addAll(currentSlides);
                order = order + currentSlides.size();
            }
            for (int j = 0; j < shiftPresentationFacadeArray.length; j++) {
                List<Slide> currentSlides = getShiftLogForShiftAndDate(presentation,
                        shiftPresentationFacadeArray[j], order, Shift.SWING, TimeUtil.addDays(
                                currentYmd, -i), copySlides);
                slides.addAll(currentSlides);
                order = order + currentSlides.size();
            }
            for (int j = 0; j < shiftPresentationFacadeArray.length; j++) {
                List<Slide> currentSlides = getShiftLogForShiftAndDate(presentation,
                        shiftPresentationFacadeArray[j], order, Shift.OWL, TimeUtil.addDays(
                                currentYmd, (-i + 1)), copySlides);
                slides.addAll(currentSlides);
                order = order + currentSlides.size();
            }
            if (dailySlideGeneratorArray != null) {
                for (int j = 0; j < dailySlideGeneratorArray.length; j++) {
                    Slide dailySlide = dailySlideGeneratorArray[j].getSlideForDay(TimeUtil.addDays(
                            currentYmd, (-i + 1)));
                    dailySlide.setPresentation(presentation);
                    dailySlide.setOrderId(order++);
                    slides.add(dailySlide);
                }
            }
        }

        return slides;
    }

    public static List<Slide> getShiftLogForShiftAndDate(PDPresentation presentation,
            ShiftPresentationFacade shiftPresentationFacade, long order, Shift shift, Date ymd,
            boolean copySlides) {
        ArrayList<Slide> slides = new ArrayList<Slide>();

        BigInteger presId = shiftPresentationFacade.findIdByYmdAndShift(ymd, shift);
        if (presId != null) {
            Presentation dayPres = shiftPresentationFacade.findWithSlides(presId);
            List<Slide> presSlides = dayPres.getSlideList();

            for (Slide s : presSlides) {
                if (copySlides) {
                    Slide copy = s.copySlide();
                    copy.setOrderId(order++);
                    copy.setPresentation(presentation);
                    slides.add(copy);
                } else {
                    slides.add(s);
                }
            }
        }

        return slides;
    }
}

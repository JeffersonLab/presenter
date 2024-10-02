package org.jlab.presenter.business.session;

import java.math.BigInteger;
import java.util.*;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.jlab.presenter.business.exception.WebAppException;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.entity.*;
import org.jlab.presenter.persistence.enumeration.*;
import org.jlab.presenter.presentation.util.DailyFsdGraphSlideGenerator;
import org.jlab.presenter.presentation.util.DailySlideGenerator;
import org.jlab.presenter.presentation.util.DailyTimeAccountingGraphSlideGenerator;
import org.jlab.presenter.presentation.util.PDPresentationUtil;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"pd", "presenter-admin"})
public class PDPresentationFacade extends AbstractFacade<PDPresentation> {

  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @EJB SlideFacade slideFacade;
  @EJB PresentationFacade presentationFacade;
  @EJB LASOPresentationFacade lasoPresentationFacade;
  @EJB CCPresentationFacade ccPresentationFacade;
  @EJB LOPresentationFacade loPresentationFacade;
  @EJB UITFPresentationFacade uitfPresentationFacade;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PDPresentationFacade() {
    super(PDPresentation.class);
  }

  @PermitAll
  public PDPresentation findWithSlides(BigInteger id) {
    PDPresentation p = find(id);

    return p;
  }

  @PermitAll
  public PdInfoSlide findFirstGoalSlide(Date ymd) {
    PdInfoSlide goalSlide = null;

    PDPresentation p = findByYmdAndPDType(ymd, PDPresentationType.SUM1);

    if (p != null && p.getSlideList() != null) {
      for (Slide s : p.getSlideList()) {
        if (Objects.requireNonNull(s.getSlideType()) == SlideType.PD_INFO_SLIDE) {
          goalSlide = (PdInfoSlide) s;

          if (goalSlide.getPdInfoSlideType() != PdInfoSlideType.FIRST_SUMMARY) {
            goalSlide = null;
          } else {
            break;
          }
        }
      }
    }

    return goalSlide;
  }

  @PermitAll
  public BodySlide findSecondGoalSlide(Date ymd) {
    BodySlide goalSlide = null;

    PDPresentation p = findByYmdAndPDType(ymd, PDPresentationType.SUM2);

    if (p != null && p.getSlideList() != null) {
      for (Slide s : p.getSlideList()) {
        if (Objects.requireNonNull(s.getSlideType()) == SlideType.BODY_SLIDE) {
          goalSlide = (BodySlide) s;

          if (goalSlide.getBodySlideType() != BodySlideType.PD_SUMMARY_PART_FOUR) {
            goalSlide = null;
          } else {
            break;
          }
        }
      }
    }

    return goalSlide;
  }

  @PermitAll
  public PDPresentation findByYmdAndPDType(Date ymd, PDPresentationType type) {
    TypedQuery<PDPresentation> q =
        em.createQuery(
            "select a from PDPresentation a where a.deliveryYmd = :ymd and a.pdPresentationType = :pdType",
            PDPresentation.class);

    q.setParameter("ymd", ymd);
    q.setParameter("pdType", type);

    List<PDPresentation> resultList = q.getResultList();

    PDPresentation presentation = null;

    if (resultList != null && !resultList.isEmpty()) {
      presentation = resultList.get(0);
    }

    return presentation;
  }

  @PermitAll
  public BigInteger findIdByYmdAndPDType(Date ymd, PDPresentationType type) {
    TypedQuery<BigInteger> q =
        em.createNamedQuery("PDPresentation.findIdByYmdAndPDType", BigInteger.class);

    q.setParameter("ymd", ymd);
    q.setParameter("pdType", type);

    List<BigInteger> result = q.getResultList();

    return (result.size() > 0) ? result.get(0) : null;
  }

  @PermitAll
  public void importShiftLogs(
      BigInteger presentationId, int precedingDayCount, boolean includeGraphs) {
    if (!isValidPrecedingDayCount(precedingDayCount)) {
      throw new IllegalArgumentException("Preceding day count must be between 1 - 7");
    }

    PDPresentation presentation;

    if (presentationId != null) {
      presentation = findWithSlides(presentationId);

      if (presentation == null) {
        throw new IllegalArgumentException("Presentation with Id " + presentationId + " not found");
      }
    } else {
      throw new IllegalArgumentException("Parameter 'presentationId' " + "must not be null");
    }

    presentationFacade.removeDailyGraphs(presentation);
    // presentationFacade.removeSyncedSlides(presentation); /* If using append instead of merge then
    // we need to remove synced slides*/

    DailySlideGenerator[] dailySlideGeneratorArray = null;

    if (includeGraphs) {
      dailySlideGeneratorArray =
          new DailySlideGenerator[] {
            new DailyTimeAccountingGraphSlideGenerator(), new DailyFsdGraphSlideGenerator()
          };
    }

    /**
     * If using "merge" later then don't copy as merge will do it for you; We do copy slides when
     * initially creating a presentation though
     */
    boolean copySlides = false;

    List<Slide> originalShiftSlides =
        PDPresentationUtil.getShiftLogsInterleaved(
            presentation,
            new ShiftPresentationFacade[] {ccPresentationFacade, lasoPresentationFacade},
            1,
            precedingDayCount,
            copySlides,
            dailySlideGeneratorArray);

    long order = originalShiftSlides.size() + 1;

    List<Slide> lerfSlides =
        PDPresentationUtil.getLerfSlides(
            presentation, loPresentationFacade, order, precedingDayCount, copySlides);

    originalShiftSlides.addAll(lerfSlides);

    // UITF
    order = originalShiftSlides.size() + 1;

    List<Slide> uitfSlides =
        PDPresentationUtil.getUitfSlides(
            presentation, uitfPresentationFacade, order, precedingDayCount, copySlides);

    originalShiftSlides.addAll(uitfSlides);

    // presentationFacade.append(presentation, originalShiftSlides);   /*if using "append" then make
    // sure  copySlides is true */
    presentationFacade.merge(presentation, originalShiftSlides, true);
  }

  @PermitAll
  public void importIncomingToOutgoing(BigInteger presentationId) {
    PDPresentation presentation = find(presentationId);

    if (presentation == null) {
      throw new IllegalArgumentException("Presentation with Id " + presentationId + " not found");
    }

    List<Slide> incomingSlides = getIncomingSlides(presentation, 1, false);

    presentationFacade.merge(presentation, incomingSlides, false);
  }

  @PermitAll
  public List<Slide> getIncomingSlides(
      PDPresentation presentation, long order, boolean copySlides) {
    List<Slide> slides = new ArrayList<>();

    BigInteger presentationId =
        findIdByYmdAndPDType(presentation.getDeliveryYmd(), PDPresentationType.SUM1);
    if (presentationId != null) {
      PDPresentation incomingPresentation = find(presentationId);
      List<Slide> incomingSlides = incomingPresentation.getSlideList();

      if (incomingSlides != null) {
        for (Slide s : incomingSlides) {
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
    }

    return slides;
  }

  @PermitAll
  public List<PDPresentation> findRecent() {
    TypedQuery<PDPresentation> q =
        em.createQuery(
            "select p from PDPresentation p where p.deliveryYmd > sysdate - 8 order by p.deliveryYmd desc, p.pdPresentationType desc",
            PDPresentation.class);

    return q.getResultList();
  }

  @RolesAllowed({"pd", "presenter-admin"})
  public void delete(BigInteger presentationId) {
    Presentation p = presentationFacade.find(presentationId);

    if (p != null && p.getPresentationType() == PresentationType.PD_PRESENTATION) {
      em.remove(p);
    }
  }

  @PermitAll
  public int getPrecedingDays(PDPresentation presentation) {
    Integer days = presentation.getShiftLogDays();

    if (!isValidPrecedingDayCount(days)) {
      days = getDefaultPrecedingDays(presentation.getDeliveryYmd());
      presentation.setShiftLogDays(days);
      this.edit(presentation);
    }

    return days;
  }

  @PermitAll
  public int getDefaultPrecedingDays(Date ymd) {
    int precedingDayCount = 1;
    if (TimeUtil.getDayOfWeek(ymd) == Calendar.MONDAY) {
      precedingDayCount = 3;
    }

    return precedingDayCount;
  }

  @PermitAll
  public boolean isValidPrecedingDayCount(Integer days) {
    final int MAX_DAYS = 7; // TODO: Make sure this matches UI
    final int MIN_DAYS = 1;

    return !(days == null || days > MAX_DAYS || days < MIN_DAYS);
  }

  @PermitAll
  public void updatePrecedingDayCount(BigInteger presentationId, int precedingDayCount)
      throws WebAppException {
    PDPresentation presentation = this.find(presentationId);

    if (presentation == null) {
      throw new WebAppException("PD Presentation with ID " + presentationId + " not found");
    }

    if (!isValidPrecedingDayCount(precedingDayCount)) {
      throw new WebAppException("Preceding day count must be between 1 and 7");
    }

    boolean changed =
        presentation.getShiftLogDays() == null
            || presentation.getShiftLogDays() != precedingDayCount;

    if (changed) {
      presentation.setShiftLogDays(precedingDayCount);

      presentationFacade.edit(presentation); // Also updates last modified
    }
  }
}

package org.jlab.presenter.business.session;

import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.entity.Presentation;
import org.jlab.presenter.persistence.entity.Slide;
import org.jlab.presenter.persistence.enumeration.PresentationType;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles({"pd", "cc", "presenter-admin"})
public class SlideFacade extends AbstractFacade<Slide> {

  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Resource private SessionContext context;
  @EJB PresentationFacade presentationFacade;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public SlideFacade() {
    super(Slide.class);
  }

  /**
   * Delete slide.
   *
   * @param slideId Slide ID
   * @return Presentation
   */
  @PermitAll
  public Presentation delete(BigInteger slideId) {
    Slide slide = find(slideId);

    PresentationType presentationType = slide.getPresentation().getPresentationType();

    String username = presentationFacade.checkAuthorized(presentationType);

    Presentation presentation = slide.getPresentation();

    // Must remove reference before deleting -
    // https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working
    presentation.getSlideList().remove(slide);

    em.remove(slide);

    presentation.setLastModified(TimeUtil.nowWithoutMillis());
    presentation.setLastUsername(username);

    return presentation;
  }

  @PermitAll
  public void createSlideListForRefresh(List<Slide> slides) {
    String username = context.getCallerPrincipal().getName();

    for (Slide slide : slides) {
      // presentationFacade.checkAuthorized(slide.getPresentation().getPresentationType());

      create(slide);

      Presentation presentation = slide.getPresentation();

      presentation.setLastModified(TimeUtil.nowWithoutMillis());

      presentation.setLastUsername(username);
    }
  }

  @PermitAll
  public Presentation create(Slide slide, BigInteger presentationId) {
    Presentation presentation = presentationFacade.find(presentationId);

    if (presentation == null) {
      throw new IllegalArgumentException("Presentation with Id " + presentationId + " not found");
    }

    String username = presentationFacade.checkAuthorized(presentation.getPresentationType());

    slide.setPresentation(presentation);
    slide.setOrderId((long) presentation.getSlideList().size() + 1);

    create(slide);

    presentation.setLastModified(TimeUtil.nowWithoutMillis());

    presentation.setLastUsername(username);

    return presentation;
  }

  @PermitAll
  public Presentation update(Slide slide) {
    PresentationType presentationType = slide.getPresentation().getPresentationType();

    String username = presentationFacade.checkAuthorized(presentationType);

    slide = edit(slide);

    Presentation presentation = slide.getPresentation();

    presentation.setLastModified(TimeUtil.nowWithoutMillis());

    presentation.setLastUsername(username);

    return presentation;
  }
}

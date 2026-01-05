package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.TitleBodyImageSlide;

/**
 * @author ryans
 */
@Stateless
public class TitleBodyImageSlideFacade extends AbstractFacade<TitleBodyImageSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public TitleBodyImageSlideFacade() {
    super(TitleBodyImageSlide.class);
  }
}

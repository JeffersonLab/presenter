package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.TitleBodySlide;

/**
 * @author ryans
 */
@Stateless
public class TitleBodySlideFacade extends AbstractFacade<TitleBodySlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public TitleBodySlideFacade() {
    super(TitleBodySlide.class);
  }
}

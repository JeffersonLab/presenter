package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

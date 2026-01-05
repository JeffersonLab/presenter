package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.BodySlide;

/**
 * @author ryans
 */
@Stateless
public class BodySlideFacade extends AbstractFacade<BodySlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public BodySlideFacade() {
    super(BodySlide.class);
  }
}

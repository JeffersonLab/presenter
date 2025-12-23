package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.PdAccessSlide;

/**
 * @author ryans
 */
@Stateless
public class PdAccessSlideFacade extends AbstractFacade<PdAccessSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PdAccessSlideFacade() {
    super(PdAccessSlide.class);
  }
}

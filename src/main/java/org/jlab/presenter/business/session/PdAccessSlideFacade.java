package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

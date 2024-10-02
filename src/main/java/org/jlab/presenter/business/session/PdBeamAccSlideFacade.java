package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.PdBeamAccSlide;

/**
 * @author ryans
 */
@Stateless
public class PdBeamAccSlideFacade extends AbstractFacade<PdBeamAccSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PdBeamAccSlideFacade() {
    super(PdBeamAccSlide.class);
  }
}

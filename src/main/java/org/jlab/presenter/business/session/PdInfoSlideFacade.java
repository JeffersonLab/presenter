package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.PdInfoSlide;

/**
 * @author ryans
 */
@Stateless
public class PdInfoSlideFacade extends AbstractFacade<PdInfoSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PdInfoSlideFacade() {
    super(PdInfoSlide.class);
  }
}

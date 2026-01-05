package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.ShiftInfoSlide;

/**
 * @author ryans
 */
@Stateless
public class ShiftInfoSlideFacade extends AbstractFacade<ShiftInfoSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ShiftInfoSlideFacade() {
    super(ShiftInfoSlide.class);
  }
}

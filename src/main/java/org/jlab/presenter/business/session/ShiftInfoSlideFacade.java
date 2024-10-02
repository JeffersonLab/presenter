package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

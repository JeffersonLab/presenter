package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.IFrameSlide;

/**
 * @author ryans
 */
@Stateless
public class IFrameSlideFacade extends AbstractFacade<IFrameSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public IFrameSlideFacade() {
    super(IFrameSlide.class);
  }
}

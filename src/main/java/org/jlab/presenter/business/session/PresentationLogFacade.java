package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.PresentationLog;

/**
 * @author ryans
 */
@Stateless
public class PresentationLogFacade extends AbstractFacade<PresentationLog> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PresentationLogFacade() {
    super(PresentationLog.class);
  }
}

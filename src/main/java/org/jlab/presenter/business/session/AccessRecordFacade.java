package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.AccessRecord;

/**
 * @author ryans
 */
@Stateless
public class AccessRecordFacade extends AbstractFacade<AccessRecord> {
  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public AccessRecordFacade() {
    super(AccessRecord.class);
  }
}

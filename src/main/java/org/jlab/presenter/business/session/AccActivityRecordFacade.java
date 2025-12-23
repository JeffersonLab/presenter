package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.AccActivityRecord;

/**
 * @author ryans
 */
@Stateless
public class AccActivityRecordFacade extends AbstractFacade<AccActivityRecord> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public AccActivityRecordFacade() {
    super(AccActivityRecord.class);
  }
}

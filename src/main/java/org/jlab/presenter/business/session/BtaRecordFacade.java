package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.BtaRecord;

/**
 * @author ryans
 */
@Stateless
public class BtaRecordFacade extends AbstractFacade<BtaRecord> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public BtaRecordFacade() {
    super(BtaRecord.class);
  }
}

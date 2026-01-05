package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.BeamToHallRecord;

/**
 * @author ryans
 */
@Stateless
public class BeamToHallRecordFacade extends AbstractFacade<BeamToHallRecord> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public BeamToHallRecordFacade() {
    super(BeamToHallRecord.class);
  }
}

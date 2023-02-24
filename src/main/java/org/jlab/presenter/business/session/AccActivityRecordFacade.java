package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.AccActivityRecord;

/**
 *
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

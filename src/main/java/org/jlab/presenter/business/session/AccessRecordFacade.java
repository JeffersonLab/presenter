package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.AccessRecord;

/**
 *
 * @author ryans
 */
@Stateless
public class AccessRecordFacade extends AbstractFacade<AccessRecord> {
    @PersistenceContext(unitName = "presenterPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccessRecordFacade() {
        super(AccessRecord.class);
    }
    
}

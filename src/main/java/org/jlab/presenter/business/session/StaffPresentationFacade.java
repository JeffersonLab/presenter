package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.StaffPresentation;

/**
 *
 * @author ryans
 */
@Stateless
public class StaffPresentationFacade extends AbstractFacade<StaffPresentation> {
    @PersistenceContext(unitName = "presenterPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StaffPresentationFacade() {
        super(StaffPresentation.class);
    }
    
}

package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.PdInfoSlide;

/**
 *
 * @author ryans
 */
@Stateless
public class PdInfoSlideFacade extends AbstractFacade<PdInfoSlide> {
    @PersistenceContext(unitName = "presenterPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PdInfoSlideFacade() {
        super(PdInfoSlide.class);
    }
    
}

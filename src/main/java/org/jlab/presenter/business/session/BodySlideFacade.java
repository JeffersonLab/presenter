package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.BodySlide;

/**
 *
 * @author ryans
 */
@Stateless
public class BodySlideFacade extends AbstractFacade<BodySlide> {
    @PersistenceContext(unitName = "presenterPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BodySlideFacade() {
        super(BodySlide.class);
    }
    
}

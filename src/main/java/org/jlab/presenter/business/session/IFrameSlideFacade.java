package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.IFrameSlide;

/**
 *
 * @author ryans
 */
@Stateless
public class IFrameSlideFacade extends AbstractFacade<IFrameSlide> {
    @PersistenceContext(unitName = "presenterPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IFrameSlideFacade() {
        super(IFrameSlide.class);
    }
    
}

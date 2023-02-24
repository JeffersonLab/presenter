package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.TitleSlide;

/**
 *
 * @author ryans
 */
@Stateless
public class TitleSlideFacade extends AbstractFacade<TitleSlide> {
    @PersistenceContext(unitName = "presenterPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TitleSlideFacade() {
        super(TitleSlide.class);
    }
    
}

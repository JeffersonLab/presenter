package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.DateSlide;

/**
 *
 * @author ryans
 */
@Stateless
public class DateSlideFacade extends AbstractFacade<DateSlide> {
    @PersistenceContext(unitName = "presenterPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DateSlideFacade() {
        super(DateSlide.class);
    }
    
}

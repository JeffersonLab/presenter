package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.TitleImageSlide;

/**
 * @author ryans
 */
@Stateless
public class TitleImageSlideFacade extends AbstractFacade<TitleImageSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public TitleImageSlideFacade() {
    super(TitleImageSlide.class);
  }
}

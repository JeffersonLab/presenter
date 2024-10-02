package org.jlab.presenter.business.session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jlab.presenter.persistence.entity.ImageSlide;

/**
 * @author ryans
 */
@Stateless
public class ImageSlideFacade extends AbstractFacade<ImageSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ImageSlideFacade() {
    super(ImageSlide.class);
  }
}

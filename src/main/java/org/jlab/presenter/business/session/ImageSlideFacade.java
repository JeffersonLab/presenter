package org.jlab.presenter.business.session;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

package org.jlab.presenter.business.session;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigInteger;
import org.jlab.presenter.persistence.entity.BtaShiftInfoSlide;

/**
 * @author ryans
 */
@Stateless
public class BtaShiftInfoSlideFacade extends AbstractFacade<BtaShiftInfoSlide> {
  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public BtaShiftInfoSlideFacade() {
    super(BtaShiftInfoSlide.class);
  }

  @PermitAll
  public int deleteBtaRecords(BigInteger slideId) {
    Query q = em.createQuery("delete from BtaRecord where slideId.slideId = :slideId");
    q.setParameter("slideId", slideId);
    return q.executeUpdate();
  }

  /*Useful if cascade and ophanRemoval aren't present*/
  @PermitAll
  public void updateWithRecords(BtaShiftInfoSlide slide) {
    deleteBtaRecords(slide.getSlideId());
    edit(slide);
  }
}

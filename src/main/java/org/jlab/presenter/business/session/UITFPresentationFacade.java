package org.jlab.presenter.business.session;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.jlab.presenter.persistence.entity.UITFPresentation;
import org.jlab.presenter.persistence.enumeration.Shift;

/**
 * @author ryans
 */
@Stateless
public class UITFPresentationFacade extends ShiftPresentationFacade<UITFPresentation> {

  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public UITFPresentationFacade() {
    super(UITFPresentation.class);
  }

  @PermitAll
  @Override
  public BigInteger findIdByYmdAndShift(Date ymd, Shift shift) {
    TypedQuery<BigInteger> q =
        em.createNamedQuery("UITFPresentation.findIdByYmdAndShift", BigInteger.class);

    q.setParameter("ymd", ymd);
    q.setParameter("shift", shift);

    List<BigInteger> result = q.getResultList();

    return (result.size() > 0) ? result.get(0) : null;
  }

  @PermitAll
  @Override
  public UITFPresentation findWithSlides(BigInteger id) {
    UITFPresentation p = find(id);

    if (p != null) {
      p.getSlideList()
          .size(); // Touch slides to prompt EM to load them (this is a hack due to missing support
      // in JPA!)
    }

    return p;
  }
}

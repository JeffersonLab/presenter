package org.jlab.presenter.business.session;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.jlab.presenter.persistence.entity.LOPresentation;
import org.jlab.presenter.persistence.enumeration.Shift;

/**
 * @author ryans
 */
@Stateless
public class LOPresentationFacade extends ShiftPresentationFacade<LOPresentation> {

  @PersistenceContext(unitName = "presenterPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public LOPresentationFacade() {
    super(LOPresentation.class);
  }

  @PermitAll
  @Override
  public BigInteger findIdByYmdAndShift(Date ymd, Shift shift) {
    TypedQuery<BigInteger> q =
        em.createNamedQuery("LOPresentation.findIdByYmdAndShift", BigInteger.class);

    q.setParameter("ymd", ymd);
    q.setParameter("shift", shift);

    List<BigInteger> result = q.getResultList();

    return (result.size() > 0) ? result.get(0) : null;
  }

  @PermitAll
  @Override
  public LOPresentation findWithSlides(BigInteger id) {
    LOPresentation p = find(id);

    if (p != null) {
      p.getSlideList()
          .size(); // Touch slides to prompt EM to load them (this is a hack due to missing support
      // in JPA!)
    }

    return p;
  }
}

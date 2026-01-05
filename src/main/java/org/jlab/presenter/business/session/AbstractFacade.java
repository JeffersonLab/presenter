package org.jlab.presenter.business.session;

import jakarta.annotation.security.PermitAll;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * @author ryans
 * @param <T>
 */
public abstract class AbstractFacade<T> {
  private final Class<T> entityClass;

  public AbstractFacade(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  protected abstract EntityManager getEntityManager();

  @PermitAll
  public void create(T entity) {
    getEntityManager().persist(entity);
  }

  @PermitAll
  public T edit(T entity) {
    return getEntityManager().merge(entity);
  }

  @PermitAll
  public void remove(T entity) {
    getEntityManager().remove(getEntityManager().merge(entity));
  }

  @PermitAll
  public T find(Object id) {
    return getEntityManager().find(entityClass, id);
  }

  @PermitAll
  public List<T> findAll() {
    CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
    cq.select(cq.from(entityClass));
    return getEntityManager().createQuery(cq).getResultList();
  }

  @PermitAll
  public List<T> findRange(int[] range) {
    CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
    cq.select(cq.from(entityClass));
    TypedQuery<T> q = getEntityManager().createQuery(cq);
    q.setMaxResults(range[1] - range[0]);
    q.setFirstResult(range[0]);
    return q.getResultList();
  }

  @PermitAll
  public int count() {
    CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
    Root<T> rt = cq.from(entityClass);
    cq.select(getEntityManager().getCriteriaBuilder().count(rt));
    Query q = getEntityManager().createQuery(cq);
    return ((Long) q.getSingleResult()).intValue();
  }
}

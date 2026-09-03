package org.jlab.presenter.business.session;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import org.jlab.presenter.persistence.entity.Team;
import org.jlab.presenter.persistence.entity.TeamStatusReport;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles("presenter-admin")
public class TeamStatusReportFacade extends AbstractFacade<TeamStatusReport> {

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public TeamStatusReportFacade() {
    super(TeamStatusReport.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb,
      CriteriaQuery<? extends Object> cq,
      Root<TeamStatusReport> root,
      Team team) {
    List<Predicate> filters = new ArrayList<>();

    if (team != null) {
      filters.add(cb.equal(root.get("team").get("teamId"), team.getTeamId()));
    }

    return filters;
  }

  @PermitAll
  public List<TeamStatusReport> filterList(Team team, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<TeamStatusReport> cq = cb.createQuery(TeamStatusReport.class);
    Root<TeamStatusReport> root = cq.from(TeamStatusReport.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, cq, root, team);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("ymd");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    Path p1 = root.get("team").get("name");
    Order o1 = cb.asc(p1);
    orders.add(o1);
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }
}

package org.jlab.presenter.business.session;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.jlab.presenter.persistence.entity.Team;
import org.jlab.presenter.persistence.enumeration.Include;
import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.view.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ryans
 */
@Stateless
@DeclareRoles("srm-admin")
public class TeamFacade extends AbstractFacade<Team> {

  @PersistenceContext(unitName = "webappPU")
  private EntityManager em;

  public TeamFacade() {
    super(Team.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @PermitAll
  public Team findTeamWithMembers(BigInteger teamId) {
    Team team = find(teamId);

    if (team != null) {
      UserAuthorizationService userService = UserAuthorizationService.getInstance();
      List<User> userList = userService.getUsersInRole(team.getDirectoryGroupName());
      team.setMembers(userList);
    }

    return team;
  }

  private List<Predicate> getFilters(
      CriteriaBuilder cb,
      CriteriaQuery<? extends Object> cq,
      Root<Team> root,
      Include includeArchived) {
    List<Predicate> filters = new ArrayList<>();

    if (includeArchived == null) {
      filters.add(cb.equal(root.get("archived"), false));
    } else if (Include.EXCLUSIVELY == includeArchived) {
      filters.add(cb.equal(root.get("archived"), true));
    } // else Include.YES, which means don't filter at all

    return filters;
  }

  @PermitAll
  public List<Team> filterList(Include includeArchived, int offset, int max) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Team> cq = cb.createQuery(Team.class);
    Root<Team> root = cq.from(Team.class);
    cq.select(root);

    List<Predicate> filters = getFilters(cb, cq, root, includeArchived);

    if (!filters.isEmpty()) {
      cq.where(cb.and(filters.toArray(new Predicate[] {})));
    }

    List<Order> orders = new ArrayList<>();
    Path p0 = root.get("name");
    Order o0 = cb.asc(p0);
    orders.add(o0);
    cq.orderBy(orders);
    return getEntityManager()
        .createQuery(cq)
        .setFirstResult(offset)
        .setMaxResults(max)
        .getResultList();
  }

  @PermitAll
  public List<Team> findAllWithMembers(Include includeArchived) {
    List<Team> teamList = filterList(includeArchived, 0, Integer.MAX_VALUE);

    for (Team team : teamList) {
      UserAuthorizationService userService = UserAuthorizationService.getInstance();
      List<User> userList = userService.getUsersInRole(team.getDirectoryGroupName());
      team.setMembers(userList);
    }

    return teamList;
  }
}

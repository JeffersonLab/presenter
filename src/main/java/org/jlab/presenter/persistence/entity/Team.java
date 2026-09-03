package org.jlab.presenter.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import org.jlab.smoothness.persistence.util.YnStringToBoolean;
import org.jlab.smoothness.persistence.view.User;

/**
 * @author ryans
 */
@Entity
@Table(name = "TEAM", schema = "PRESENTER_OWNER")
public class Team implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "TeamId", sequenceName = "TEAM_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TeamId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "TEAM_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger teamId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(nullable = false, length = 128)
  private String name;

  @Size(max = 1024)
  @Column(length = 1024)
  private String description;

  @NotNull
  @Column(name = "DIRECTORY_GROUP_NAME", nullable = false, length = 64)
  private String directoryGroupName;

  @Basic(optional = false)
  @Column(name = "WEIGHT", nullable = false, precision = 22, scale = 0)
  private BigInteger weight;

  @Basic
  @Column(name = "ARCHIVED_YN", nullable = false, length = 1)
  @Convert(converter = YnStringToBoolean.class)
  private boolean archived;

  @Transient private List<User> members;

  public Team() {}

  public BigInteger getTeamId() {
    return teamId;
  }

  public void setTeamId(BigInteger teamId) {
    this.teamId = teamId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigInteger getWeight() {
    return weight;
  }

  public void setWeight(BigInteger weight) {
    this.weight = weight;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public String getDirectoryGroupName() {
    return directoryGroupName;
  }

  public void setDirectoryGroupName(String directoryGroupName) {
    this.directoryGroupName = directoryGroupName;
  }

  public List<User> getMembers() {
    return members;
  }

  public void setMembers(List<User> members) {
    this.members = members;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (teamId != null ? teamId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Team)) {
      return false;
    }
    Team other = (Team) object;
    return (this.teamId != null || other.teamId == null)
        && (this.teamId == null || this.teamId.equals(other.teamId));
  }

  @Override
  public String toString() {
    return "org.jlab.presenter.persistence.entity.Team[ teamId=" + teamId + " ]";
  }
}

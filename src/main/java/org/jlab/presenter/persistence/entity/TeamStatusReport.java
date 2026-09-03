package org.jlab.presenter.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "TEAM_STATUS_REPORT", schema = "PRESENTER_OWNER")
public class TeamStatusReport implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(
      name = "TeamStatusReportId",
      sequenceName = "TEAM_STATUS_REPORT_ID",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TeamStatusReportId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "TEAM_STATUS_REPORT_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger teamStatusReportId;

  @JoinColumn(name = "TEAM_ID", referencedColumnName = "TEAM_ID", nullable = false)
  @ManyToOne(optional = false)
  private Team team;

  @Basic(optional = false)
  @Column(name = "YMD", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date ymd;

  @Column(name = "ACCOMPLISHED")
  @Lob
  private String accomplished;

  @Column(name = "IN_PROGRESS")
  @Lob
  private String inProgress;

  @Column(name = "PLANNED")
  @Lob
  private String planned;

  @Column(name = "ROADBLOCKS")
  @Lob
  private String roadblocks;

  public TeamStatusReport() {}

  public BigInteger getTeamStatusReportId() {
    return teamStatusReportId;
  }

  public void setTeamStatusReportId(BigInteger teamStatusReportId) {
    this.teamStatusReportId = teamStatusReportId;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public Date getYmd() {
    return ymd;
  }

  public void setYmd(Date ymd) {
    this.ymd = ymd;
  }

  public String getAccomplished() {
    return accomplished;
  }

  public void setAccomplished(String accomplished) {
    this.accomplished = accomplished;
  }

  public String getInProgress() {
    return inProgress;
  }

  public void setInProgress(String inProgress) {
    this.inProgress = inProgress;
  }

  public String getPlanned() {
    return planned;
  }

  public void setPlanned(String planned) {
    this.planned = planned;
  }

  public String getRoadblocks() {
    return roadblocks;
  }

  public void setRoadblocks(String roadblocks) {
    this.roadblocks = roadblocks;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 73 * hash + (this.teamStatusReportId != null ? this.teamStatusReportId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TeamStatusReport other = (TeamStatusReport) obj;
    return Objects.equals(this.teamStatusReportId, other.teamStatusReportId);
  }

  @Override
  public String toString() {
    return "TeamStatusReport{" + "teamStatusReportId=" + teamStatusReportId + '}';
  }
}

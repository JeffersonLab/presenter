package org.jlab.presenter.persistence.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import org.jlab.presenter.persistence.enumeration.PresentationType;
import org.jlab.presenter.persistence.enumeration.Shift;

/**
 * @author ryans
 */
@Entity
@DiscriminatorValue("LO_PRESENTATION")
@Table(
    name = "UITF_PRESENTATION",
    schema = "PRESENTER_OWNER",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"YMD", "SHIFT"})})
@NamedQueries({
  @NamedQuery(
      name = "UITFPresentation.findIdByYmdAndShift",
      query =
          "SELECT a.presentationId FROM UITFPresentation a WHERE a.ymd = :ymd AND a.shift = :shift")
})
public class UITFPresentation extends Presentation {
  @Basic(optional = false)
  @NotNull
  @Column(name = "YMD", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date ymd;

  @Basic(optional = false)
  @NotNull
  @Column(name = "SHIFT", nullable = false, length = 5)
  @Enumerated(EnumType.STRING)
  private Shift shift;

  {
    setPresentationType(PresentationType.UITF_PRESENTATION);
  }

  public UITFPresentation() {}

  public UITFPresentation(Date ymd, Shift shift) {
    this.ymd = ymd;
    this.shift = shift;
  }

  public Date getYmd() {
    return ymd;
  }

  public void setYmd(Date ymd) {
    this.ymd = ymd;
  }

  public Shift getShift() {
    return shift;
  }

  public void setShift(Shift shift) {
    this.shift = shift;
  }
}

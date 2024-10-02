package org.jlab.presenter.persistence.entity;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.jlab.presenter.persistence.enumeration.PDPresentationType;
import org.jlab.presenter.persistence.enumeration.PresentationType;

/**
 * @author ryans
 */
@Entity
@DiscriminatorValue("PD_PRESENTATION")
@Table(
    name = "PD_PRESENTATION",
    schema = "PRESENTER_OWNER",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"DELIVERY_YMD", "PD_PRESENTATION_TYPE"})})
@NamedQueries({
  @NamedQuery(
      name = "PDPresentation.findIdByYmdAndPDType",
      query =
          "SELECT a.presentationId FROM PDPresentation a WHERE a.deliveryYmd = :ymd AND a.pdPresentationType = :pdType")
})
public class PDPresentation extends Presentation {
  private static final long serialVersionUID = 1L;

  @Basic(optional = false)
  @NotNull
  @Column(name = "DELIVERY_YMD", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date deliveryYmd;

  @Basic(optional = false)
  @NotNull
  @Column(name = "PD_PRESENTATION_TYPE", nullable = false, length = 16)
  @Enumerated(EnumType.STRING)
  private PDPresentationType pdPresentationType;

  @Basic(optional = true)
  @Column(name = "SHIFT_LOG_DAYS", nullable = true)
  private Integer shiftLogDays;

  {
    setPresentationType(PresentationType.PD_PRESENTATION);
  }

  public PDPresentation() {}

  public PDPresentation(Date deliveryYmd, PDPresentationType pdPresentationType) {
    this.deliveryYmd = deliveryYmd;
    this.pdPresentationType = pdPresentationType;
  }

  public Date getDeliveryYmd() {
    return deliveryYmd;
  }

  public void setDeliveryYmd(Date deliveryYmd) {
    this.deliveryYmd = deliveryYmd;
  }

  public PDPresentationType getPdPresentationType() {
    return pdPresentationType;
  }

  public void setPdPresentationType(PDPresentationType pdPresentationType) {
    this.pdPresentationType = pdPresentationType;
  }

  public Integer getShiftLogDays() {
    return shiftLogDays;
  }

  public void setShiftLogDays(Integer shiftLogDays) {
    this.shiftLogDays = shiftLogDays;
  }
}

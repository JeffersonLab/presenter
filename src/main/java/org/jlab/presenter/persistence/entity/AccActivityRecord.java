package org.jlab.presenter.persistence.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author ryans
 */
@Entity
@Table(name = "ACC_ACTIVITY_RECORD", schema = "PRESENTER_OWNER")
public class AccActivityRecord implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(
      name = "AccActivityRecordId",
      sequenceName = "ACC_ACTIVITY_RECORD_ID",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccActivityRecordId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "ACC_ACTIVITY_RECORD_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger accActivityRecordId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "ORDER_ID", nullable = false)
  private Long orderId;

  @Size(max = 36)
  @Column(name = "ACTIVITY_TYPE", length = 36)
  private String activityType;

  @Column(name = "SCHEDULED", precision = 12, scale = 4)
  private Float scheduled;

  @Column(name = "ACTUAL", precision = 12, scale = 4)
  private Float actual;

  @JoinColumn(name = "SLIDE_ID", referencedColumnName = "SLIDE_ID")
  @ManyToOne
  private PdBeamAccSlide slideId;

  public AccActivityRecord() {}

  public AccActivityRecord(BigInteger accActivityRecordId) {
    this.accActivityRecordId = accActivityRecordId;
  }

  public AccActivityRecord copyRecord() {
    AccActivityRecord copy = new AccActivityRecord();

    copy.setActivityType(activityType);
    copy.setActual(actual);
    copy.setScheduled(scheduled);

    return copy;
  }

  public BigInteger getAccActivityRecordId() {
    return accActivityRecordId;
  }

  public void setAccActivityRecordId(BigInteger accActivityRecordId) {
    this.accActivityRecordId = accActivityRecordId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getActivityType() {
    return activityType;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public Float getScheduled() {
    return scheduled;
  }

  public void setScheduled(Float scheduled) {
    this.scheduled = scheduled;
  }

  public Float getActual() {
    return actual;
  }

  public void setActual(Float actual) {
    this.actual = actual;
  }

  public PdBeamAccSlide getSlideId() {
    return slideId;
  }

  public void setSlideId(PdBeamAccSlide slideId) {
    this.slideId = slideId;
  }
}

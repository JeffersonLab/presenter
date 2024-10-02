package org.jlab.presenter.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ryans
 */
@Entity
@Table(name = "ACCESS_RECORD", schema = "PRESENTER_OWNER")
public class AccessRecord implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "AccessRecordId", sequenceName = "ACCESS_RECORD_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccessRecordId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "ACCESS_RECORD_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger accessRecordId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "ORDER_ID", nullable = false)
  private Long orderId;

  @Size(max = 64)
  @Column(name = "ACCESS_TYPE", length = 64)
  private String accessType;

  @Column(name = "HALL_A", precision = 12, scale = 4)
  private Float hallA;

  @Column(name = "HALL_B", precision = 12, scale = 4)
  private Float hallB;

  @Column(name = "HALL_C", precision = 12, scale = 4)
  private Float hallC;

  @Column(name = "HALL_D", precision = 12, scale = 4)
  private Float hallD;

  @Column(name = "ACCEL", precision = 12, scale = 4)
  private Float accel;

  @JoinColumn(name = "SLIDE_ID", referencedColumnName = "SLIDE_ID")
  @ManyToOne
  private PdAccessSlide slideId;

  public AccessRecord() {}

  public AccessRecord(BigInteger accessRecordId) {
    this.accessRecordId = accessRecordId;
  }

  public AccessRecord copyRecord() {
    AccessRecord copy = new AccessRecord();

    copy.setAccel(accel);
    copy.setAccessType(accessType);
    copy.setHallA(hallA);
    copy.setHallB(hallB);
    copy.setHallC(hallC);
    copy.setHallD(hallD);

    return copy;
  }

  public BigInteger getAccessRecordId() {
    return accessRecordId;
  }

  public void setAccessRecordId(BigInteger accessRecordId) {
    this.accessRecordId = accessRecordId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getAccessType() {
    return accessType;
  }

  public void setAccessType(String accessType) {
    this.accessType = accessType;
  }

  public Float getHallA() {
    return hallA;
  }

  public void setHallA(Float hallA) {
    this.hallA = hallA;
  }

  public Float getHallB() {
    return hallB;
  }

  public void setHallB(Float hallB) {
    this.hallB = hallB;
  }

  public Float getHallC() {
    return hallC;
  }

  public void setHallC(Float hallC) {
    this.hallC = hallC;
  }

  public Float getHallD() {
    return hallD;
  }

  public void setHallD(Float hallD) {
    this.hallD = hallD;
  }

  public Float getAccel() {
    return accel;
  }

  public void setAccel(Float accel) {
    this.accel = accel;
  }

  public PdAccessSlide getSlideId() {
    return slideId;
  }

  public void setSlideId(PdAccessSlide slideId) {
    this.slideId = slideId;
  }
}

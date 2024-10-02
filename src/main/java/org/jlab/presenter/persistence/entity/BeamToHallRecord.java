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
@Table(name = "BEAM_TO_HALL_RECORD", schema = "PRESENTER_OWNER")
public class BeamToHallRecord implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(
      name = "BeamToHallRecordId",
      sequenceName = "BEAM_TO_HALL_RECORD_ID",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BeamToHallRecordId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "BEAM_TO_HALL_RECORD_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger beamToHallRecordId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "ORDER_ID", nullable = false)
  private Long orderId;

  @Size(max = 12)
  @Column(name = "HALL", length = 12)
  private String hall;

  @Column(name = "SCHEDULED", precision = 12, scale = 4)
  private Float scheduled;

  @Column(name = "ACC_AVAIL", precision = 12, scale = 4)
  private Float accAvail;

  @Column(name = "ACCEPT", precision = 12, scale = 4)
  private Float accept;

  @Column(name = "HALL_AVAIL", precision = 12, scale = 4)
  private Float hallAvail;

  @Column(name = "ACTUAL", precision = 12, scale = 4)
  private Float actual;

  @JoinColumn(name = "SLIDE_ID", referencedColumnName = "SLIDE_ID")
  @ManyToOne
  private PdBeamAccSlide slideId;

  public BeamToHallRecord() {}

  public BeamToHallRecord(BigInteger beamToHallRecordId) {
    this.beamToHallRecordId = beamToHallRecordId;
  }

  public BeamToHallRecord copyRecord() {
    BeamToHallRecord copy = new BeamToHallRecord();

    copy.setAccAvail(accAvail);
    copy.setAccept(accept);
    copy.setHall(hall);
    copy.setHallAvail(hallAvail);
    copy.setScheduled(scheduled);

    return copy;
  }

  public BigInteger getBeamToHallRecordId() {
    return beamToHallRecordId;
  }

  public void setBeamToHallRecordId(BigInteger beamToHallRecordId) {
    this.beamToHallRecordId = beamToHallRecordId;
  }

  public Float getActual() {
    return actual;
  }

  public void setActual(Float actual) {
    this.actual = actual;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getHall() {
    return hall;
  }

  public void setHall(String hall) {
    this.hall = hall;
  }

  public Float getScheduled() {
    return scheduled;
  }

  public void setScheduled(Float scheduled) {
    this.scheduled = scheduled;
  }

  public Float getAccAvail() {
    return accAvail;
  }

  public void setAccAvail(Float accAvail) {
    this.accAvail = accAvail;
  }

  public Float getAccept() {
    return accept;
  }

  public void setAccept(Float accept) {
    this.accept = accept;
  }

  public Float getHallAvail() {
    return hallAvail;
  }

  public void setHallAvail(Float hallAvail) {
    this.hallAvail = hallAvail;
  }

  public PdBeamAccSlide getSlideId() {
    return slideId;
  }

  public void setSlideId(PdBeamAccSlide slideId) {
    this.slideId = slideId;
  }
}

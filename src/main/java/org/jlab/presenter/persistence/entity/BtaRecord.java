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
@Table(name = "BTA_RECORD", schema = "PRESENTER_OWNER")
public class BtaRecord implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "BtaRecordId", sequenceName = "BTA_RECORD_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BtaRecordId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "BTA_RECORD_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger btaRecordId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "ORDER_ID", nullable = false)
  private Long orderId;

  @Size(max = 75)
  @Column(name = "HALL_PROGRAM", length = 75)
  private String hallProgram;

  @Column(name = "SCHEDULED")
  private Float scheduled;

  @Column(name = "ACTUAL")
  private Float actual;

  @Column(name = "ABU")
  private Float abu;

  @Column(name = "BANU")
  private Float banu;

  @JoinColumn(name = "SLIDE_ID", referencedColumnName = "SLIDE_ID")
  @ManyToOne
  private BtaShiftInfoSlide slideId;

  public BtaRecord() {}

  public BtaRecord(BigInteger btaRecordId) {
    this.btaRecordId = btaRecordId;
  }

  public BtaRecord(BigInteger btaRecordId, Long orderId) {
    this.btaRecordId = btaRecordId;
    this.orderId = orderId;
  }

  public BtaRecord(String hallProgram, Float scheduled, Float actual, Float abu, Float banu) {
    this.hallProgram = hallProgram;
    this.scheduled = scheduled;
    this.actual = actual;
    this.abu = abu;
    this.banu = banu;
  }

  public BtaRecord copyRecord() {
    BtaRecord copy = new BtaRecord();

    copy.setHallProgram(hallProgram);
    copy.setScheduled(scheduled);
    copy.setActual(actual);
    copy.setAbu(abu);
    copy.setBanu(banu);

    return copy;
  }

  public BigInteger getBtaRecordId() {
    return btaRecordId;
  }

  public void setBtaRecordId(BigInteger btaRecordId) {
    this.btaRecordId = btaRecordId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getHallProgram() {
    return hallProgram;
  }

  public void setHallProgram(String hallProgram) {
    this.hallProgram = hallProgram;
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

  public Float getAbu() {
    return abu;
  }

  public void setAbu(Float abu) {
    this.abu = abu;
  }

  public Float getBanu() {
    return banu;
  }

  public void setBanu(Float banu) {
    this.banu = banu;
  }

  public BtaShiftInfoSlide getSlideId() {
    return slideId;
  }

  public void setSlideId(BtaShiftInfoSlide slideId) {
    this.slideId = slideId;
  }

  @Override
  public String toString() {
    String builder =
        "HallProgram: "
            + hallProgram
            + ", Scheduled: "
            + scheduled
            + ", Actual: "
            + actual
            + ", ABU: "
            + abu
            + ", BANU: "
            + banu;
    return builder;
  }
}

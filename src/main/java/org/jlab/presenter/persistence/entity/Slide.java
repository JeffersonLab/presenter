package org.jlab.presenter.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.jlab.presenter.persistence.enumeration.PresentationType;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 * @author ryans
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
/*Note: Hibernate doesn't like a DiscriminatorColumn in combination with Inheritance type JOINED.  Use property hibernate.discriminator.ignore_explicit_for_joined to make this work.  See HHH-6911*/
@DiscriminatorColumn(name = "SLIDE_TYPE", discriminatorType = DiscriminatorType.STRING, length = 64)
@DiscriminatorValue("SLIDE")
@Table(name = "SLIDE", schema = "PRESENTER_OWNER")
@NamedQueries({
  @NamedQuery(name = "Slide.delete", query = "DELETE FROM Slide a WHERE a.slideId = :slideId")
})
public class Slide implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SlideId", sequenceName = "SLIDE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SlideId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "SLIDE_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger slideId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "ORDER_ID", nullable = false)
  private Long orderId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "SLIDE_TYPE", nullable = false, length = 64)
  @Enumerated(EnumType.STRING)
  private SlideType slideType;

  @Column(name = "SYNC_FROM_SLIDE_ID")
  private BigInteger syncFromSlideId;

  @JoinColumn(name = "PRESENTATION_ID", referencedColumnName = "PRESENTATION_ID", nullable = false)
  @ManyToOne(optional = false)
  private Presentation presentation;

  @Transient private BigInteger syncFromPresentationId;
  @Transient private PresentationType syncFromPresentationType;

  @Basic(optional = true)
  @Column(name = "LABEL", nullable = true, length = 64)
  protected String label;

  {
    setSlideType(SlideType.SLIDE);
  }

  public Slide() {}

  public Slide(BigInteger slideId) {
    this.slideId = slideId;
  }

  public Slide(BigInteger slideId, Long orderId, SlideType slideType) {
    this.slideId = slideId;
    this.orderId = orderId;
    this.slideType = slideType;
  }

  public Slide copySlide() {
    Slide copy = new Slide();
    copy.setSlideType(slideType);
    copy.setSyncFromSlideId(slideId);
    return copy;
  }

  public void mergeSlide(Slide other) {
    this.slideType = other.getSlideType();
  }

  public BigInteger getSyncFromPresentationId() {
    return syncFromPresentationId;
  }

  public void setSyncFromPresentationId(BigInteger syncFromPresentationId) {
    this.syncFromPresentationId = syncFromPresentationId;
  }

  public PresentationType getSyncFromPresentationType() {
    return syncFromPresentationType;
  }

  public void setSyncFromPresentationType(PresentationType syncFromPresentationType) {
    this.syncFromPresentationType = syncFromPresentationType;
  }

  public BigInteger getSlideId() {
    return slideId;
  }

  public void setSlideId(BigInteger slideId) {
    this.slideId = slideId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public SlideType getSlideType() {
    return slideType;
  }

  public void setSlideType(SlideType slideType) {
    this.slideType = slideType;
  }

  public Presentation getPresentation() {
    return presentation;
  }

  public void setPresentation(Presentation presentation) {
    this.presentation = presentation;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public BigInteger getSyncFromSlideId() {
    return syncFromSlideId;
  }

  public void setSyncFromSlideId(BigInteger syncFromSlideId) {
    this.syncFromSlideId = syncFromSlideId;
  }

  @Override
  public int hashCode() {
    int hash = 3;
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
    final Slide other = (Slide) obj;
    return Objects.equals(this.slideId, other.slideId);
  }

  @Override
  public String toString() {
    return "Order: "
        + orderId
        + ", Type: "
        + slideType.name()
        + ", Label: "
        + label
        + ", Presentation ID: "
        + presentation.getPresentationId()
        + ", Slide ID: "
        + slideId;
  }
}

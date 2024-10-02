package org.jlab.presenter.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author ryans
 */
@Entity
@Table(name = "PRESENTATION_LOG", schema = "PRESENTER_OWNER")
@NamedQueries({
  @NamedQuery(name = "PresentationLog.findAll", query = "SELECT p FROM PresentationLog p"),
  @NamedQuery(
      name = "PresentationLog.findByPresentationId",
      query =
          "SELECT p FROM PresentationLog p WHERE p.presentationLogPK.presentationId = :presentationId"),
  @NamedQuery(
      name = "PresentationLog.findByLogId",
      query = "SELECT p FROM PresentationLog p WHERE p.presentationLogPK.logId = :logId"),
  @NamedQuery(
      name = "PresentationLog.findByUsername",
      query = "SELECT p FROM PresentationLog p WHERE p.presentationLogPK.username = :username"),
  @NamedQuery(
      name = "PresentationLog.findByUsernameAndPresentationId",
      query =
          "SELECT p FROM PresentationLog p WHERE p.presentationLogPK.username = :username AND p.presentationLogPK.presentationId = :presentationId")
})
public class PresentationLog implements Serializable {
  private static final long serialVersionUID = 1L;
  @EmbeddedId protected PresentationLogPK presentationLogPK;

  @JoinColumn(
      name = "PRESENTATION_ID",
      referencedColumnName = "PRESENTATION_ID",
      nullable = false,
      insertable = false,
      updatable = false)
  @ManyToOne(optional = false)
  private Presentation presentation;

  public PresentationLog() {}

  public PresentationLog(PresentationLogPK presentationLogPK) {
    this.presentationLogPK = presentationLogPK;
  }

  public PresentationLog(BigInteger presentationId, BigInteger logId, String username) {
    this.presentationLogPK = new PresentationLogPK(presentationId, logId, username);
  }

  public PresentationLogPK getPresentationLogPK() {
    return presentationLogPK;
  }

  public void setPresentationLogPK(PresentationLogPK presentationLogPK) {
    this.presentationLogPK = presentationLogPK;
  }

  public Presentation getPresentation() {
    return presentation;
  }

  public void setPresentation(Presentation presentation) {
    this.presentation = presentation;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (presentationLogPK != null ? presentationLogPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof PresentationLog)) {
      return false;
    }
    PresentationLog other = (PresentationLog) object;
    return (this.presentationLogPK != null || other.presentationLogPK == null)
        && (this.presentationLogPK == null
            || this.presentationLogPK.equals(other.presentationLogPK));
  }

  @Override
  public String toString() {
    return "org.jlab.presenter.business.session.PresentationLog[ presentationLogPK="
        + presentationLogPK
        + " ]";
  }
}

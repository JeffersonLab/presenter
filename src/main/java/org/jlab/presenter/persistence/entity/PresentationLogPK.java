package org.jlab.presenter.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ryans
 */
@Embeddable
public class PresentationLogPK implements Serializable {
  @Basic(optional = false)
  @NotNull
  @Column(name = "PRESENTATION_ID", nullable = false)
  private BigInteger presentationId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "LOG_ID", nullable = false)
  private BigInteger logId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 32)
  @Column(nullable = false, length = 32)
  private String username;

  public PresentationLogPK() {}

  public PresentationLogPK(BigInteger presentationId, BigInteger logId, String username) {
    this.presentationId = presentationId;
    this.logId = logId;
    this.username = username;
  }

  public BigInteger getPresentationId() {
    return presentationId;
  }

  public void setPresentationId(BigInteger presentationId) {
    this.presentationId = presentationId;
  }

  public BigInteger getLogId() {
    return logId;
  }

  public void setLogId(BigInteger logId) {
    this.logId = logId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (presentationId != null ? presentationId.hashCode() : 0);
    hash += (logId != null ? logId.hashCode() : 0);
    hash += (username != null ? username.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof PresentationLogPK)) {
      return false;
    }
    PresentationLogPK other = (PresentationLogPK) object;
    if ((this.presentationId == null && other.presentationId != null)
        || (this.presentationId != null && !this.presentationId.equals(other.presentationId))) {
      return false;
    }
    if ((this.logId == null && other.logId != null)
        || (this.logId != null && !this.logId.equals(other.logId))) {
      return false;
    }
    return (this.username != null || other.username == null)
        && (this.username == null || this.username.equals(other.username));
  }

  @Override
  public String toString() {
    return "org.jlab.presenter.business.session.PresentationLogPK[ presentationId="
        + presentationId
        + ", logId="
        + logId
        + ", username="
        + username
        + " ]";
  }
}

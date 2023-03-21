package org.jlab.presenter.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.enumeration.PresentationType;

/**
 *
 * @author ryans
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
/*Note: Hibernate doesn't like a DiscriminatorColumn in combination with Inheritance type JOINED.  Use property hibernate.discriminator.ignore_explicit_for_joined to make this work.  See HHH-6911*/
@DiscriminatorColumn(name = "PRESENTATION_TYPE", discriminatorType = DiscriminatorType.STRING, length
        = 24)
@DiscriminatorValue("PRESENTATION")
@Table(name = "PRESENTATION", schema = "PRESENTER_OWNER")
@NamedQueries({
    @NamedQuery(name = "Presentation.delete", query
            = "DELETE FROM Presentation a WHERE a.presentationId = :presentationId")})
public class Presentation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "PresentationId", sequenceName = "PRESENTATION_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PresentationId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRESENTATION_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger presentationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRESENTATION_TYPE", nullable = false, length = 24)
    @Enumerated(EnumType.STRING)
    private PresentationType presentationType;
    @Column(name = "LAST_MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
    @Column(name = "LAST_USERNAME")
    @Basic(optional = true)
    private String lastUsername;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "presentation", fetch = FetchType.LAZY)
    @OrderBy("orderId asc")
    private List<Slide> slideList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "presentation")
    private List<PresentationLog> presentationLogList;

    {
        setPresentationType(PresentationType.PRESENTATION);
        lastModified = TimeUtil.nowWithoutMillis();
        
        /*System.err.println("New Last Modified: " + lastModified.getTime());*/
    }

    public Presentation() {
    }

    public Presentation(BigInteger presentationId) {
        this.presentationId = presentationId;
    }

    public Presentation(BigInteger presentationId, PresentationType presentationType) {
        this.presentationId = presentationId;
        this.presentationType = presentationType;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastUsername() {
        return lastUsername;
    }

    public void setLastUsername(String lastUsername) {
        this.lastUsername = lastUsername;
    }

    public BigInteger getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(BigInteger presentationId) {
        this.presentationId = presentationId;
    }

    public PresentationType getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(PresentationType presentationType) {
        this.presentationType = presentationType;
    }

    public List<Slide> getSlideList() {
        return slideList;
    }

    public void setSlideList(List<Slide> slideList) {
        this.slideList = slideList;
    }

    public List<PresentationLog> getPresentationLogList() {
        return presentationLogList;
    }

    public void setPresentationLogList(List<PresentationLog> presentationLogList) {
        this.presentationLogList = presentationLogList;
    }
}

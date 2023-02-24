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
import org.jlab.presenter.persistence.enumeration.PresentationType;
import org.jlab.presenter.persistence.enumeration.Shift;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("LASO_PRESENTATION")
@Table(name = "LASO_PRESENTATION", schema = "PRESENTER_OWNER", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"YMD", "SHIFT"})})
@NamedQueries({
    @NamedQuery(name = "LASOPresentation.findIdByYmdAndShift", query = "SELECT a.presentationId FROM LASOPresentation a WHERE a.ymd = :ymd AND a.shift = :shift")})
public class LASOPresentation extends Presentation {
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
        setPresentationType(PresentationType.LASO_PRESENTATION);
    }       
    
    public LASOPresentation() {
    }

    public LASOPresentation(Date ymd, Shift shift) {
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

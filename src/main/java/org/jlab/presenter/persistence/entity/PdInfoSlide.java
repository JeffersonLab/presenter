package org.jlab.presenter.persistence.entity;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.enumeration.PdInfoSlideType;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("PD_INFO_SLIDE")
@Table(name = "PD_INFO_SLIDE", schema = "PRESENTER_OWNER")
public class PdInfoSlide extends Slide {

    @Size(max = 64)
    @Column(name = "PD", length = 64)
    private String pd;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FROM_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TO_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDate;
    @Lob
    @Column(name = "BODY")
    private String body;
    @Column(name = "PD_INFO_SLIDE_TYPE", length = 24, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private PdInfoSlideType pdInfoSlideType;

    {
        setSlideType(SlideType.PD_INFO_SLIDE);
    }

    public PdInfoSlide() {
        this(PdInfoSlideType.FIRST_SUMMARY);
    }

    public PdInfoSlide(PdInfoSlideType pdInfoSlideType) {
        this(pdInfoSlideType, new Date(), TimeUtil.getOneWeekLater(new Date()));
    }
    
    public PdInfoSlide(PdInfoSlideType pdInfoSlideType, Date fromDate, Date toDate) {
        this.pdInfoSlideType = pdInfoSlideType;
        this.fromDate = fromDate;
        this.toDate = toDate;

        switch (pdInfoSlideType) {
            case FIRST_SUMMARY:
                body =
                        "                        <h2>PROGRAM (upcoming week)</h2>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul>    \n"
                        + "                        <h3>Hall With Beam Delivery Priority</h3>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul> \n"
                        + "                        <h2>PROGRAM GOALS (upcoming week)</h2>\n"
                        + "                        <h3>Must:</h3>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul>\n"
                        + "                        <h3>Should:</h3>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul>\n"
                        + "                        <h3>Like:</h3>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul>";
                setLabel("PD Summary Type I");
                break;
            case SECOND_SUMMARY:
                body =
                        "                        <h2>PROGRAM (last week)</h2>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul>    \n"
                        + "                        <h2>PROGRAM GOALS (last week)</h2>\n"
                        + "                        <h3>Must:</h3>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul>\n"
                        + "                        <h3>Should:</h3>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul>\n"
                        + "                        <h3>Like:</h3>\n"
                        + "                        <ul>\n"
                        + "                            <li></li>\n"
                        + "                        </ul>";
                setLabel("PD Summary Type II #1");
                break;
        }
    }

    @Override
    public Slide copySlide() {
        PdInfoSlide copy = new PdInfoSlide();
        
        copy.setBody(getBody());
        copy.setFromDate(getFromDate());
        copy.setToDate(getToDate());
        copy.setPd(getPd());
        copy.setPdInfoSlideType(getPdInfoSlideType());
        copy.setSlideType(getSlideType());
        copy.setSyncFromSlideId(getSlideId());
        
        return copy;
    }
    
    @Override
    public void mergeSlide(Slide other) {
        if (other instanceof PdInfoSlide) {
            PdInfoSlide slide = (PdInfoSlide) other;
            this.setBody(slide.getBody());
            this.setFromDate(slide.getFromDate());
            this.setToDate(slide.getToDate());
            this.setPd(slide.getPd());
        }
    }    
    
    public String getPd() {
        return pd;
    }

    public void setPd(String pd) {
        this.pd = pd;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public PdInfoSlideType getPdInfoSlideType() {
        return pdInfoSlideType;
    }

    public void setPdInfoSlideType(PdInfoSlideType pdInfoSlideType) {
        this.pdInfoSlideType = pdInfoSlideType;
    }
}

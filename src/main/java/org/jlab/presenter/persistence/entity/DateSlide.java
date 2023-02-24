package org.jlab.presenter.persistence.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.presenter.persistence.enumeration.DateSlideType;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("DATE_SLIDE")
@Table(name = "DATE_SLIDE", schema = "PRESENTER_OWNER")
public class DateSlide extends Slide {

    @Column(name = "SLIDE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date slideDate;
    @Size(max = 24)
    @Column(name = "DATE_SLIDE_TYPE", length = 24, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private DateSlideType dateSlideType;

    {
        setSlideType(SlideType.DATE_SLIDE);
    }

    public DateSlide() {
        this(DateSlideType.CALENDAR);
    }

    public DateSlide(DateSlideType dateSlideType) {
        this.dateSlideType = dateSlideType;
    }

    @Override
    public Slide copySlide() {
        DateSlide copy = new DateSlide();

        copy.setDateSlideType(getDateSlideType());
        copy.setSlideDate(getSlideDate());
        copy.setSlideType(getSlideType());
        copy.setSyncFromSlideId(getSlideId());

        return copy;
    }

    @Override
    public void mergeSlide(Slide other) {
        if (other instanceof DateSlide) {
            DateSlide slide = (DateSlide) other;
            this.setSlideDate(slide.getSlideDate());
        }
    }       
    
    public Date getSlideDate() {
        return slideDate;
    }

    public void setSlideDate(Date slideDate) {
        this.slideDate = slideDate;
    }

    public DateSlideType getDateSlideType() {
        return dateSlideType;
    }

    public void setDateSlideType(DateSlideType dateSlideType) {
        this.dateSlideType = dateSlideType;
    }

    public String getIFrameUrl() {
        String url = null;

        switch (dateSlideType) {
            case CALENDAR:
                url = "https://accweb.acc.jlab.org/calendar";
                break;
            case WORKMAP:
                url = "https://accweb.acc.jlab.org/workmap";
                break;
        }

        return url;
    }
}

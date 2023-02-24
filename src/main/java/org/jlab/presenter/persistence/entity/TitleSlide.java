package org.jlab.presenter.persistence.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("TITLE_SLIDE")
@Table(name = "TITLE_SLIDE", schema = "PRESENTER_OWNER")
public class TitleSlide extends Slide {

    @Size(max = 128)
    @Column(name = "TITLE", length = 128)
    private String title;

    {
        setSlideType(SlideType.TITLE_SLIDE);
    }

    public TitleSlide() {
    }

    @Override
    public Slide copySlide() {
        TitleSlide copy = new TitleSlide();

        copy.setSlideType(getSlideType());
        copy.setTitle(getTitle());
        copy.setSyncFromSlideId(getSlideId());

        return copy;
    }

    @Override
    public void mergeSlide(Slide other) {
        if (other instanceof TitleSlide) {
            TitleSlide slide = (TitleSlide) other;
            this.setTitle(slide.getTitle());
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

package org.jlab.presenter.persistence.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.jlab.presenter.persistence.enumeration.BodySlideType;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("BODY_SLIDE")
@Table(name = "BODY_SLIDE", schema = "PRESENTER_OWNER")
public class BodySlide extends Slide {

    private static final long serialVersionUID = 1L;
    @Lob
    @Column(name = "BODY")
    private String body;
    @Column(name = "BODY_SLIDE_TYPE", length = 24, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private BodySlideType bodySlideType;

    {
        setSlideType(SlideType.BODY_SLIDE);
    }

    public BodySlide() {
        this(BodySlideType.SHIFT_OVERFLOW);
    }

    public BodySlide(BodySlideType bodySlideType) {
        this.bodySlideType = bodySlideType;

        switch (bodySlideType) {
            case PD_SUMMARY_PART_FOUR:
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
                setLabel("PD Summary Type II #4");
                break;
            case PD_SUMMARY_OVERFLOW:
                setLabel("PD Summary Overflow");
                break;
            case SHIFT_OVERFLOW:
                setLabel("Shift Log Overflow");
                break;
        }
    }

    @Override
    public Slide copySlide() {
        BodySlide copy = new BodySlide();

        copy.setBody(body);
        copy.setBodySlideType(bodySlideType);
        copy.setSlideType(getSlideType());
        copy.setSyncFromSlideId(getSlideId());

        return copy;
    }

    @Override
    public void mergeSlide(Slide other) {
        if (other instanceof BodySlide) {
            BodySlide slide = (BodySlide) other;
            this.setBody(slide.getBody());
        }
    }       
    
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public BodySlideType getBodySlideType() {
        return bodySlideType;
    }

    public void setBodySlideType(BodySlideType bodySlideType) {
        this.bodySlideType = bodySlideType;
    }
}

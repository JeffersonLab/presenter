package org.jlab.presenter.persistence.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("IFrameSlide")
@Table(name = "IFRAME_SLIDE", schema = "PRESENTER_OWNER")
public class IFrameSlide extends Slide {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "IFRAME_URL", nullable = false, length = 1024)
    private String iframeUrl;

    {
        setSlideType(SlideType.IFRAME_SLIDE);
    }

    public IFrameSlide() {
        this("about:blank", "Embedded Webpage");
    }

    public IFrameSlide(String iframeUrl, String label) {
        this.iframeUrl = iframeUrl;
        this.label = label;
    }

    @Override
    public Slide copySlide() {
        IFrameSlide copy = new IFrameSlide();

        copy.setIframeUrl(getIframeUrl());
        copy.setSlideType(getSlideType());
        copy.setSyncFromSlideId(getSlideId());
        copy.setLabel(getLabel());

        return copy;
    }

    @Override
    public void mergeSlide(Slide other) {
        if (other instanceof IFrameSlide) {
            IFrameSlide slide = (IFrameSlide) other;
            this.setIframeUrl(slide.getIframeUrl());
        }
    }       

    public String getIframeUrl() {
        return iframeUrl;
    }

    public void setIframeUrl(String iframeUrl) {
        this.iframeUrl = iframeUrl;
    }
}

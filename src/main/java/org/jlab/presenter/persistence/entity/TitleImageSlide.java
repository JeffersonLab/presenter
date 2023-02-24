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
@DiscriminatorValue("TITLE_IMAGE_SLIDE")
@Table(name = "TITLE_IMAGE_SLIDE", schema = "PRESENTER_OWNER")
public class TitleImageSlide extends ImageSlide {
    @Size(max = 128)
    @Column(name = "TITLE", length = 128)
    private String title;

    {
        setSlideType(SlideType.TITLE_IMAGE_SLIDE);
    }    
    
    public TitleImageSlide() {     
        setLabel("Titled Image");
    }

    @Override
    public Slide copySlide() {
        TitleImageSlide copy = new TitleImageSlide();
        
        copy.setImageUrl(getImageUrl());
        copy.setSlideType(getSlideType());
        copy.setTitle(getTitle());
        copy.setSyncFromSlideId(getSlideId());
        
        return copy;
    }
    
    @Override
    public void mergeSlide(Slide other) {
        if (other instanceof TitleImageSlide) {
            TitleImageSlide slide = (TitleImageSlide) other;
            this.setImageUrl(slide.getImageUrl());
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

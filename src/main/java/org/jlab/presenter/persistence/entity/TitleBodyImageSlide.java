package org.jlab.presenter.persistence.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 * @author ryans
 */
@Entity
@DiscriminatorValue("TITLE_BODY_IMAGE_SLIDE")
@Table(name = "TITLE_BODY_IMAGE_SLIDE", schema = "PRESENTER_OWNER")
public class TitleBodyImageSlide extends TitleImageSlide {

  @Lob
  @Column(name = "BODY")
  private String body;

  {
    setSlideType(SlideType.TITLE_BODY_IMAGE_SLIDE);
  }

  public TitleBodyImageSlide() {
    body = "<ul><li></li></ul>";
    setLabel("Titled Text Image");
  }

  @Override
  public Slide copySlide() {
    TitleBodyImageSlide copy = new TitleBodyImageSlide();

    copy.setBody(getBody());
    copy.setImageUrl(getImageUrl());
    copy.setSlideType(getSlideType());
    copy.setTitle(getTitle());
    copy.setSyncFromSlideId(getSlideId());

    return copy;
  }

  @Override
  public void mergeSlide(Slide other) {
    if (other instanceof TitleBodyImageSlide) {
      TitleBodyImageSlide slide = (TitleBodyImageSlide) other;
      this.setBody(slide.getBody());
      this.setImageUrl(slide.getImageUrl());
      this.setTitle(slide.getTitle());
    }
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}

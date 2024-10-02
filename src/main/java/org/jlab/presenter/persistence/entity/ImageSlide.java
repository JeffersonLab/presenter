package org.jlab.presenter.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 * @author ryans
 */
@Entity
@Table(name = "IMAGE_SLIDE", schema = "PRESENTER_OWNER")
public class ImageSlide extends Slide {
  private static final long serialVersionUID = 1L;

  @Lob
  @Column(name = "IMAGE_URL")
  private String imageUrl;

  {
    setSlideType(SlideType.IMAGE_SLIDE);
  }

  public ImageSlide() {
    imageUrl = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";
    setLabel("Image");
  }

  @Override
  public Slide copySlide() {
    ImageSlide copy = new ImageSlide();

    copy.setImageUrl(getImageUrl());
    copy.setSlideType(getSlideType());
    copy.setSyncFromSlideId(getSlideId());

    return copy;
  }

  @Override
  public void mergeSlide(Slide other) {
    if (other instanceof ImageSlide) {
      ImageSlide slide = (ImageSlide) other;
      this.setImageUrl(slide.getImageUrl());
    }
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}

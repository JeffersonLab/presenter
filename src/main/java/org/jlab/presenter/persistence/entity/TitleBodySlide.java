package org.jlab.presenter.persistence.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.presenter.persistence.enumeration.SlideType;
import org.jlab.presenter.persistence.enumeration.TitleBodySlideType;

/**
 * @author ryans
 */
@Entity
@DiscriminatorValue("TITLE_BODY_SLIDE")
@Table(name = "TITLE_BODY_SLIDE", schema = "PRESENTER_OWNER")
public class TitleBodySlide extends Slide {

  @Size(max = 128)
  @Column(name = "TITLE", length = 128)
  private String title;

  @Lob
  @Column(name = "BODY")
  private String body;

  @Column(name = "TITLE_BODY_SLIDE_TYPE", length = 24, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private TitleBodySlideType titleBodySlideType;

  {
    setSlideType(SlideType.TITLE_BODY_SLIDE);
  }

  public TitleBodySlide() {
    this(TitleBodySlideType.SINGLE_COLUMN);
  }

  public TitleBodySlide(TitleBodySlideType titleBodySlideType) {
    this.titleBodySlideType = titleBodySlideType;

    switch (titleBodySlideType) {
      case SINGLE_COLUMN:
        body = "<ul><li></li></ul>";
        setLabel("Titled Text");
        break;
      case DYNAMIC_TWO_COLUMN:
        setLabel("Titled Dynamic Two Columns");
        break;
    }
  }

  @Override
  public Slide copySlide() {
    TitleBodySlide copy = new TitleBodySlide();

    copy.setBody(getBody());
    copy.setSlideType(getSlideType());
    copy.setTitle(getTitle());
    copy.setTitleBodySlideType(getTitleBodySlideType());
    copy.setSyncFromSlideId(getSlideId());

    return copy;
  }

  @Override
  public void mergeSlide(Slide other) {
    if (other instanceof TitleBodySlide) {
      TitleBodySlide slide = (TitleBodySlide) other;
      this.setTitle(slide.getTitle());
      this.setBody(slide.getBody());
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public TitleBodySlideType getTitleBodySlideType() {
    return titleBodySlideType;
  }

  public void setTitleBodySlideType(TitleBodySlideType titleBodySlideType) {
    this.titleBodySlideType = titleBodySlideType;
  }
}

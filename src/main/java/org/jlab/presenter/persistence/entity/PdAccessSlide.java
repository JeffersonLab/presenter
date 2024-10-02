package org.jlab.presenter.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 * @author ryans
 */
@Entity
@DiscriminatorValue("PD_ACCESS_SLIDE")
@Table(name = "PD_ACCESS_SLIDE", schema = "PRESENTER_OWNER")
public class PdAccessSlide extends Slide {

  @Lob
  @Column(name = "BODY")
  private String body;

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(mappedBy = "slideId", orphanRemoval = true, cascade = CascadeType.ALL)
  @OrderBy("orderId asc")
  private List<AccessRecord> accessRecordList;

  {
    setSlideType(SlideType.PD_ACCESS_SLIDE);

    accessRecordList = new ArrayList<AccessRecord>();

    String[] types = {
      "PCC - Planned Configuration Change", "Repair/Investigate", "Opportunistic", "Other"
    };
    for (int i = 0; i < types.length; i++) {
      AccessRecord access = new AccessRecord();
      access.setSlideId(this);
      access.setOrderId(Long.valueOf(i + 1));
      access.setAccessType(types[i]);
      access.setHallA(0f);
      access.setHallB(0f);
      access.setHallC(0f);
      access.setHallD(0f);
      access.setAccel(0f);
      accessRecordList.add(access);
    }

    body =
        "                        <h2>TEST PLANS COMPLETED</h2>\n"
            + "                        <ul>\n"
            + "                            <li></li>\n"
            + "                        </ul>    \n"
            + "                        <h2>SIGNIFICANT ACHIEVEMENTS</h2>\n"
            + "                        <ul>\n"
            + "                            <li></li>\n"
            + "                        </ul>\n"
            + "                        <h2>SIGNIFICANT MAINTENANCE/MACHINE DEVELOPMENT ACTIVITIES</h2>\n"
            + "                        <ul>\n"
            + "                            <li></li>\n"
            + "                        </ul>\n"
            + "                        <h2>SIGNIFICANT PROBLEMS PENDING</h2>\n"
            + "                        <ul>\n"
            + "                            <li></li>\n"
            + "                        </ul>";
    setLabel("PD Summary Type II #3");
  }

  public PdAccessSlide() {}

  @Override
  public Slide copySlide() {
    PdAccessSlide copy = new PdAccessSlide();

    List<AccessRecord> copyOfAccessRecordList = new ArrayList<AccessRecord>();

    long order = 1;

    for (AccessRecord record : getAccessRecordList()) {
      AccessRecord copyOfRecord = record.copyRecord();
      copyOfRecord.setSlideId(copy);
      copyOfRecord.setOrderId(order++);

      copyOfAccessRecordList.add(copyOfRecord);
    }

    copy.setAccessRecordList(copyOfAccessRecordList);
    copy.setBody(getBody());
    copy.setSlideType(getSlideType());
    copy.setSyncFromSlideId(getSlideId());

    return copy;
  }

  @Override
  public void mergeSlide(Slide other) {
    if (other instanceof PdAccessSlide) {
      PdAccessSlide slide = (PdAccessSlide) other;
      this.setBody(slide.getBody());

      long order = 1;

      this.getAccessRecordList().clear();

      for (AccessRecord record : slide.getAccessRecordList()) {
        AccessRecord copyOfRecord = record.copyRecord();
        copyOfRecord.setSlideId(this);
        copyOfRecord.setOrderId(order++);

        this.getAccessRecordList().add(copyOfRecord);
      }
    }
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public List<AccessRecord> getAccessRecordList() {
    return accessRecordList;
  }

  public void setAccessRecordList(List<AccessRecord> accessRecordList) {
    this.accessRecordList = accessRecordList;
  }

  public Float getHallATotal() {
    float total = 0f;
    for (AccessRecord record : accessRecordList) {
      total = total + ((record.getHallA() == null) ? 0f : record.getHallA());
    }
    return total;
  }

  public Float getHallBTotal() {
    float total = 0f;
    for (AccessRecord record : accessRecordList) {
      total = total + ((record.getHallB() == null) ? 0f : record.getHallB());
    }
    return total;
  }

  public Float getHallCTotal() {
    float total = 0f;
    for (AccessRecord record : accessRecordList) {
      total = total + ((record.getHallC() == null) ? 0f : record.getHallC());
    }
    return total;
  }

  public Float getHallDTotal() {
    float total = 0f;
    for (AccessRecord record : accessRecordList) {
      total = total + ((record.getHallD() == null) ? 0f : record.getHallD());
    }
    return total;
  }

  public Float getAccTotal() {
    float total = 0f;
    for (AccessRecord record : accessRecordList) {
      total = total + ((record.getAccel() == null) ? 0f : record.getAccel());
    }
    return total;
  }
}

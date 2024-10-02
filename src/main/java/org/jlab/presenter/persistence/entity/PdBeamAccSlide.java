package org.jlab.presenter.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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
@DiscriminatorValue("PD_BEAM_ACC_SLIDE")
@Table(name = "PD_BEAM_ACC_SLIDE", schema = "PRESENTER_OWNER")
public class PdBeamAccSlide extends Slide {

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(mappedBy = "slideId", orphanRemoval = true, cascade = CascadeType.ALL)
  @OrderBy("orderId asc")
  private List<AccActivityRecord> accActivityRecordList;

  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(mappedBy = "slideId", orphanRemoval = true, cascade = CascadeType.ALL)
  @OrderBy("orderId asc")
  private List<BeamToHallRecord> beamToHallRecordList;

  {
    setSlideType(SlideType.PD_BEAM_ACC_SLIDE);

    beamToHallRecordList = new ArrayList<BeamToHallRecord>();
    char hall = 'A';
    for (int i = 1; i < 5; i++) {
      BeamToHallRecord record = new BeamToHallRecord();
      record.setSlideId(this);
      record.setOrderId(Long.valueOf(i));
      record.setHall("Hall " + hall++);
      beamToHallRecordList.add(record);
    }

    accActivityRecordList = new ArrayList<AccActivityRecord>();

    String[] types = {
      "Acc. Beam Studies", "Acc. Restoration", "Acc. Config. Change", "Sched. Acc. Off"
      // "Down Hard",
      // "FSD Down Time"
    };
    for (int i = 0; i < types.length; i++) {
      AccActivityRecord activity = new AccActivityRecord();
      activity.setSlideId(this);
      activity.setOrderId(Long.valueOf(i + 1));
      activity.setActivityType(types[i]);
      accActivityRecordList.add(activity);
    }

    setLabel("PD Summary Type II #2");
  }

  public PdBeamAccSlide() {}

  @Override
  public Slide copySlide() {
    PdBeamAccSlide copy = new PdBeamAccSlide();

    List<AccActivityRecord> copyOfAccActivityRecordList = new ArrayList<AccActivityRecord>();

    long order = 1;

    for (AccActivityRecord record : getAccActivityRecordList()) {
      AccActivityRecord copyOfRecord = record.copyRecord();
      copyOfRecord.setSlideId(copy);
      copyOfRecord.setOrderId(order++);

      copyOfAccActivityRecordList.add(copyOfRecord);
    }

    List<BeamToHallRecord> copyOfBeamToHallRecordList = new ArrayList<BeamToHallRecord>();

    order = 1;

    for (BeamToHallRecord record : getBeamToHallRecordList()) {
      BeamToHallRecord copyOfRecord = record.copyRecord();
      copyOfRecord.setSlideId(copy);
      copyOfRecord.setOrderId(order++);

      copyOfBeamToHallRecordList.add(copyOfRecord);
    }

    copy.setSlideType(getSlideType());
    copy.setAccActivityRecordList(copyOfAccActivityRecordList);
    copy.setBeamToHallRecordList(copyOfBeamToHallRecordList);
    copy.setSyncFromSlideId(getSlideId());

    return copy;
  }

  @Override
  public void mergeSlide(Slide other) {
    if (other instanceof PdBeamAccSlide) {
      PdBeamAccSlide slide = (PdBeamAccSlide) other;

      long order = 1;

      this.getAccActivityRecordList().clear();

      for (AccActivityRecord record : slide.getAccActivityRecordList()) {
        AccActivityRecord copyOfRecord = record.copyRecord();
        copyOfRecord.setSlideId(this);
        copyOfRecord.setOrderId(order++);

        this.getAccActivityRecordList().add(copyOfRecord);
      }

      this.getBeamToHallRecordList().clear();

      order = 1;

      for (BeamToHallRecord record : slide.getBeamToHallRecordList()) {
        BeamToHallRecord copyOfRecord = record.copyRecord();
        copyOfRecord.setSlideId(this);
        copyOfRecord.setOrderId(order++);

        this.getBeamToHallRecordList().add(copyOfRecord);
      }
    }
  }

  public List<AccActivityRecord> getAccActivityRecordList() {
    return accActivityRecordList;
  }

  public void setAccActivityRecordList(List<AccActivityRecord> accActivityRecordList) {
    this.accActivityRecordList = accActivityRecordList;
  }

  public List<BeamToHallRecord> getBeamToHallRecordList() {
    return beamToHallRecordList;
  }

  public void setBeamToHallRecordList(List<BeamToHallRecord> beamToHallRecordList) {
    this.beamToHallRecordList = beamToHallRecordList;
  }
}

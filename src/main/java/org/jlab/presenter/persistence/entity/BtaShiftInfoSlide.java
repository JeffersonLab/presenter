package org.jlab.presenter.persistence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.jlab.presenter.persistence.enumeration.Shift;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("BTA_SHIFT_INFO_SLIDE")
@Table(name = "BTA_SHIFT_INFO_SLIDE", schema = "PRESENTER_OWNER")
public class BtaShiftInfoSlide extends ShiftInfoSlide {

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "slideId", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("orderId asc")
    private List<BtaRecord> btaRecordList;

    {
        setSlideType(SlideType.BTA_SHIFT_INFO_SLIDE);
        setBody(
                "                    <h1>Shift Summary</h1>\n"
                + "                    <p><b>Start of shift:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>Accel:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>Hall A:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>Hall B:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>Hall C:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>Hall D:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>End of shift:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <h1>Major Reasons For No Beam</h1>\n"
                + "                    <ul>\n"
                + "                        <li></li>\n"
                + "                    </ul>\n"
                + "                    <h1>Problems Requiring Attention</h1>\n"
                + "                    <ul>\n"
                + "                        <li></li>\n"
                + "                    </ul>");

        setProgram("");

        btaRecordList = new ArrayList<BtaRecord>();
        char hall = 'A';
        for (int i = 1; i < 5; i++) {
            BtaRecord record = new BtaRecord();
            record.setSlideId(this);
            record.setOrderId(Long.valueOf(i));
            record.setHallProgram("Hall " + hall++);
            btaRecordList.add(record);
        }

        setLabel("Shift Log");
    }

    public BtaShiftInfoSlide() {
    }

    public BtaShiftInfoSlide(Date ymd, Shift shift) {
        super(ymd, shift);
    }

    @Override
    public Slide copySlide() {
        BtaShiftInfoSlide copy = new BtaShiftInfoSlide();
        copy.setSlideType(getSlideType());
        copy.setSyncFromSlideId(getSlideId());
        copy.setBody(getBody());
        copy.setYmd(getYmd());
        copy.setShift(getShift());
        copy.setTeam(getTeam());
        copy.setProgram(getProgram());

        List<BtaRecord> copyOfBtaRecordList = new ArrayList<BtaRecord>();

        long order = 1;
        for (BtaRecord record : getBtaRecordList()) {
            BtaRecord copyOfRecord = record.copyRecord();
            copyOfRecord.setSlideId(copy);
            copyOfRecord.setOrderId(order++);

            copyOfBtaRecordList.add(copyOfRecord);
        }

        copy.setBtaRecordList(copyOfBtaRecordList);
        return copy;
    }

    @Override
    public void mergeSlide(Slide other) {
        if (other instanceof BtaShiftInfoSlide) {
            BtaShiftInfoSlide slide = (BtaShiftInfoSlide) other;
            this.setBody(slide.getBody());
            this.setYmd(slide.getYmd());
            this.setShift(slide.getShift());
            this.setTeam(slide.getTeam());
            this.setProgram(slide.getProgram());

            this.getBtaRecordList().clear();

            long order = 1;
            for (BtaRecord record : slide.getBtaRecordList()) {
                BtaRecord copyOfRecord = record.copyRecord();
                copyOfRecord.setSlideId(this);
                copyOfRecord.setOrderId(order++);

                this.getBtaRecordList().add(copyOfRecord);
            }

        }
    }

    public List<BtaRecord> getBtaRecordList() {
        return btaRecordList;
    }

    public void setBtaRecordList(List<BtaRecord> btaRecordList) {
        this.btaRecordList = btaRecordList;
    }
}

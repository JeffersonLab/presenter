package org.jlab.presenter.persistence.entity;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.persistence.enumeration.ShiftSlideType;
import org.jlab.presenter.persistence.enumeration.Shift;
import org.jlab.presenter.persistence.enumeration.SlideType;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("SHIFT_INFO_SLIDE")
@Table(name = "SHIFT_INFO_SLIDE", schema = "PRESENTER_OWNER")
public class ShiftInfoSlide extends Slide {

    @Basic(optional = false)
    @NotNull
    @Column(name = "YMD", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ymd;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SHIFT", nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    private Shift shift;
    @Size(max = 48)
    @Column(name = "TEAM", length = 48)
    private String team;
    @Size(max = 48)
    @Column(name = "PROGRAM", length = 48)
    private String program;
    @Lob
    @Column(name = "BODY")
    private String body;
    @Column(name = "SHIFT_SLIDE_TYPE", length = 24, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ShiftSlideType shiftSlideType;

    {
        setSlideType(SlideType.SHIFT_INFO_SLIDE);
    }

    public ShiftInfoSlide() {
        this(new Date(), Shift.DAY);
    }

    public ShiftInfoSlide(Date ymd, Shift shift) {
        this(ymd, shift, ShiftSlideType.LSD);
    }

    public ShiftInfoSlide(Date ymd, Shift shift, ShiftSlideType shiftSlideType) {
        this.ymd = ymd;
        this.shift = shift;
        this.shiftSlideType = shiftSlideType;

        body = "                    <h1>Summary</h1>\n"
                + "                    <p><b>ES&H:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>INJ:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>MSSG:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>RF:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>SRF:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>DC:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>I&amp;C:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>SSG:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>S&amp;A:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>SW:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>Cryo:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>FML:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>Physics:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>LERF:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>Gun Test Stand:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>UITF:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <h1>Problems Requiring Attention</h1>\n"
                + "                    <ul>\n"
                + "                        <li></li>\n"
                + "                    </ul> ";

        switch (shiftSlideType) {
            case LSD:
                setLabel("LSD Summary");
                program = "LSD";
                break;
            case HCO:
                setLabel("HCO Summary");
                program = "HCO";
                break;
            case PD:
                setLabel("PD Summary");
                program = "";
                break;
            case LO:
                setLabel("LERF Shift Log");
                program = "";
                body = "                    <h1>Summary</h1>\n"
                + "                    <p><b>Start of shift:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <p><b>End of shift:</b>&nbsp;</p>\n"
                + "                    <p><br/></p>"
                + "                    <h1>Major Reasons for no beam</h1>\n"
                + "                    <ul>\n"
                + "                        <li></li>\n"
                + "                    </ul> "                
                + "                    <h1>Problems Requiring Attention</h1>\n"
                + "                    <ul>\n"
                + "                        <li></li>\n"
                + "                    </ul> ";
                break;
            case UITF:
                setLabel("UITF Shift Log");
                program = "";
                body = "                    <h1>Summary</h1>\n"
                        + "                    <p><b>Start of shift:</b>&nbsp;</p>\n"
                        + "                    <p><br/></p>"
                        + "                    <p><b>End of shift:</b>&nbsp;</p>\n"
                        + "                    <p><br/></p>"
                        + "                    <h1>Major Reasons for no beam</h1>\n"
                        + "                    <ul>\n"
                        + "                        <li></li>\n"
                        + "                    </ul> "
                        + "                    <h1>Problems Requiring Attention</h1>\n"
                        + "                    <ul>\n"
                        + "                        <li></li>\n"
                        + "                    </ul> ";
                break;
            case LASO:
                setLabel("LASO Shift Log");
                program = "";
                body = "                    <h1>Program</h1>\n"
                        + "                    <p><b>Beam Commissioning Plan:</b>&nbsp;</p>\n"
                        + "                    <p><br/></p>"
                        + "                    <p><b>Beam Test Plans:</b>&nbsp;</p>\n"
                        + "                    <p><br/></p>"
                        + "                    <h1>PROGRESS</h1>\n"
                        + "                    <p><b>Completed Beam Commissioning Steps:</b>&nbsp;</p>\n"
                        + "                    <p><br/></p>"
                        + "                    <p><b>Progress Summary:</b>&nbsp;</p>\n"
                        + "                    <p><br/></p>"
                        + "                    <h1>Issues Preventing Progress</h1>\n"
                        + "                    <ul>\n"
                        + "                        <li></li>\n"
                        + "                    </ul> ";
                break;
        }
    }

    public ShiftSlideType getShiftSlideType() {
        return shiftSlideType;
    }

    public void setShiftSlideType(ShiftSlideType pdShiftSlideType) {
        this.shiftSlideType = pdShiftSlideType;
    }

    @Override
    public Slide copySlide() {
        ShiftInfoSlide copy = new ShiftInfoSlide();

        copy.setBody(getBody());
        copy.setProgram(getProgram());
        copy.setShift(getShift());
        copy.setSlideType(getSlideType());
        copy.setTeam(getTeam());
        copy.setYmd(getYmd());
        copy.setShiftSlideType(getShiftSlideType());
        copy.setSyncFromSlideId(getSlideId());
        
        return copy;
    }

    @Override
    public void mergeSlide(Slide other) {
        if (other instanceof ShiftInfoSlide) {
            ShiftInfoSlide slide = (ShiftInfoSlide) other;
            this.setBody(slide.getBody());
            this.setProgram(slide.getProgram());
            this.setShift(slide.getShift());
            this.setTeam(slide.getTeam());
            this.setYmd(slide.getYmd());
        }
    }
    
    public Date getYmd() {
        return ymd;
    }

    public void setYmd(Date ymd) {
        this.ymd = ymd;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    public Date getCrewChiefStartDayAndHourInclusive() {
        Date day = new Date(ymd.getTime());
        
        int hour;
        switch (shift) {
            case DAY:
                hour = 7;
                break;
            case SWING:
                hour = 15;
                break;
            case OWL:
                hour = 23;
                day = TimeUtil.addDays(day, -1);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized shift: " + shift);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, hour);

        return cal.getTime();
    }

    public Date getCrewChiefEndDayAndHourExclusive() {
        Date day = new Date(ymd.getTime());
        int hour = 7;
        switch (shift) {
            case DAY:
                hour = 15;
                break;
            case SWING:
                hour = 23;
                break;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, hour);

        return cal.getTime();
    }    
}

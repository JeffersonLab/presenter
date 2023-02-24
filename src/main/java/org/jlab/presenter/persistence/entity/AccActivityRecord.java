package org.jlab.presenter.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ryans
 */
@Entity
@Table(name = "ACC_ACTIVITY_RECORD", schema = "PRESENTER_OWNER")
public class AccActivityRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name="AccActivityRecordId", sequenceName="ACC_ACTIVITY_RECORD_ID", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AccActivityRecordId")       
    @Basic(optional = false)
    @NotNull
    @Column(name = "ACC_ACTIVITY_RECORD_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger accActivityRecordId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ORDER_ID", nullable = false)
    private Long orderId;    
    @Size(max = 36)
    @Column(name = "ACTIVITY_TYPE", length = 36)
    private String activityType;
    @Column(name = "SCHEDULED", precision = 12, scale = 4)
    private Float scheduled;
    @Column(name = "ACTUAL", precision = 12, scale = 4)
    private Float actual;
    @JoinColumn(name = "SLIDE_ID", referencedColumnName = "SLIDE_ID")
    @ManyToOne
    private PdBeamAccSlide slideId;

    public AccActivityRecord() {
    }

    public AccActivityRecord(BigInteger accActivityRecordId) {
        this.accActivityRecordId = accActivityRecordId;
    }

    public AccActivityRecord copyRecord() {
        AccActivityRecord copy = new AccActivityRecord();
        
        copy.setActivityType(activityType);
        copy.setActual(actual);
        copy.setScheduled(scheduled);
        
        return copy;
    }
    
    public BigInteger getAccActivityRecordId() {
        return accActivityRecordId;
    }

    public void setAccActivityRecordId(BigInteger accActivityRecordId) {
        this.accActivityRecordId = accActivityRecordId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Float getScheduled() {
        return scheduled;
    }

    public void setScheduled(Float scheduled) {
        this.scheduled = scheduled;
    }

    public Float getActual() {
        return actual;
    }

    public void setActual(Float actual) {
        this.actual = actual;
    }

    public PdBeamAccSlide getSlideId() {
        return slideId;
    }

    public void setSlideId(PdBeamAccSlide slideId) {
        this.slideId = slideId;
    }    
}

package org.jlab.presenter.persistence.entity;

import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import org.jlab.presenter.persistence.enumeration.PresentationType;

/**
 *
 * @author ryans
 */
@Entity
@DiscriminatorValue("STAFF_PRESENTATION")
@Table(name = "STAFF_PRESENTATION", schema = "PRESENTER_OWNER", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"TITLE", "STAFF_ID"})})
public class StaffPresentation extends Presentation {
    @Size(max = 128)
    @Column(name = "TITLE", length = 128)
    private String title;
    @Column(name = "STAFF_ID")
    private BigInteger staffId;

    {
        setPresentationType(PresentationType.STAFF_PRESENTATION);
    }      
    
    public StaffPresentation() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigInteger getStaffId() {
        return staffId;
    }

    public void setStaffId(BigInteger staffId) {
        this.staffId = staffId;
    }
}

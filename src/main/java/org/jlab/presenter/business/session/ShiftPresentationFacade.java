package org.jlab.presenter.business.session;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.security.PermitAll;
import org.jlab.presenter.persistence.entity.Presentation;
import org.jlab.presenter.persistence.enumeration.Shift;

/**
 *
 * @author ryans
 * @param <T>
 */
public abstract class ShiftPresentationFacade<T> extends AbstractFacade<T> {

    public ShiftPresentationFacade(Class<T> entityClass) {
        super(entityClass);
    }

    @PermitAll
    public abstract BigInteger findIdByYmdAndShift(Date ymd, Shift shift);

    @PermitAll
    public abstract Presentation findWithSlides(BigInteger id);
}

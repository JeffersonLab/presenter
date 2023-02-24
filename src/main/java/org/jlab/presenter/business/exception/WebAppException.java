package org.jlab.presenter.business.exception;

import javax.ejb.ApplicationException;

/**
 *
 * @author ryans
 */
@ApplicationException(inherited = true, rollback = true)
public class WebAppException extends Exception {
    public WebAppException(String msg) {
        super(msg);
    }
    
    public WebAppException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

package org.jlab.presenter.business.exception;

import javax.ejb.ApplicationException;

/**
 *
 * @author ryans
 */
@ApplicationException(inherited = true, rollback = true)
public class WebAppException extends Exception {
    private final String userMessage;

    public WebAppException(String msg) {
        super(msg);
        this.userMessage = msg;
    }
    
    public WebAppException(String msg, Throwable cause) {
        super(msg, cause);
        this.userMessage = msg;
    }

    public String getUserFriendlyMessage() {
        return userMessage;
    }
}

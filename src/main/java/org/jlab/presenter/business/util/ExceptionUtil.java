package org.jlab.presenter.business.util;

/**
 * @author ryans
 */
public final class ExceptionUtil {

  private ExceptionUtil() {
    // No one can instantiate due to private visibility
  }

  public static Throwable getRootCause(Throwable t) {
    while (t != null && t.getCause() != null) {
      t = t.getCause();
    }

    return t;
  }
}

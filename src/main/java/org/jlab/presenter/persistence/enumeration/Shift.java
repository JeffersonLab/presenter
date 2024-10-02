package org.jlab.presenter.persistence.enumeration;

public enum Shift {
  OWL("Owl"),
  DAY("Day"),
  SWING("Swing");

  private final String label;

  Shift(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}

package com.flowingcode.vaadin.addons.demo;

public enum SourcePosition {

  PRIMARY, SECONDARY;

  public SourcePosition toggle() {
    switch (this) {
      case SECONDARY:
        return PRIMARY;
      case PRIMARY:
      default:
        return SECONDARY;
    }
  }

}

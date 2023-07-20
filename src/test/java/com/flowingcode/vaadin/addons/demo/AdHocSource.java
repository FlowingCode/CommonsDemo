package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class AdHocSource extends Div {

  public AdHocSource() {
    add(new Span("... but sources have this other span instead"));
  }

}

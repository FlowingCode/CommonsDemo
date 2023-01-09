package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.Component;

/**
 * Classes implementing this interface render the help content of a demo view. <br/>
 * Implementations must have a no arguments constructor.
 *
 */
public interface DemoHelperRenderer {

  /**
   * Demo help Content to display.
   * 
   * @return
   */
  Component helperContent();

}

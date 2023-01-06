package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.Component;

/**
 * Implementations of this interface can be used to display content rendered by
 * {@link DemoHelperRenderer}.
 *
 */
public interface DemoHelperViewer {

  /**
   * Shows help content.
   * 
   * @param content
   */
  void show(Component content);

}

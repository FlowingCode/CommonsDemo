package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;

/**
 * A simple {@link DemoHelperViewer} that displays help content from {@link DemoHelperRenderer} in a
 * {@link Dialog}.
 * 
 */
public class DialogDemoHelperViewer implements DemoHelperViewer {

  @Override
  public void show(Component content) {
    new Dialog(content).open();
  }

}

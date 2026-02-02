package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;

public class AppShellConfiguratorImpl implements AppShellConfigurator {

  @Override
  public void configurePage(AppShellSettings settings) {
    if (DynamicTheme.isFeatureSupported()) {
      DynamicTheme.LUMO.initialize(settings);
    }
  }

}

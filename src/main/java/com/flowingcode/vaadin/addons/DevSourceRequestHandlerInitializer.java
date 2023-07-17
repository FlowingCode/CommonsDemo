package com.flowingcode.vaadin.addons;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class DevSourceRequestHandlerInitializer implements VaadinServiceInitListener {

  @Override
  public void serviceInit(ServiceInitEvent event) {
    if (!event.getSource().getDeploymentConfiguration().isProductionMode()) {
      event.addRequestHandler(new DevSourceRequestHandler());
    }
  }

}

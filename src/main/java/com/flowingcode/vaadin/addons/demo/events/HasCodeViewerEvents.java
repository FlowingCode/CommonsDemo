package com.flowingcode.vaadin.addons.demo.events;

import com.vaadin.flow.component.ComponentEventListener;

public interface HasCodeViewerEvents {

  void addSourceCollapseListener(ComponentEventListener<SourceCollapseChangedEvent> listener);

  void addSourceFlipListener(ComponentEventListener<SourceFlipEvent> listener);

  void addSourceRotateListener(ComponentEventListener<SourceRotateEvent> listener);

}

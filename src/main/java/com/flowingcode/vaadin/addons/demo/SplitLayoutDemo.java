/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2025 Flowing Code
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.flowingcode.vaadin.addons.demo;

import com.flowingcode.vaadin.addons.demo.events.HasCodeViewerEvents;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.server.Version;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.Delegate;

@SuppressWarnings("serial")
class SplitLayoutDemo extends Composite<SplitLayout> implements HasCodeViewerEvents {

  private static final String HIDE_SOURCE_CLASS = "hide-source";
  private static final String SOURCE_PRIMARY_CLASS = "source-primary";
  private static final String SOURCE_SECONDARY_CLASS = "source-secondary";

  @Delegate(types = HasCodeViewerEvents.class)
  private MultiSourceCodeViewer code;
  private Component demo;
  private SourcePosition sourcePosition;

  public SplitLayoutDemo(Component demo, String sourceUrl, SourcePosition sourcePosition) {
    this(demo, Arrays.asList(new SourceCodeTab(sourceUrl, sourcePosition)));
  }

  public SplitLayoutDemo(Component demo, List<SourceCodeTab> tabs) {
    getContent().setOrientation(Orientation.HORIZONTAL);

    setId("commons-demo-split-layout");
    Map<String, String> properties = new HashMap<>();
    properties.put("vaadin", VaadinVersion.getVaadinVersion());
    properties.put("flow", Version.getFullVersion());

    code = new MultiSourceCodeViewer(tabs, properties);
    
    code.createToolbar();
    
    code.addSourceCollapseListener(ev -> {
      if (ev.isCollapsed()) {
        hideSourceCode();
      } else {
        showSourceCode();
      }
    });

    code.addSourceFlipListener(ev -> {
      toggleSourcePosition();
    });

    this.demo = demo;
    setSourcePosition(code.getSourcePosition());

    getContent().setSizeFull();
  }

  private void setSourcePosition(SourcePosition position) {
    if (!position.equals(sourcePosition)) {
      getContent().removeAll();
      switch (position) {
        case PRIMARY:
          getContent().addToPrimary(code);
          getContent().addToSecondary(demo);
          addClassName(SOURCE_PRIMARY_CLASS);
          removeClassName(SOURCE_SECONDARY_CLASS);
          break;
        case SECONDARY:
        default:
          getContent().addToPrimary(demo);
          getContent().addToSecondary(code);
          removeClassName(SOURCE_PRIMARY_CLASS);
          addClassName(SOURCE_SECONDARY_CLASS);
      }
      sourcePosition = position;
    }
  }
  
  public void toggleSourcePosition() {
    setSourcePosition(sourcePosition.toggle());
  }

  public void setOrientation(Orientation o) {
    getContent().setOrientation(o);
    getContent()
        .getPrimaryComponent()
        .getElement()
        .setAttribute("style", "width: 100%; height: 100%");
    code.setSizeFull();
  }

  public Orientation getOrientation() {
    return getContent().getOrientation();
  }

  public void setSplitterPosition(int pos) {
    getContent().setSplitterPosition(pos);
  }

  public void setSizeFull() {
    getContent().setSizeFull();
  }

  public void showSourceCode() {
    getClassNames().remove(HIDE_SOURCE_CLASS);
    getContent().setSplitterPosition(50);
  }

  public void hideSourceCode() {
    getClassNames().add(HIDE_SOURCE_CLASS);
    switch (sourcePosition) {
      case PRIMARY:
        getContent().setSplitterPosition(0);
        break;
      case SECONDARY:
        getContent().setSplitterPosition(100);
        break;
    }
  }

}

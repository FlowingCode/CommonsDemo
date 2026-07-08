/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2026 Flowing Code
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
package com.flowingcode.vaadin.addons.demo.it;

import com.flowingcode.vaadin.addons.GithubLink;
import com.flowingcode.vaadin.addons.demo.DemoSource;
import com.flowingcode.vaadin.addons.demo.SourcePosition;
import com.flowingcode.vaadin.addons.demo.TabbedDemo;
import com.flowingcode.vaadin.jsonmigration.InstrumentedRoute;
import com.flowingcode.vaadin.jsonmigration.JsonMigration;
import com.flowingcode.vaadin.jsonmigration.LegacyClientCallable;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
@GithubLink("https://github.com/FlowingCode/CommonsDemo")
public class OverlayView extends TabbedDemo implements OverlayCallables {

  private boolean collapsed;
  private boolean collapseFromClient;
  private SourcePosition position;
  private boolean positionFromClient;
  private boolean orientationFromClient;

  public OverlayView() {
    addDemo(JsonMigration.instrumentClass(RpcOverlayDemo.class));
    addSourceCollapseListener(ev -> {
      collapsed = ev.isCollapsed();
      collapseFromClient = ev.isFromClient();
    });
    addSourcePositionChangedListener(ev -> {
      position = ev.getSourcePosition();
      positionFromClient = ev.isFromClient();
    });
    addOrientationChangedListener(ev -> orientationFromClient = ev.isFromClient());
  }

  @Override
  @LegacyClientCallable
  public JsonValue $call(JsonObject invocation) {
    return OverlayCallables.super.$call(invocation);
  }

  @Override
  public Orientation orientation() {
    return getOrientation();
  }

  @Override
  public boolean sourceCollapsed() {
    return collapsed;
  }

  @Override
  public SourcePosition sourcePosition() {
    return position;
  }

  @Override
  public boolean lastCollapseFromClient() {
    return collapseFromClient;
  }

  @Override
  public boolean lastPositionFromClient() {
    return positionFromClient;
  }

  @Override
  public boolean lastOrientationFromClient() {
    return orientationFromClient;
  }

  /** Single source-bearing demo, so the overlay controls are rendered. */
  @DemoSource(clazz = OverlayView.class)
  @InstrumentedRoute(value = "it/rpc-overlay", layout = OverlayView.class)
  public static class RpcOverlayDemo extends Div {

    public RpcOverlayDemo() {
      add(new Span("RpcOverlayDemo"));
    }
  }
}

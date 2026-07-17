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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import com.flowingcode.vaadin.addons.demo.SourcePosition;
import com.flowingcode.vaadin.addons.demo.events.OrientationChangedEvent;
import com.flowingcode.vaadin.addons.demo.events.SourceCollapseChangedEvent;
import com.flowingcode.vaadin.addons.demo.events.SourcePositionChangedEvent;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewMultiSource;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewNoSource;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewPrimarySource;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewSingleSource;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.dom.Element;
import com.vaadin.testbench.unit.UIUnit4Test;
import com.vaadin.testbench.unit.ViewPackages;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

@ViewPackages(classes = TabbedDemoView.class)
public class TabbedDemoUIUnitTest extends UIUnit4Test {

  private static final String TAG_CODE_VIEWER = "code-viewer";
  private static final String TAG_SOURCE_CODE_VIEWER_BUTTONS = "source-code-viewer-buttons";

  private static TabbedDemoView openWithSource() {
    TabbedDemoView demo = new TabbedDemoView();
    demo.showRouterLayoutContent(new TabbedDemoViewSingleSource());
    return demo;
  }

  // --- the overlay is present exactly when the demo has source ----------------------

  @Test
  public void overlayPresentForSingleSource() {
    TabbedDemoView demo = openWithSource();
    assertEquals(1, count(demo.getElement(), TAG_SOURCE_CODE_VIEWER_BUTTONS));
    assertEquals(1, count(demo.getElement(), TAG_CODE_VIEWER));
  }

  @Test
  public void overlayPresentOnceForMultiSource() {
    TabbedDemoView demo = new TabbedDemoView();
    demo.showRouterLayoutContent(new TabbedDemoViewMultiSource());
    assertEquals(1, count(demo.getElement(), TAG_SOURCE_CODE_VIEWER_BUTTONS));
    assertEquals(1, count(demo.getElement(), TAG_CODE_VIEWER));
  }

  @Test
  public void noOverlayWhenDemoHasNoSource() {
    TabbedDemoView demo = new TabbedDemoView();
    demo.showRouterLayoutContent(new TabbedDemoViewNoSource());
    assertEquals(0, count(demo.getElement(), TAG_SOURCE_CODE_VIEWER_BUTTONS));
    assertEquals(0, count(demo.getElement(), TAG_CODE_VIEWER));
  }

  // --- setSourceVisible collapses/expands and fires the event (fromClient=false) ----

  @Test
  public void setSourceVisibleFiresCollapseEvent() {
    TabbedDemoView demo = openWithSource();
    List<SourceCollapseChangedEvent> events = new ArrayList<>();
    demo.addSourceCollapseListener(events::add);

    demo.setSourceVisible(false);
    assertEquals(1, events.size());
    assertTrue(events.get(0).isCollapsed());
    assertFalse(events.get(0).isFromClient());

    demo.setSourceVisible(true);
    assertEquals(2, events.size());
    assertFalse(events.get(1).isCollapsed());
    assertFalse(events.get(1).isFromClient());
  }

  // --- toggleSourcePosition toggles the position and fires the event ---------------

  @Test
  public void toggleSourcePositionFiresEventAndToggles() {
    TabbedDemoView demo = openWithSource();
    List<SourcePositionChangedEvent> events = new ArrayList<>();
    demo.addSourcePositionChangedListener(events::add);

    demo.toggleSourcePosition();
    assertEquals(1, events.size());
    // Single source defaults to SECONDARY, so the first toggle yields PRIMARY.
    assertEquals(SourcePosition.PRIMARY, events.get(0).getSourcePosition());
    assertFalse(events.get(0).isFromClient());

    demo.toggleSourcePosition();
    assertEquals(2, events.size());
    assertEquals(SourcePosition.SECONDARY, events.get(1).getSourcePosition());
    assertNotEquals(events.get(0).getSourcePosition(), events.get(1).getSourcePosition());
  }

  // --- a DEFAULT position inherits the position carried over from the previous demo -

  @Test
  public void defaultPositionInheritsFromPreviousDemo() {
    TabbedDemoView demo = new TabbedDemoView();
    Component first = new TabbedDemoViewPrimarySource();
    demo.showRouterLayoutContent(first);

    // TabbedDemoViewSingleSource has no explicit position (DEFAULT), so it should adopt
    // the PRIMARY position carried over from the previous demo.
    demo.removeRouterLayoutContent(first);
    demo.showRouterLayoutContent(new TabbedDemoViewSingleSource());

    List<SourcePositionChangedEvent> events = new ArrayList<>();
    demo.addSourcePositionChangedListener(events::add);

    demo.toggleSourcePosition();
    assertEquals(1, events.size());
    // Inherited PRIMARY, so the first toggle yields SECONDARY.
    assertEquals(SourcePosition.SECONDARY, events.get(0).getSourcePosition());
  }

  // --- setOrientation changes state and fires the event; same value is a no-op ------

  @Test
  public void setOrientationChangesStateAndFiresEvent() {
    TabbedDemoView demo = openWithSource();
    assertEquals(Orientation.HORIZONTAL, demo.getOrientation());
    List<OrientationChangedEvent> events = new ArrayList<>();
    demo.addOrientationChangedListener(events::add);

    demo.setOrientation(Orientation.VERTICAL);

    assertEquals(Orientation.VERTICAL, demo.getOrientation());
    assertEquals(1, events.size());
    assertEquals(Orientation.VERTICAL, events.get(0).getOrientation());
    assertFalse(events.get(0).isFromClient());
  }

  @Test
  public void settingCurrentOrientationIsANoOp() {
    TabbedDemoView demo = openWithSource();
    List<OrientationChangedEvent> events = new ArrayList<>();
    demo.addOrientationChangedListener(events::add);

    demo.setOrientation(Orientation.HORIZONTAL); // already horizontal

    assertEquals(Orientation.HORIZONTAL, demo.getOrientation());
    assertTrue(events.isEmpty());
  }

  // --- orientation carries over to the next demo ------------------------------------

  @Test
  public void orientationPersistsAcrossContentChange() {
    TabbedDemoView demo = new TabbedDemoView();
    Component first = new TabbedDemoViewSingleSource();
    demo.showRouterLayoutContent(first);
    demo.setOrientation(Orientation.VERTICAL);
    assertEquals(Orientation.VERTICAL, demo.getOrientation());

    demo.removeRouterLayoutContent(first);
    demo.showRouterLayoutContent(new TabbedDemoViewMultiSource());

    assertEquals(Orientation.VERTICAL, demo.getOrientation());
  }

  @Test
  public void orientationSetWithoutLayoutCarriesOverToNextDemo() {
    // A source-less demo has no layout; setting the orientation there (as a device-driven portrait
    // adjustment does on attach) must be remembered and applied to the next source-bearing demo.
    TabbedDemoView demo = new TabbedDemoView();
    Component noSource = new TabbedDemoViewNoSource();
    demo.showRouterLayoutContent(noSource);

    demo.setOrientation(Orientation.VERTICAL);
    assertEquals(Orientation.VERTICAL, demo.getOrientation());

    demo.removeRouterLayoutContent(noSource);
    demo.showRouterLayoutContent(new TabbedDemoViewSingleSource());

    assertEquals(Orientation.VERTICAL, demo.getOrientation());
  }

  /** Counts elements with the given tag in the subtree rooted at {@code root} (inclusive). */
  private static long count(Element root, String tag) {
    long self = tag.equals(root.getTag()) ? 1 : 0;
    return self + root.getChildren()
        .filter(e -> !e.isTextNode())
        .mapToLong(child -> count(child, tag)).sum();
  }

}

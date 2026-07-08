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
package com.flowingcode.vaadin.addons.demo.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import com.flowingcode.vaadin.addons.demo.SourceCodeTab;
import com.flowingcode.vaadin.addons.demo.SourcePosition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import org.junit.Test;

@ExtensionMethod(value = Reflect.class, suppressBaseMethods = true)
public class SplitLayoutDemoTest {

  private Div demo;

  private Component build(SourcePosition position) {
    demo = new Div();
    demo.setId("content");
    return Reflect.newSplitLayoutDemo(demo,
        List.of(new SourceCodeTab("src/test/java/Demo.java", position)));
  }

  // --- collapse resolves to the correct splitter position -----------------------

  @Test
  public void collapseWithSecondarySourceMovesSplitterToFullPrimary() {
    Component layout = build(SourcePosition.SECONDARY);

    layout.setSourceCollapsed(false);
    assertEquals(50.0, splitterPosition(layout), 0.0);

    layout.setSourceCollapsed(true);
    assertEquals(100.0, splitterPosition(layout), 0.0);

    layout.setSourceCollapsed(false);
    assertEquals(50.0, splitterPosition(layout), 0.0);
  }

  @Test
  public void collapseWithPrimarySourceMovesSplitterToFullSecondary() {
    Component layout = build(SourcePosition.PRIMARY);

    layout.setSourceCollapsed(true);
    assertEquals(0.0, splitterPosition(layout), 0.0);

    layout.setSourceCollapsed(false);
    assertEquals(50.0, splitterPosition(layout), 0.0);
  }

  // --- source position swaps slot occupants and toggles cleanly -----------------

  @Test
  public void sourcePositionSwapsSlotsAndToggles() {
    Component layout = build(SourcePosition.SECONDARY);

    // Default SECONDARY: demo in primary, source viewer in secondary.
    assertSame(demo, layout.getContent().getPrimaryComponent());
    assertNotSame(demo, layout.getContent().getSecondaryComponent());
    Component code = layout.getContent().getSecondaryComponent();

    layout.setSourcePosition(SourcePosition.PRIMARY);
    assertSame(code, layout.getContent().getPrimaryComponent());
    assertSame(demo, layout.getContent().getSecondaryComponent());
    assertEquals(SourcePosition.PRIMARY, layout.getSourcePosition());

    layout.setSourcePosition(SourcePosition.SECONDARY);
    assertSame(demo, layout.getContent().getPrimaryComponent());
    assertSame(code, layout.getContent().getSecondaryComponent());
    assertEquals(SourcePosition.SECONDARY, layout.getSourcePosition());
  }

  // --- orientation set/get round-trips ------------------------------------------

  @Test
  public void orientationRoundTrips() {
    Component layout = build(SourcePosition.SECONDARY);
    assertEquals(Orientation.HORIZONTAL, layout.getOrientation());

    layout.setOrientation(Orientation.VERTICAL);
    assertEquals(Orientation.VERTICAL, layout.getOrientation());

    layout.setOrientation(Orientation.HORIZONTAL);
    assertEquals(Orientation.HORIZONTAL, layout.getOrientation());
  }

  private static double splitterPosition(Component layout) {
    Double position = layout.getContent().getSplitterPosition();
    assertNotNull("splitter position was never set", position);
    return position;
  }

}


/**
 * Reflection accessors for the package-private {@code SplitLayoutDemo} and the protected
 * {@code Composite.getContent()}, one static method per real method. Surfaced as Lombok
 * {@code @ExtensionMethod}s so the test can drive the layout with real-API-looking call sites while
 * the actual dispatch goes through reflection.
 */
final class Reflect {

  private Reflect() {}

  @SneakyThrows
  static Component newSplitLayoutDemo(Component demo, List<SourceCodeTab> tabs) {
    Class<?> type = Class.forName("com.flowingcode.vaadin.addons.demo.SplitLayoutDemo");
    Constructor<?> constructor = type.getDeclaredConstructor(Component.class, List.class);
    constructor.setAccessible(true);
    return (Component) constructor.newInstance(demo, tabs);
  }

  public static void setSourceCollapsed(Component layout, boolean collapsed) {
    call(layout, "setSourceCollapsed", new Class<?>[] {boolean.class}, collapsed);
  }

  public static void setSourcePosition(Component layout, SourcePosition position) {
    call(layout, "setSourcePosition", new Class<?>[] {SourcePosition.class}, position);
  }

  public static void setOrientation(Component layout, Orientation orientation) {
    call(layout, "setOrientation", new Class<?>[] {Orientation.class}, orientation);
  }

  public static Orientation getOrientation(Component layout) {
    return (Orientation) call(layout, "getOrientation", new Class<?>[0]);
  }

  public static SourcePosition getSourcePosition(Component layout) {
    return (SourcePosition) call(layout, "getSourcePosition", new Class<?>[0]);
  }

  public static SplitLayout getContent(Component layout) {
    return (SplitLayout) call(layout, "getContent", new Class<?>[0]);
  }

  /** Locates {@code name(paramTypes)} on {@code target} or a supertype and invokes it. */
  @SneakyThrows
  private static Object call(Object target, String name, Class<?>[] paramTypes, Object... args) {
    for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
      try {
        Method method = c.getDeclaredMethod(name, paramTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
      } catch (NoSuchMethodException declaredInSupertype) {
        // keep walking up the hierarchy
      }
    }
    throw new NoSuchMethodException(target.getClass().getName() + "#" + name);
  }
}

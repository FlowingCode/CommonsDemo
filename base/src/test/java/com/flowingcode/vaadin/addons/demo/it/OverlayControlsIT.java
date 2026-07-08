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

import static com.vaadin.flow.component.splitlayout.SplitLayout.Orientation.HORIZONTAL;
import static com.vaadin.flow.component.splitlayout.SplitLayout.Orientation.VERTICAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.flowingcode.vaadin.testbench.rpc.HasRpcSupport;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

/**
 * Integration tests for the source-code viewer overlay controls. Each test clicks a rendered
 * overlay button and then asserts the effect on both the DOM (CSS-driven visibility, slot
 * placement, orientation attribute) and the server-side state, the latter read over testbench-rpc
 * through {@link OverlayCallables} — see {@link OverlayView} for the {@code @ClientCallable}
 * implementation.
 *
 * <p>
 * The unit ({@code SplitLayoutDemoTest}) and UI-unit ({@code TabbedDemoUIUnitTest}) layers cover
 * the mechanics and the programmatic ({@code fromClient=false}) paths; these tests add what only a
 * real browser can show: CSS visibility, real geometry, and the genuine {@code fromClient=true}
 * origin of a rendered button click.
 */
public class OverlayControlsIT extends AbstractViewTest implements HasRpcSupport {

  private final OverlayCallables $server = createCallableProxy(OverlayCallables.class);

  public OverlayControlsIT() {
    super(null);
  }

  /** Landscape viewport (deterministic HORIZONTAL, non-collapsed baseline) + navigate. */
  private void open() {
    getDriver().manage().window().setSize(new Dimension(1200, 800));
    getDriver().get(getURL("it/rpc-overlay"));
    getCommandExecutor().waitForVaadin();
    getDriver().findElement(By.id("content"));
    waitUntil(d -> hasElement("vaadin-button.source-code-viewer-hide-button"));
  }

  private WebElement button(String name) {
    return getDriver()
        .findElement(By.cssSelector("vaadin-button.source-code-viewer-" + name + "-button"));
  }

  private void click(String name) {
    button(name).click();
    getCommandExecutor().waitForVaadin();
  }

  private boolean hasElement(String cssSelector) {
    return !getDriver().findElements(By.cssSelector(cssSelector)).isEmpty();
  }

  private String orientationAttribute() {
    return getDriver().findElement(By.cssSelector("vaadin-split-layout")).getAttribute("orientation");
  }

  /** Rendered pixel width of the source panel (the slotted {@code code-viewer}). */
  private int sourcePanelWidth() {
    // the slot that contains the source-code-viewer
    String xpath = "//vaadin-split-layout/*[div[contains(@class, 'source-code-viewer')]]";
    return getDriver().findElement(By.xpath(xpath)).getSize().getWidth();
  }

  /** Rendered pixel width of the whole split layout. */
  private int splitLayoutWidth() {
    return getDriver().findElement(By.cssSelector("vaadin-split-layout")).getSize().getWidth();
  }

  /** IT-BTN-01 — the action buttons are visible, the show button hidden, at a normal split. */
  @Test
  public void overlayButtonsVisibility() {
    open();
    assertTrue(button("hide").isDisplayed());
    assertTrue(button("flip").isDisplayed());
    assertTrue(button("rotate").isDisplayed());
    assertFalse(button("show").isDisplayed());
  }

  /** IT-COL-01 — clicking Hide collapses the panel, reveals Show, and is client-originated. */
  @Test
  public void clickingHideCollapsesAndRevealsShow() {
    open();
    assertFalse($server.sourceCollapsed());

    click("hide");

    assertTrue($server.sourceCollapsed());
    assertTrue($server.lastCollapseFromClient());
    waitUntil(d -> button("show").isDisplayed());
    // real geometry: the collapsed source panel measures ~0px wide
    waitUntil(d -> sourcePanelWidth() < 5);
    assertTrue("collapsed source panel should measure ~0px but was " + sourcePanelWidth(),
        sourcePanelWidth() < 5);
  }

  /** IT-COL-02 — clicking Show restores the panel. */
  @Test
  public void clickingShowRestoresPanel() {
    open();
    click("hide");
    waitUntil(d -> button("show").isDisplayed());

    click("show");

    assertFalse($server.sourceCollapsed());
    waitUntil(d -> button("hide").isDisplayed());
    assertFalse(button("show").isDisplayed());
    // real geometry: the splitter returns to ~50, so the source panel spans ~half the layout
    waitUntil(d -> {
      int total = splitLayoutWidth();
      return total > 0 && Math.abs(sourcePanelWidth() - total / 2.0) < total * 0.1;
    });
    double ratio = (double) sourcePanelWidth() / splitLayoutWidth();
    assertTrue("restored source panel should span ~50% of the layout but was " + ratio,
        ratio > 0.4 && ratio < 0.6);
  }

  /** IT-FLIP-01 — clicking Flip swaps the source viewer between slots (client-originated). */
  @Test
  public void clickingFlipSwapsSlots() {
    open();
    assertTrue(hasElement("[slot='secondary'] code-viewer"));
    assertFalse(hasElement("[slot='primary'] code-viewer"));

    click("flip");

    waitUntil(d -> hasElement("[slot='primary'] code-viewer"));
    assertTrue($server.lastPositionFromClient());

    click("flip");
    waitUntil(d -> hasElement("[slot='secondary'] code-viewer"));
  }

  /** IT-ROT-01 — clicking Rotate toggles the split orientation (client-originated). */
  @Test
  public void clickingRotateTogglesOrientation() {
    open();
    assertEquals(HORIZONTAL, $server.orientation());
    assertEquals("horizontal", orientationAttribute());

    click("rotate");

    assertEquals(VERTICAL, $server.orientation());
    waitUntil(d -> "vertical".equals(orientationAttribute()));
    assertTrue($server.lastOrientationFromClient());

    click("rotate");
    assertEquals(HORIZONTAL, $server.orientation());
  }

  /**
   * IT-VIS-01 (optional) — at the collapsed extreme the overlay container falls below every
   * {@code @container} threshold, so only the show button remains visible.
   */
  @Test
  public void collapsedPanelHidesActionButtons() {
    open();
    click("hide");
    waitUntil(d -> button("show").isDisplayed());

    assertFalse(button("hide").isDisplayed());
    assertFalse(button("flip").isDisplayed());
    assertFalse(button("rotate").isDisplayed());
  }
}

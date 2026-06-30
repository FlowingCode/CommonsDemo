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

import com.flowingcode.vaadin.addons.DevSourceRequestHandler;
import com.flowingcode.vaadin.jsonmigration.JsonMigration;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.VaadinService;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.Map;
import lombok.experimental.ExtensionMethod;

@SuppressWarnings("serial")
@JsModule("./code-viewer.ts")
@ExtensionMethod(value = JsonMigration.class, suppressBaseMethods = true)
public class SourceCodeViewer extends Div implements HasSize {

  private final Element codeViewer;

  public SourceCodeViewer(String sourceUrl) {
    this(sourceUrl, null);
  }

  public SourceCodeViewer(String sourceUrl, Map<String, String> properties) {
    this(sourceUrl, "java", properties);
  }

  public SourceCodeViewer(String url, String language, Map<String, String> properties) {
    addClassName("source-code-viewer");
    addClassName("has-code-viewer-gutter");

    codeViewer = new Element("code-viewer");

    Div codeViewerWrapper = new Div();
    codeViewerWrapper.addClassName("source-code-viewer-codeviewer-wrapper");
    codeViewerWrapper.getElement().appendChild(codeViewer);

    Button showButton = new Button(CommonsDemoIcons.SHOW_SOURCE.create());
    showButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
    showButton.setAriaLabel("Show source code");
    showButton.addClickListener(ev -> setSourceCollapsed(false));
    showButton.addClassName("source-code-viewer-button");
    showButton.addClassName("source-code-viewer-show-button");

    Button hideButton = new Button(CommonsDemoIcons.HIDE_SOURCE.create());
    hideButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
    hideButton.setAriaLabel("Hide source code");
    hideButton.addClickListener(ev -> setSourceCollapsed(true));
    hideButton.addClassName("source-code-viewer-button");
    hideButton.addClassName("source-code-viewer-hide-button");

    Button rotateButton = new Button(CommonsDemoIcons.ROTATE.create());
    rotateButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
    rotateButton.setAriaLabel("Rotate source code");
    rotateButton.addClickListener(ev -> rotateSource());
    rotateButton.addClassName("source-code-viewer-button");
    rotateButton.addClassName("source-code-viewer-rotate-button");

    Button flipButton = new Button(CommonsDemoIcons.FLIP.create());
    flipButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
    flipButton.setAriaLabel("Flip source code");
    flipButton.addClickListener(ev -> flipSource());
    flipButton.addClassName("source-code-viewer-button");
    flipButton.addClassName("source-code-viewer-flip-button");

    Div buttons = new Div(showButton, hideButton, flipButton, rotateButton);
    buttons.addClassName("source-code-viewer-buttons");

    // Non-scrolling overlay so the buttons stay pinned while the code scrolls
    Div buttonsWrapper = new Div(buttons);
    buttonsWrapper.addClassName("source-code-viewer-buttons-wrapper");

    add(codeViewerWrapper, buttonsWrapper);

    setProperties(properties);
    addAttachListener(ev -> {
      fetchContents(url, language);
      observeScrollbar();
    });
  }

  /**
   * Observes the scrollable wrapper. Whenever the vertical scrollbar appears or disappears, sets (or
   * clears) the {@code --code-viewer-gutter} custom property on the nearest ancestor (or self)
   * carrying the {@code has-code-viewer-gutter} class. Whenever the wrapper collapses below 24px in
   * either axis, sets {@code --source-code-viewer-show-button-display} so the show button becomes
   * visible (and clears it otherwise).
   */
  private void observeScrollbar() {
    getElement().executeJs(
        """
        const root = this;
        const wrapper = root.querySelector('.source-code-viewer-codeviewer-wrapper');
        if (!wrapper) return;
        root.__scrollbarObserver?.disconnect();
        root.__scrollbarMutation?.disconnect();
        let hasScrollbar = null;
        const update = () => {
          if (wrapper.offsetWidth < 24 || wrapper.offsetHeight < 10) {
            root.style.setProperty('--source-code-viewer-show-button-display', 'block');
          } else {
            root.style.removeProperty('--source-code-viewer-show-button-display');
          }
          const current = wrapper.scrollHeight > wrapper.clientHeight;
          if (current === hasScrollbar) return;
          hasScrollbar = current;
          let target = root;
          while (target && !target.classList.contains('has-code-viewer-gutter')) {
            target = target.parentElement;
          }
          if (target) {
            if (current) {
              const scrollbarWidth = wrapper.offsetWidth - wrapper.clientWidth;
              target.style.setProperty('--code-viewer-gutter', scrollbarWidth + 'px');
            } else {
              target.style.removeProperty('--code-viewer-gutter');
            }
          }
        };
        let frame = 0;
        const scheduleUpdate = () => {
          if (frame) return;
          frame = requestAnimationFrame(() => { frame = 0; update(); });
        };
        const resizeObserver = new ResizeObserver(scheduleUpdate);
        resizeObserver.observe(wrapper);
        root.__scrollbarObserver = resizeObserver;
        const codeViewer = root.querySelector('code-viewer');
        if (codeViewer) {
          const mutationObserver = new MutationObserver(scheduleUpdate);
          mutationObserver.observe(codeViewer, {childList: true, subtree: true});
          root.__scrollbarMutation = mutationObserver;
        }
        update();
        """);
  }

  private void setSourceCollapsed(boolean collapsed) {
    getElement().executeJs(
        "this.dispatchEvent(new CustomEvent('source-collapse-changed',"
            + " {bubbles: true, detail: {collapsed: $0}}))",
        collapsed);
  }

  private void rotateSource() {
    getElement().executeJs(
        "this.dispatchEvent(new CustomEvent('source-rotate', {bubbles: true}))");
  }

  private void flipSource() {
    getElement().executeJs(
        "this.dispatchEvent(new CustomEvent('source-flip', {bubbles: true}))");
  }

  public void fetchContents(String url, String language) {
    url = translateSource(url);
    codeViewer.executeJs("this.fetchContents($0,$1)", url, language);
  }

  private static String translateSource(String url) {
    if (!VaadinService.getCurrent().getDeploymentConfiguration().isProductionMode()) {
      String src = url.replaceFirst("^.*/src/", "/src/");
      if (DevSourceRequestHandler.fileExists(src)) {
        return src;
      }
    }

    if (url.startsWith("https://github.com")) {
      url = url.replaceFirst("github.com", "raw.githubusercontent.com");
      url = url.replaceFirst("/blob", "");
    }
    return url;
  }

  private void setProperties(Map<String, String> properties) {
    if (properties != null) {
      JsonObject env = Json.createObject();
      properties.forEach((k, v) -> {
        if (v != null) {
          env.put(k, Json.create(v));
        }
      });
      codeViewer.setPropertyJson("env", env);
    }
  }

  /**
   * Highlights the block identified by {@code filenameAndId} when the component is hovered over.
   * <p>
   * If the component is in an additional source, {@code filenameAndId} can be given as a string in
   * the format {@code filename#id}. If no {@code '#'} is present, it is assumed that the identifier
   * corresponds to a block in the first source panel.
   *
   * @param c The component that triggers the highlight action when hovered over.
   * @param filenameAndId The identifier string that combines filename and id separated by
   *        {@code '#'}.
   */
  public static void highlightOnHover(Component c, String filenameAndId) {
    c.addAttachListener(ev -> {
      c.getElement().executeJs("Vaadin.Flow.fcCodeViewerConnector.highlightOnHover(this,$0)", filenameAndId);
    });
  }

  /**
   * Highlight block {@code id} when the component is clicked.
   * <p>
   * If the component is in an additional source, {@code filenameAndId} can be given as a string in
   * the format {@code filename#id}. If no {@code '#'} is present, it is assumed that the identifier
   * corresponds to a block in the first source panel.
   *
   * @param c The component that triggers the highlight action when clicked.
   * @param filenameAndId The identifier string that combines filename and id separated by
   *        {@code '#'}.
   */
  public static <T extends HasElement & ClickNotifier<?>> void highlightOnClick(T c,
      String filenameAndId) {
    c.addClickListener(ev -> {
      c.getElement().executeJs("Vaadin.Flow.fcCodeViewerConnector.highlight($0)", filenameAndId);
    });
  }

  /**
   * Highlights the block identified by {@code filenameAndId}.
   * <p>
   * If the component is in an additional source, {@code filenameAndId} can be given as a string in
   * the format {@code filename#id}. If no {@code '#'} is present, it is assumed that the identifier
   * corresponds to a block in the first source panel.
   *
   * @param filenameAndId The identifier string that combines filename and id separated by
   *        {@code '#'}.
   */

  public static void highlight(String filenameAndId) {
    UI.getCurrent().getElement().executeJs("Vaadin.Flow.fcCodeViewerConnector.highlight($0)", filenameAndId);
  }

}

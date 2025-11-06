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
import com.flowingcode.vaadin.addons.demo.events.HasCodeViewerEvents;
import com.flowingcode.vaadin.addons.demo.events.SourceCollapseChangedEvent;
import com.flowingcode.vaadin.addons.demo.events.SourceFlipEvent;
import com.flowingcode.vaadin.addons.demo.events.SourceRotateEvent;
import com.flowingcode.vaadin.jsonmigration.JsonMigration;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.VaadinService;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("serial")
@JsModule("./code-viewer.ts")
public class SourceCodeViewer extends Div implements HasSize, HasCodeViewerEvents {

  private final Element codeViewer;

  private boolean hasToolbar;

  public SourceCodeViewer(String sourceUrl) {
    this(sourceUrl, null);
  }

  public SourceCodeViewer(String sourceUrl, Map<String, String> properties) {
    this(sourceUrl, "java", properties);
  }

  public SourceCodeViewer(String url, String language, Map<String, String> properties) {
    codeViewer = new Element("code-viewer");
    getElement().appendChild(codeViewer);
    getElement().getStyle().set("overflow", "auto");
    getElement().getStyle().set("display", "flex");
    codeViewer.getStyle().set("flex-grow", "1");
    setProperties(properties);
    addAttachListener(ev -> fetchContents(url, language));
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
      JsonMigration.setPropertyJson(codeViewer, "env", env);
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

  private Button createButton(CommonsDemoIcons icon, String tooltip, String id,
      ComponentEventListener<ClickEvent<Button>> listener) {
    Button button = new Button(icon.create());
    Optional.ofNullable(id).ifPresent(button::setId);
    button.setTooltipText(tooltip);
    button.setAriaLabel(tooltip);
    button.addClickListener(listener);
    return button;
  }

  /**
   * Attaches a toolbar with viewer manipulation controls.
   * <p>
   * The toolbar includes buttons for Rotate, Flip, and Hide/Show Code.
   * <p>
   * <b>Important:</b> This method only constructs the toolbar UI. The caller is responsible for
   * adding listeners and implementing the logic.
   *
   * @see #addSourceRotateListener(ComponentEventListener)
   * @see #addSourceFlipListener(ComponentEventListener)
   * @see #addSourceCollapseListener(ComponentEventListener)
   *
   */
  public void createToolbar() {
    if (hasToolbar) {
      return;
    }

    Div toolbar = new Div();
    toolbar.setClassName("commons-demo-code-toolbar");

    Div buttons = new Div(
        new Span(),
        createButton(CommonsDemoIcons.ROTATE, "Rotate", "rotate", ev -> {
          fireEvent(new SourceRotateEvent(this, true));
        }), createButton(CommonsDemoIcons.FLIP, "Flip", "flip", ev -> {
          fireEvent(new SourceFlipEvent(this, true));
        }), createButton(CommonsDemoIcons.HIDE_SOURCE, "Close", "hide-source", ev -> {
          fireEvent(new SourceCollapseChangedEvent(this, true, true));
        }), createButton(CommonsDemoIcons.SHOW_SOURCE, "Show source", "show-source", ev -> {
          fireEvent(new SourceCollapseChangedEvent(this, true, false));
        }),
        new Span());
    buttons.setClassName("commons-demo-code-toolbar-buttons");

    toolbar.add(buttons);

    toolbar.addAttachListener(ev -> {
      toolbar.getElement().executeJs("""
             let buttonsWidth = 0;
             let hideSourceButtonWidth = 0;

             this._adjust = (parentWidth) => {
                 this.style.width = `${parentWidth}px`;
                 buttonsWidth = buttonsWidth || this.querySelector('.commons-demo-code-toolbar-buttons').clientWidth;

                 const shouldBeNarrow = parentWidth <= buttonsWidth;
                 if (buttonsWidth && shouldBeNarrow !== this._isNarrow) {
                     this.classList.toggle('narrow', shouldBeNarrow);
                     this._isNarrow = shouldBeNarrow;
                 }

                 hideSourceButtonWidth = hideSourceButtonWidth || this.querySelector('#hide-source').clientWidth;
                 const shouldBeCollapsed = parentWidth <= hideSourceButtonWidth + 8;
                 if (hideSourceButtonWidth && shouldBeCollapsed !== this._isCollapsed) {
                     console.error("shouldBeCollapsed: "+shouldBeCollapsed);
                     this._isCollapsed = shouldBeCollapsed;
                     this.dispatchEvent(new CustomEvent('source-collapse-changed', {
                         bubbles: true,
                         detail: { collapsed : shouldBeCollapsed }
                    }));
                 }
             };

             this._resizeObserver = new ResizeObserver(entries => {
                 for (let entry of entries) {
                     if (entry.target == this.parentElement) {
                         this._adjust(entry.contentRect.width);
                     }
                 }
             });

             this._resizeObserver.observe(this.parentElement);
             this._adjust(this.parentElement.clientWidth);
          """);
    });

    hasToolbar = true;
    add(toolbar);
  }

  @Override
  public void addSourceCollapseListener(
      ComponentEventListener<SourceCollapseChangedEvent> listener) {
    addListener(SourceCollapseChangedEvent.class, listener);
  }

  @Override
  public void addSourceFlipListener(
      ComponentEventListener<SourceFlipEvent> listener) {
    addListener(SourceFlipEvent.class, listener);
  }

  @Override
  public void addSourceRotateListener(
      ComponentEventListener<SourceRotateEvent> listener) {
    addListener(SourceRotateEvent.class, listener);
  }

}

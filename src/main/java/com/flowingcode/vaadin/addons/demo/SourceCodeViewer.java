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

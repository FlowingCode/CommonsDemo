/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2023 Flowing Code
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
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.VaadinService;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.Map;

@SuppressWarnings("serial")
@JsModule("./code-viewer.ts")
@NpmPackage(value = "lit", version = "2.5.0")
public class SourceCodeViewer extends Div implements HasSize {

  private final Element codeViewer;

  public SourceCodeViewer(String sourceUrl) {
    this(sourceUrl, null);
  }

  public SourceCodeViewer(String sourceUrl, Map<String, String> properties) {
    String url = translateSource(sourceUrl);
    codeViewer = new Element("code-viewer");
    getElement().appendChild(codeViewer);
    getElement().getStyle().set("display", "flex");
    codeViewer.getStyle().set("flex-grow", "1");
    setProperties(properties);
    addAttachListener(
        ev -> {
          codeViewer.executeJs("this.fetchContents($0,$1)", url, "java");
        });
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

  public static void highlightOnHover(Component c, String id) {
    c.addAttachListener(ev -> {
      c.getElement().executeJs("Vaadin.Flow.fcCodeViewerConnector.highlightOnHover(this,$0)", id);
    });
  }

  public static void highlight(String id) {
    UI.getCurrent().getElement().executeJs("Vaadin.Flow.fcCodeViewerConnector.highlight($0)", id);
  }

}

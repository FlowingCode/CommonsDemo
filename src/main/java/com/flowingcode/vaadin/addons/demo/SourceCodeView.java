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
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.VaadinService;

@SuppressWarnings("serial")
@JsModule("./code-viewer.ts")
@NpmPackage(value = "lit", version = "2.5.0")
class SourceCodeView extends Div implements HasSize {

  public SourceCodeView(String sourceUrl) {
    String url = translateSource(sourceUrl);
    Element codeViewer = new Element("code-viewer");
    getElement().appendChild(codeViewer);
    getElement().getStyle().set("display", "flex");
    codeViewer.getStyle().set("flex-grow", "1");
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
}

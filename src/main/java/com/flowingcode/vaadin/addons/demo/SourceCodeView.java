/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2021 Flowing Code
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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;

@SuppressWarnings("serial")
@NpmPackage(value = "polymer-code-highlighter", version = "1.0.5")
@JsModule("polymer-code-highlighter/code-highlighter.js")
class SourceCodeView extends Div implements HasSize {

  private String sourceUrl;

  public SourceCodeView(String sourceUrl) {
    this.sourceUrl = translateSource(sourceUrl);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);

    getElement().executeJs(
        "var self=this;"
      + "var xhr = new XMLHttpRequest();"
      + "xhr.onreadystatechange = function() {"
      + "if (this.readyState == 4 && this.status == 200) {" 
      + "  var elem = document.createElement('code-highlighter');"
      + "  elem.setAttribute('lang','java');"
      + "  elem.innerHTML = this.responseText;" 
      + "  self.appendChild(elem);"
      + "}};"
      + "xhr.open('GET', $0, true);"
      +  "xhr.send();", sourceUrl);    
  }

  private static String translateSource(String url) {
    if (url.startsWith("https://github.com")) {
      url = url.replaceFirst("github.com", "raw.githubusercontent.com");
      url = url.replaceFirst("/blob", "");
    }
    return url;
  }

}

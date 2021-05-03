/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 Flowing Code
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

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.IFrame;

@SuppressWarnings("serial")
class SourceCodeView extends Composite<IFrame> {

  public SourceCodeView(String sourceUrl) {
    getContent().getElement().setAttribute("frameborder", "0");
    getContent().setMinHeight("0");
    getContent().setMinWidth("0");
    getContent().getElement().setAttribute("srcdoc", getSrcdoc(sourceUrl));
    getContent().setSizeFull();
  }

  private String getSrcdoc(String sourceUrl) {
    return "<html style=\"overflow-y:hidden; height:100%;\"><body style=\"overflow-y: scroll; height:100%;\"><script src=\"https://gist-it.appspot.com/"
        + sourceUrl
        + "\"></script></body></html>";
  }

  public void setSizeFull() {
    getContent().setSizeFull();
  }
}

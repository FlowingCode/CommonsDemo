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
package com.flowingcode.vaadin.addons.demo.it;

import com.flowingcode.vaadin.addons.demo.SourceCodeViewer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import java.util.HashMap;
import java.util.Map;

@Route("it/view")
public class SourceCodeViewerView extends Div implements HasUrlParameter<String> {

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    Map<String, String> properties = null;
    if (parameter != null) {
      properties = new HashMap<>();
      for (String s : parameter.split(";")) {
        String param[] = s.split("=");
        properties.put(param[0], param[1]);
      }
      if (properties.isEmpty()) {
        properties = null;
      }
    }

    String url = event.getLocation().getQueryParameters().getQueryString();
    add(new SourceCodeViewer(url, properties));
  }

}

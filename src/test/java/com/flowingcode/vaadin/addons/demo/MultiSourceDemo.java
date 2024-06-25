/*-
 * #%L
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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "demo/multisource", layout = Demo.class)
@PageTitle("Demo with multiple sources")
// show-source @DemoSource
// show-source @DemoSource(clazz = AdditionalSources.class)
// show-source @DemoSource("/src/test/resources/META-INF/resources/frontend/multi-source-demo.css")
@DemoSource
@DemoSource(clazz = AdditionalSources.class)
@DemoSource("/src/test/resources/META-INF/resources/frontend/multi-source-demo.css")
@StyleSheet("./multi-source-demo.css")
public class MultiSourceDemo extends Div {
  public MultiSourceDemo() {

    // begin-block main
    Div div = new Div("This is the main source");
    div.addClassName("custom-style");
    SourceCodeViewer.highlightOnHover(div, "main");
    add(div);
    // end-block

    Button button1 = new Button("Highlight code in AdditionalSources");
    SourceCodeViewer.highlightOnClick(button1, "AdditionalSources.java#fragment");
    add(button1);
  }

}

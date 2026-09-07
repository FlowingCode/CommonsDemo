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

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "demo/highlight", layout = Demo.class)
@PageTitle("Highlight")
@DemoSource
@DemoSource("/src/test/resources/META-INF/resources/frontend/highlight-demo.css")
@StyleSheet("context://frontend/highlight-demo.css")
public class SampleDemoHighlight extends Div {

  public SampleDemoHighlight() {
    add(new Span("Highlight source fragments"));

    // begin-block first
    Div first = new Div(new Text("Highlight on hover (first)"));
    SourceCodeViewer.highlightOnHover(first, "first"); // show-source
    first.addClassName("dashed");
    add(first);
    // end-block

    // begin-block second
    Div second = new Div(new Text("Highlight on hover (second)"));
    SourceCodeViewer.highlightOnHover(second, "second"); // show-source
    second.addClassName("dashed");
    add(second);
    // end-block

    Div third = new Div(new Text("Highlight on hover (CSS)"));
    SourceCodeViewer.highlightOnHover(third, "highlight-demo.css#dashed"); // show-source
    third.addClassName("dashed");
    add(third);

    HorizontalLayout hl = new HorizontalLayout();

    // begin-block button
    Button button = new Button("Highlight on click");
    SourceCodeViewer.highlightOnClick(button, "button"); // show-source
    add(button);
    // end-block

    hl.add(new Button("Highlight Off", ev -> {
      SourceCodeViewer.highlight(null); // show-source
    }));

    add(hl);
  }
}

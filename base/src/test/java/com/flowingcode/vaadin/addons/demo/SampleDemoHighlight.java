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
@StyleSheet("./highlight-demo.css")
public class SampleDemoHighlight extends Div {

  public SampleDemoHighlight() {
    add(new Span("Highlight source fragments"));

    // begin-block first
    Div first = new Div(new Text("First"));
    SourceCodeViewer.highlightOnHover(first, "first");
    first.addClassName("dashed"); // hide-source
    add(first);
    // end-block

    // begin-block second
    Div second = new Div(new Text("Second"));
    SourceCodeViewer.highlightOnHover(second, "second");
    second.addClassName("dashed"); // hide-source
    add(second);
    // end-block

    HorizontalLayout hl = new HorizontalLayout();

    // begin-block button
    Button button = new Button("Click me");
    SourceCodeViewer.highlightOnClick(button, "button");
    add(button);
    // end-block

    hl.add(new Button("Highlight Off", ev -> {
      SourceCodeViewer.highlight(null);
    }));

    add(hl);
  }
}

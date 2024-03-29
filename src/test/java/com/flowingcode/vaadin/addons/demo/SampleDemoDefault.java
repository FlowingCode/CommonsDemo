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

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "demo/demo-with-source", layout = Demo.class)
@PageTitle("Demo with source")
@DemoSource
public class SampleDemoDefault extends Div {

  public SampleDemoDefault() {
    add(new Span("Demo component with defaulted @DemoSource annotation"));
    // show-source System.out.println("this line will be displayed in the code snippet");
    this.getClass(); // hide-source (this line will not be displayed in the code snippet)
    // #if vaadin ge 23
    // show-source System.out.println("conditional code for Vaadin 23+");
    // #elif vaadin ge 14
    // show-source System.out.println("conditional code for Vaadin 14-22");
    // #endif
  }
}

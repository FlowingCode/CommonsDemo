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

import com.flowingcode.vaadin.addons.GithubLink;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route("demo")
@RouteAlias("")
@GithubLink("https://github.com/FlowingCode/CommonsDemo")
public class Demo extends TabbedDemo {

  public Demo() {
    addDemo(new LegacyDemo());
    addDemo(SampleDemo.class);
    addDemo(SampleDemoDefault.class);
    addDemo(SampleDemoHighlight.class);
    addDemo(AdHocDemo.class);
    addDemo(MultiSourceDemo.class);
    addDemo(SourcePositionDemo.class);
  }
}

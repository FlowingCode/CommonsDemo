/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2026 Flowing Code
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

import com.flowingcode.vaadin.addons.GithubLink;
import com.flowingcode.vaadin.addons.demo.AdHocDemo;
import com.flowingcode.vaadin.addons.demo.DemoSource;
import com.flowingcode.vaadin.addons.demo.TabbedDemo;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@GithubLink("https://github.com/FlowingCode/CommonsDemo")
public class TabbedDemoView extends TabbedDemo {

  public TabbedDemoView() {
    addDemo(TabbedDemoViewNoSource.class);
    addDemo(TabbedDemoViewSingleSource.class);
    addDemo(TabbedDemoViewMultiSource.class);
    addDemo(TabbedDemoViewConditionalTrue.class);
    addDemo(TabbedDemoViewConditionalFalse.class);
  }

  protected abstract static class AbstractDemoView extends Div {
    public AbstractDemoView() {
      add(new Span(this.getClass().getSimpleName()));
    }
  }

  @Route(value = "it/tabbed-demo-no-source", layout = TabbedDemoView.class)
  public static class TabbedDemoViewNoSource extends AbstractDemoView { }

  @DemoSource(clazz = TabbedDemoView.class)
  @Route(value = "it/tabbed-demo-single-source", layout = TabbedDemoView.class)
  public static class TabbedDemoViewSingleSource extends AbstractDemoView { }

  @DemoSource(clazz = TabbedDemoView.class)
  @DemoSource(clazz = AdHocDemo.class)
  @Route(value = "it/tabbed-demo-multi-source", layout = TabbedDemoView.class)
  public static class TabbedDemoViewMultiSource extends AbstractDemoView { }

  @DemoSource(clazz = TabbedDemoView.class, condition = "vaadin ge 14")
  @Route(value = "it/tabbed-demo-conditional-true", layout = TabbedDemoView.class)
  public static class TabbedDemoViewConditionalTrue extends AbstractDemoView { }

  @DemoSource(clazz = TabbedDemoView.class, condition = "vaadin eq 0")
  @Route(value = "it/tabbed-demo-conditional-false", layout = TabbedDemoView.class)
  public static class TabbedDemoViewConditionalFalse extends AbstractDemoView { }
}

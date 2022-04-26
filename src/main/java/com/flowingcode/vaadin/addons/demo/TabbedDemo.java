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

import java.util.Optional;
import com.flowingcode.vaadin.addons.GithubLink;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

@StyleSheet("context://frontend/styles/commons-demo/shared-styles.css")
@SuppressWarnings("serial")
public class TabbedDemo extends VerticalLayout implements RouterLayout, BeforeEnterObserver {

  private RouteTabs tabs;
  private HorizontalLayout footer;
  private SplitLayoutDemo currentLayout;
  private Checkbox orientationCB;
  private Checkbox codeCB;

  public TabbedDemo() {
    tabs = new RouteTabs();
    tabs.setWidthFull();

    // Footer
    orientationCB = new Checkbox("Toggle Orientation");
    orientationCB.setValue(true);
    orientationCB.addClassName("smallcheckbox");
    orientationCB.addValueChangeListener(
        cb -> {
          updateSplitterOrientation();
        });
    codeCB = new Checkbox("Show Source Code");
    codeCB.setValue(true);
    codeCB.addClassName("smallcheckbox");
    codeCB.addValueChangeListener(
        cb -> {
          updateSplitterPosition();
        });
    footer = new HorizontalLayout();
    footer.setWidthFull();
    footer.setJustifyContentMode(JustifyContentMode.END);
    footer.add(codeCB, orientationCB);
        
    this.add(tabs);
    this.add(new Div());
    this.add(footer);
    setSizeFull();
  }

  /**
   * Add a tab with a {@code demo} component. The tab label and source code URL are retrieved from
   * the {@link PageTitle} (required) and {@link DemoSource} (optional) annotations in the demo
   * class, respectively.
   *
   * @param demo the demo instance
   */
  @Deprecated
  public void addDemo(Component demo) {
    DemoSource demoSource = demo.getClass().getAnnotation(DemoSource.class);

    String label =
        Optional.ofNullable(demo.getClass().getAnnotation(PageTitle.class))
            .map(PageTitle::value)
            .orElse(demo.getClass().getSimpleName());

    addDemo(demo, label, null);
  }

  /**
   * @param demo the demo instance
   * @param label the demo name (tab label)
   * @param sourceCodeUrl ignored.
   */
  @Deprecated
  public void addDemo(Component demo, String label, String sourceCodeUrl) {
    tabs.addLegacyTab(label, demo);
  }

  /** Add a tab with a demo component.
   *
   * @param clazz the class of routed demo view component
   * @param label the demo name (tab label)
   */
  public void addDemo(Class<? extends Component> clazz, String label) {
    if(!clazz.isAnnotationPresent(Route.class)) {
      throw new IllegalArgumentException(clazz + " must be annotated as Route");
    }
    RouterLink tab = new RouterLink(label, clazz);
    tabs.add(tab);
  }

  /**
   * Add a tab with a {@code demo} component. The tab label is retrieved from
   * the {@link PageTitle} annotations in the demo class.
   *
   * @param clazz the class of routed demo view component
   */
  public void addDemo(Class<? extends Component> clazz) {
    String label =
        Optional.ofNullable(clazz.getClass().getAnnotation(PageTitle.class))
            .map(PageTitle::value)
            .orElse(clazz.getSimpleName());

    addDemo(clazz, label);
  }

  @Deprecated
  public void addDemo(Component demo, String label) {
    addDemo(demo, label, null);
  }

  @Override
  public void showRouterLayoutContent(HasElement content) {
    Component demo = (Component)content;
    if (!demo.getId().isPresent()) {
      demo.setId("content");
    }

    DemoSource demoSource = demo.getClass().getAnnotation(DemoSource.class);
    String sourceCodeUrl = null;
    if (demoSource != null) {
      sourceCodeUrl = demoSource.value();
      if (sourceCodeUrl.equals(DemoSource.GITHUB_SOURCE)) {
        sourceCodeUrl = Optional.ofNullable(this.getClass().getAnnotation(GithubLink.class))
            .map(githubLink -> githubLink.value() + "/blob/master/src/test/java/"
                + demo.getClass().getName().replace('.', '/') + ".java")
            .orElse(null);
      }
      content = new SplitLayoutDemo(demo, sourceCodeUrl);
      currentLayout = (SplitLayoutDemo) content;
      updateSplitterPosition();
      updateSplitterOrientation();
      this.footer.setVisible(true);
    } else {
      currentLayout = null;
      this.footer.setVisible(false);
    }    
    this.getElement().insertChild(1, content.getElement());
  }
  
  @Override
  public void removeRouterLayoutContent(HasElement oldContent) {
    this.getElement().removeChild(1);
  }
  
  private void updateSplitterPosition() {
    boolean b = codeCB.getValue();
    if (b) {
      currentLayout.setSplitterPosition(50);
      orientationCB.setEnabled(true);
    } else {
      currentLayout.setSplitterPosition(100);
      orientationCB.setEnabled(false);
    }
  }

  private void updateSplitterOrientation() {
    boolean b = orientationCB.getValue();
    if (b) {
      currentLayout.setOrientation(Orientation.HORIZONTAL);
    } else {
      currentLayout.setOrientation(Orientation.VERTICAL);
    }
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    if(TabbedDemo.class.isAssignableFrom(event.getNavigationTarget())) {
      RouterLink first = tabs.getFirstRoute();
      if(first != null) {
        event.forwardTo(first.getHref()); 
      }       
    }
  }
 
}

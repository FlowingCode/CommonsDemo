/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2024 Flowing Code
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

import com.flowingcode.vaadin.addons.GithubBranch;
import com.flowingcode.vaadin.addons.GithubLink;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StyleSheet("context://frontend/styles/commons-demo/shared-styles.css")
@SuppressWarnings("serial")
public class TabbedDemo extends VerticalLayout implements RouterLayout {

  private static final Logger logger = LoggerFactory.getLogger(TabbedDemo.class);

  private static final int MOBILE_DEVICE_BREAKPOINT_WIDTH = 768;
  private boolean autoVisibility;
  private EnhancedRouteTabs tabs;
  private HorizontalLayout footer;
  private SplitLayoutDemo currentLayout;
  private Checkbox orientationCB;
  private Checkbox codeCB;
  private Checkbox codePositionCB;
  private Checkbox themeCB;
  private Orientation splitOrientation;
  private Button helperButton;
  private DemoHelperViewer demoHelperViewer;

  public TabbedDemo() {
    demoHelperViewer = new DialogDemoHelperViewer();

    tabs = new EnhancedRouteTabs();

    // Footer
    orientationCB = new Checkbox("Toggle Orientation");
    orientationCB.setValue(true);
    orientationCB.addClassName("smallcheckbox");
    orientationCB.addValueChangeListener(ev -> {
      if (ev.isFromClient()) {
        toggleSplitterOrientation();
      }
    });
    codeCB = new Checkbox("Show Source Code");
    codeCB.setValue(true);
    codeCB.addClassName("smallcheckbox");
    codeCB.addValueChangeListener(ev -> updateSplitterPosition());
    codePositionCB = new Checkbox("Toggle Code Position");
    codePositionCB.setValue(true);
    codePositionCB.addClassName("smallcheckbox");
    codePositionCB.addValueChangeListener(ev -> toggleSourcePosition());
    themeCB = new Checkbox("Dark Theme");
    themeCB.setValue(false);
    themeCB.addClassName("smallcheckbox");
    themeCB.addValueChangeListener(cb -> {
      applyTheme(getElement(), themeCB.getValue());
    });
    footer = new HorizontalLayout();
    footer.setWidthFull();
    footer.setJustifyContentMode(JustifyContentMode.END);
    footer.add(codeCB, codePositionCB, orientationCB, themeCB);
    footer.setClassName("demo-footer");

    Package pkg = this.getClass().getPackage();
    String title = pkg.getImplementationTitle();
    String version = pkg.getImplementationVersion();
    if (title != null && version != null) {
      Div footerLeft = new Div();
      footer.setFlexGrow(1, footerLeft);
      footer.addComponentAsFirst(footerLeft);

      footerLeft.add(new Span(title + " " + version));
    }

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
    String label = Optional.ofNullable(demo.getClass().getAnnotation(PageTitle.class))
        .map(PageTitle::value).orElse(demo.getClass().getSimpleName());

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
    updateVisibility();
  }

  /**
   * Add a tab with a demo component.
   *
   * @param clazz the class of routed demo view component
   * @param label the demo name (tab label)
   */
  public void addDemo(Class<? extends Component> clazz, String label) {
    if (!clazz.isAnnotationPresent(Route.class)) {
      throw new IllegalArgumentException(clazz + " must be annotated as Route");
    }
    tabs.add(label, clazz);
    updateVisibility();
  }

  private void updateVisibility() {
    if (autoVisibility) {
      tabs.setVisible(tabs.getContent().getTabCount() > 1);
    }
  }

  /**
   * Sets the autovisibility mode. When autovisibility is enabled, the tabs component is hidden
   * unless it contains two or more tabs.
   */
  public void setAutoVisibility(boolean autoVisibility) {
    this.autoVisibility = autoVisibility;
    updateVisibility();
  }

  /**
   * Add a tab with a {@code demo} component. The tab label is retrieved from the {@link PageTitle}
   * annotations in the demo class.
   *
   * @param clazz the class of routed demo view component
   */
  public void addDemo(Class<? extends Component> clazz) {
    String label = Optional.ofNullable(clazz.getAnnotation(PageTitle.class)).map(PageTitle::value)
        .orElse(clazz.getSimpleName());

    addDemo(clazz, label);
  }

  @Deprecated
  public void addDemo(Component demo, String label) {
    addDemo(demo, label, null);
  }

  @Override
  public void showRouterLayoutContent(HasElement content) {
    Component demo = (Component) content;
    if (!demo.getId().isPresent()) {
      demo.setId("content");
    }

    if (helperButton != null) {
      remove(helperButton);
      helperButton = null;
    }

    DemoSource[] demoSources = demo.getClass().getAnnotationsByType(DemoSource.class);
    List<SourceCodeTab> sourceTabs = new ArrayList<>(demoSources.length);
    for (DemoSource demoSource : demoSources) {
      createSourceCodeTab(demo.getClass(), demoSource).ifPresent(sourceTabs::add);
    }

    if (!sourceTabs.isEmpty()) {
      content = new SplitLayoutDemo(demo, sourceTabs);
      currentLayout = (SplitLayoutDemo) content;
      if (splitOrientation != null) {
        setOrientation(splitOrientation);
        updateSplitterPosition();
      }

      if (currentLayout != null) {
        setupDemoHelperButton(currentLayout.getContent().getPrimaryComponent().getClass());
      }
    } else {
      currentLayout = null;
      demo.getElement().getStyle().set("height", "100%");
      setupDemoHelperButton(content.getClass());
    }
    
    updateFooterButtonsVisibility();
    getElement().insertChild(1, content.getElement());

    applyTheme(getElement(), getThemeName());
  }

  private Optional<SourceCodeTab> createSourceCodeTab(Class<?> annotatedClass, DemoSource annotation) {
    String demoFile;
    String url = annotation.value();
    if (url.equals(DemoSource.GITHUB_SOURCE)) {
      String className;
      if (annotation.clazz() == DemoSource.class) {
        className = annotatedClass.getName().replace('.', '/');
      } else {
        className = annotation.clazz().getName().replace('.', '/');
      }
      demoFile = "src/test/java/" + className + ".java";
    } else if (url.startsWith("/src/test/")) {
      demoFile = url.substring(1);
    } else {
      demoFile = null;
    }

    if (demoFile != null) {
      String branch = lookupGithubBranch(this.getClass());
      url = Optional.ofNullable(this.getClass().getAnnotation(GithubLink.class))
          .map(githubLink -> String.format("%s/blob/%s/%s", githubLink.value(),
              branch, demoFile))
          .orElse(null);
    }
    
    if (url==null) {
      return Optional.empty();
    }

    SourceCodeTab.SourceCodeTabBuilder builder = SourceCodeTab.builder();
    builder.url(url);
    
    if (!annotation.caption().equals(DemoSource.DEFAULT_VALUE)) {
      builder.caption(annotation.caption());
    }
    
    if (!annotation.language().equals(DemoSource.DEFAULT_VALUE)) {
      builder.language(annotation.caption());
    }

    builder.sourcePosition(annotation.sourcePosition());
    
    return Optional.of(builder.build());
  }
  
  public static String lookupGithubBranch(Class<? extends TabbedDemo> clazz) {
    GithubBranch branch = clazz.getAnnotation(GithubBranch.class);
    if (branch == null) {
      Package pkg = clazz.getPackage();
      if (pkg!=null) {
        branch = pkg.getAnnotation(GithubBranch.class);
      }
    }
    return Optional.ofNullable(branch).map(GithubBranch::value).orElse("master");
  }

  @Override
  public void removeRouterLayoutContent(HasElement oldContent) {
    getElement().removeChild(1);
  }

  private void updateSplitterPosition() {
    if (currentLayout != null) {
      if (codeCB.getValue()) {
        currentLayout.showSourceCode();
      } else {
        currentLayout.hideSourceCode();
      }
      orientationCB.setEnabled(codeCB.getValue());
      codePositionCB.setEnabled(codeCB.getValue());
    }
  }

  public void setSourceVisible(boolean visible) {
    codeCB.setValue(visible);
    codePositionCB.setVisible(visible);
  }

  public void toggleSourcePosition() {
    if (currentLayout != null) {
      currentLayout.toggleSourcePosition();
    }
  }

  private void toggleSplitterOrientation() {
    if (currentLayout == null) {
      return;
    }
    if (Orientation.HORIZONTAL.equals(splitOrientation)) {
      splitOrientation = Orientation.VERTICAL;
    } else {
      splitOrientation = Orientation.HORIZONTAL;
    }
    setOrientation(splitOrientation);
  }

  public Orientation getOrientation() {
    return currentLayout.getOrientation();
  }

  public void setOrientation(Orientation orientation) {
    splitOrientation = orientation;
    if (currentLayout != null) {
      currentLayout.setOrientation(orientation);
      currentLayout.setSplitterPosition(50);
    }
    orientationCB.setValue(Orientation.HORIZONTAL.equals(orientation));
  }

  private static final String THEME_NAME = TabbedDemo.class.getName() + "#THEME_NAME";

  public static String getThemeName() {
    return (String) Optional.ofNullable(VaadinSession.getCurrent().getAttribute(THEME_NAME))
        .orElse("");
  }

  @Deprecated
  public static void applyTheme(Element element, boolean useDarkTheme) {
    String theme = useDarkTheme ? "dark" : "";
    applyTheme(element, theme);
  }

  public static void applyTheme(Element element, String theme) {
    VaadinSession.getCurrent().setAttribute(THEME_NAME, theme);

    element.executeJs("document.documentElement.setAttribute('theme', $0);", theme);

    Component c = element.getComponent().get();
    collectThemeChangeObservers(c).forEach(observer -> observer.onThemeChange(theme));
  }

  private static Stream<ThemeChangeObserver> collectThemeChangeObservers(Component c) {
    Stream<ThemeChangeObserver> children =
        c.getChildren().flatMap(child -> collectThemeChangeObservers(child));
    return c instanceof ThemeChangeObserver o ? Stream.concat(Stream.of(o), children) : children;
  }

  private void updateFooterButtonsVisibility() {
    boolean hasSourceCode = currentLayout != null;
    ComponentUtil.fireEvent(this, new TabbedDemoSourceEvent(this, hasSourceCode));
    orientationCB.setVisible(hasSourceCode);
    codeCB.setVisible(hasSourceCode);
    codePositionCB.setVisible(hasSourceCode);
  }

  public void addTabbedDemoSourceListener(ComponentEventListener<TabbedDemoSourceEvent> listener) {
    ComponentUtil.addListener(this, TabbedDemoSourceEvent.class, listener);
    listener.onComponentEvent(new TabbedDemoSourceEvent(this, currentLayout != null));
  }


  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    getUI().ifPresent(ui -> ui.getPage().retrieveExtendedClientDetails(receiver -> {
      boolean mobile = receiver.getBodyClientWidth() <= MOBILE_DEVICE_BREAKPOINT_WIDTH;
      codeCB.setValue(codeCB.getValue() && !mobile);
      codePositionCB.setValue(codeCB.getValue() && !mobile);

      boolean portraitOrientation = receiver.getBodyClientHeight() > receiver.getBodyClientWidth();
      adjustSplitOrientation(portraitOrientation);
    }));
  }

  private void adjustSplitOrientation(boolean portraitOrientation) {
    if (portraitOrientation) {
      splitOrientation = Orientation.VERTICAL;
    } else {
      splitOrientation = Orientation.HORIZONTAL;
    }
    setOrientation(splitOrientation);
  }

  public void setDemoHelperViewer(DemoHelperViewer demoHelperViewer) {
    this.demoHelperViewer =
        Objects.requireNonNull(demoHelperViewer, "Demo helper viewer cannot be null");
  }

  private void setupDemoHelperButton(Class<?> helperClass) {
    if (helperClass.isAnnotationPresent(DemoHelper.class)) {
      DemoHelper demoHelper = helperClass.getAnnotation(DemoHelper.class);
      try {
        final DemoHelperRenderer demoHelperRenderer = demoHelper.renderer().newInstance();
        helperButton = new Button(demoHelper.icon().create());
        helperButton.getElement().setAttribute("title", demoHelper.tooltipText());
        helperButton.addClassName("helper-button");
        helperButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        helperButton
            .addClickListener(e -> demoHelperViewer.show(demoHelperRenderer.helperContent()));
        add(helperButton);
      } catch (Exception e) {
        logger.error("Error creating an instance",e);
      }
    }
  }

}

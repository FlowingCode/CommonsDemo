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
package com.flowingcode.vaadin.addons.demo;

import com.flowingcode.vaadin.addons.GithubBranch;
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
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A layout for displaying a tabbed demo with source code integration.
 * <p>
 * This layout consists of a set of tabs for navigating between different demos,
 * a content area for displaying the current demo, and a footer with controls
 * for
 * toggling the source code visibility, orientation, and theme.
 * </p>
 */
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

  /**
   * Constructs a new TabbedDemo instance.
   */
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
      boolean useDarkTheme = themeCB.getValue();
      setColorScheme(this, useDarkTheme ? ColorScheme.DARK : ColorScheme.LIGHT);
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
   * Add a tab with a demo component.
   *
   * @param clazz the class of routed demo view component
   * @param label the demo name (tab label)
   * @throws IllegalArgumentException if the class is not annotated with {@link Route}
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
   *
   * @param autoVisibility {@code true} to enable autovisibility, {@code false} to disable it
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
      currentLayout = new SplitLayoutDemo(demo, sourceTabs);
      if (currentLayout.isEmpty()) {
        demo.getElement().removeAttribute("slot");
        currentLayout = null;
      }
    }

    if (currentLayout != null) {
      content = currentLayout;
      if (splitOrientation != null) {
        setOrientation(splitOrientation);
        updateSplitterPosition();
      }

      setupDemoHelperButton(currentLayout.getContent().getPrimaryComponent().getClass());
    } else {
      currentLayout = null;
      demo.getElement().getStyle().set("height", "100%");
      setupDemoHelperButton(content.getClass());
    }

    updateFooterButtonsVisibility();
    getElement().insertChild(1, content.getElement());

    setColorScheme(this, getColorScheme());
  }

  private static SourceUrlResolver resolver = null;

  /**
   * Configures the {@code SourceUrlResolver} for resolving source URLs. This method can only be
   * called once; subsequent calls will result in an exception.
   *
   * @param resolver The {@code SourceUrlResolver} to be used. Must not be {@code null}.
   * @throws IllegalStateException if a resolver has already been set.
   * @throws NullPointerException if the provided {@code resolver} is  {@code null}.
   */
  public static void configureSourceUrlResolver(@NonNull SourceUrlResolver resolver) {
    if (TabbedDemo.resolver != null) {
      throw new IllegalStateException();
    }
    TabbedDemo.resolver = resolver;
  }

  private static SourceUrlResolver getResolver() {
    if (resolver == null) {
      resolver = new DefaultSourceUrlResolver();
    }
    return resolver;
  }

  private Optional<SourceCodeTab> createSourceCodeTab(Class<?> annotatedClass, DemoSource annotation) {
    String url = getResolver().resolveURL(this, annotatedClass, annotation).orElse(null);

    if (url == null) {
      return Optional.empty();
    }

    SourceCodeTab.SourceCodeTabBuilder builder = SourceCodeTab.builder();
    builder.url(url);

    if (!annotation.caption().equals(DemoSource.DEFAULT_VALUE)) {
      builder.caption(annotation.caption());
    }

    if (!annotation.language().equals(DemoSource.DEFAULT_VALUE)) {
      builder.language(annotation.language());
    }

    if (!annotation.condition().isEmpty()) {
      builder.condition(annotation.condition());
    }

    builder.sourcePosition(annotation.sourcePosition());

    return Optional.of(builder.build());
  }

  /**
   * Looks up the GitHub branch name associated with the given TabbedDemo class.
   *
   * @param clazz the TabbedDemo class to inspect
   * @return the GitHub branch name, or "master" if the annotation is not found
   */
  public static String lookupGithubBranch(Class<? extends TabbedDemo> clazz) {
    GithubBranch branch = clazz.getAnnotation(GithubBranch.class);
    if (branch == null) {
      Package pkg = clazz.getPackage();
      if (pkg != null) {
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

  /**
   * Sets the visibility of the source code.
   *
   * @param visible {@code true} to make the source code visible, {@code false} otherwise
   */
  public void setSourceVisible(boolean visible) {
    codeCB.setValue(visible);
    codePositionCB.setVisible(visible);
  }

  /**
   * Toggles the position of the source code relative to the demo content.
   */
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

  /**
   * Returns the current orientation of the split layout.
   *
   * @return the current orientation
   */
  public Orientation getOrientation() {
    return currentLayout.getOrientation();
  }

  /**
   * Sets the orientation of the split layout.
   *
   * @param orientation the new orientation
   */
  public void setOrientation(Orientation orientation) {
    splitOrientation = orientation;
    if (currentLayout != null) {
      currentLayout.setOrientation(orientation);
      currentLayout.setSplitterPosition(50);
    }
    orientationCB.setValue(Orientation.HORIZONTAL.equals(orientation));
  }

  /**
   * Returns the theme attribute value.
   * <p>
   * The "theme attribute" is either an empty string (light) or "dark".
   * </p>
   *
   * @deprecated Use {@link #getColorScheme()}
   * @return the theme attribute value
   */
  @Deprecated
  public static String getThemeAttribute() {
    ColorScheme scheme = getColorScheme();
    return scheme == ColorScheme.LIGHT ? "" : scheme.getValue();
  }

  /**
   * Returns the current color scheme.
   *
   * @return the current color scheme
   */
  public static ColorScheme getColorScheme() {
    return Optional.ofNullable(VaadinSession.getCurrent().getAttribute(ColorScheme.class))
        .orElse(ColorScheme.LIGHT);
  }

  /**
   * Applies the theme attribute to the given element.
   * <p>
   * The "theme attribute" is either an empty string (light) or "dark".
   * </p>
   *
   * @param element the element to apply the theme to
   * @param theme   the theme attribute value
   * @deprecated Use {@link #setColorScheme(Component, ColorScheme)}
   */
  @Deprecated
  public static void applyThemeAttribute(Element element, String theme) {
    Component c = element.getComponent().get();
    if (theme.isEmpty()) {
      setColorScheme(c, ColorScheme.LIGHT);
    } else if (theme.equals("dark")) {
      setColorScheme(c, ColorScheme.DARK);
    }
  }

  /**
   * Sets the color scheme for the given component.
   *
   * @param component   the component to apply the color scheme to
   * @param colorScheme the color scheme to apply
   */
  public static void setColorScheme(Component component, @NonNull ColorScheme colorScheme) {
    VaadinSession.getCurrent().setAttribute(ColorScheme.class, colorScheme);
    String theme = colorScheme.getValue();

    Element element = component.getElement();
    String script;
    if (element.getTag().equalsIgnoreCase("iframe")) {
      script = "let e = this.contentWindow.document.documentElement;";
    } else {
      script = "let e = document.documentElement;";
    }

    script += """
        e.setAttribute('theme', $0);
        e.style.colorScheme = $0;
        """;

    element.executeJs(script, theme);

    collectThemeChangeObservers(component).forEach(observer -> observer.onThemeChange(theme));
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

  /**
   * Adds a listener for {@link TabbedDemoSourceEvent}.
   *
   * @param listener the listener to add
   */
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

  /**
   * Sets the {@link DemoHelperViewer} to be used for displaying demo helpers.
   *
   * @param demoHelperViewer the viewer to set
   */
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
        logger.error("Error creating an instance", e);
      }
    }
  }

}

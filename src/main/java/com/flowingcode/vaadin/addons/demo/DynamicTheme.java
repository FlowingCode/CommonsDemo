package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.page.Inline.Position;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.Version;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration representing supported themes for dynamic switching.
 * <p>
 * This enum facilitates switching between themes (e.g., Lumo, Aura) at runtime.
 * </p>
 */
@RequiredArgsConstructor
public enum DynamicTheme {

  /**
   * The standard Lumo theme.
   */
  LUMO("lumo/lumo.css", "hsl(214, 35%, 21%)"),

  /**
   * The standard Aura theme.
   */
  AURA("aura/aura.css", "oklch(0.2 0.01 260)"),

  /**
   * A base theme without specific styling.
   */
  BASE(null, "#000");

  @Getter
  private final String href;

  @Getter
  private final String bgColor;


  private static void assertFeatureSupported() {
    if (!isFeatureSupported()) {
      throw new UnsupportedOperationException("Dynamic theme switching requires Vaadin 25+");
    }
  }

  /**
   * Checks if the dynamic theme feature is supported. The feature is supported in Vaadin 25.
   *
   * @return {@code true} if the feature is supported and initialized; {@code false} otherwise.
   */
  public static boolean isFeatureSupported() {
    return Version.getMajorVersion() >= 25;
  }

  private static void assertFeatureInitialized() {
    assertFeatureSupported();
    if (!isFeatureInitialized()) {
      throw new IllegalStateException("Dynamic theme switching has not been initialized");
    }
  }

  /**
   * Checks if the dynamic theme feature has been initialized for the current session.
   *
   * @return {@code true} if the feature is supported and initialized; {@code false} otherwise.
   */
  public static boolean isFeatureInitialized() {
    return isFeatureSupported()
        && VaadinSession.getCurrent().getAttribute(DynamicTheme.class) != null;
  }

  /**
   * Return the current dynamic theme.
   *
   * @throws UnsupportedOperationException if the runtime Vaadin version is older than 25.
   * @return the current dynamic theme, or {@code null} if the feature has not been initialized.
   */
  public static DynamicTheme getCurrent() {
    assertFeatureSupported();
    return VaadinSession.getCurrent().getAttribute(DynamicTheme.class);
  }

  /**
   * Initializes the theme settings.
   * <p>
   * This method performs a lazy initialization of the {@link DynamicTheme} within the
   * current {@link VaadinSession}. If no theme is present, it registers this instance
   * as the session default. Subsequently, it injects the corresponding CSS stylesheet
   * link into the {@link AppShellSettings}.
   * </p>
   *
   * @param settings the application shell settings to be modified
   * @throws UnsupportedOperationException if the runtime Vaadin version is older than 25
   */
  public void initialize(AppShellSettings settings) {
    assertFeatureSupported();

    DynamicTheme theme = getCurrent();
    if (theme == null) {
      theme = this;
      VaadinSession.getCurrent().setAttribute(DynamicTheme.class, theme);
    }

    switch (theme) {
      case AURA:
        settings.addLink(Position.APPEND, "stylesheet", "aura/aura.css");
        break;
      case LUMO:
        settings.addLink(Position.APPEND, "stylesheet", "lumo/lumo.css");
        break;
      default:
        break;
    }
  }

  /**
   * Prepares the component for dynamic theme switching by preloading stylesheets.
   * <p>
   * Adds a client-side listener to the component that detects mouseover events.
   * When triggered, it preloads the theme stylesheets (Lumo and Aura) to ensure
   * they can be applied immediately when needed.
   * </p>
   *
   * @param component the component to attach the listener to
   * @throws IllegalStateException if the dynamic theme feature has not been initialized
   */
  public static void prepare(Component component) {
    assertFeatureInitialized();

    component.addAttachListener(ev -> doPrepare(component));
    if (component.isAttached()) {
      doPrepare(component);
    }
  }

  private static void doPrepare(Component component) {
    component.getElement().executeJs("""
        this.addEventListener('mouseover', function() {
           ["lumo/lumo.css", "aura/aura.css"].forEach(href=> {
                let link = document.querySelector(`link[href="${href}"]`);
                if (!link) {
                   link = document.createElement("link");
                   link.href = href;
                   link.as   = 'style';
                   link.rel  = 'preload';
                   document.head.prepend(link);
                }
            });
        }, {once:true} );
        """);
  }

  /**
   * Applies this theme to the view.
   *
   * @param component a component in the view
   * @throws IllegalStateException if the dynamic theme feature has not been initialized
   */
  public void apply(HasElement component) {
    assertFeatureInitialized();

    VaadinSession.getCurrent().setAttribute(DynamicTheme.class, this);
    component.getElement().executeJs("""
        const applyTheme = () => {
          ["lumo/lumo.css", "aura/aura.css"].forEach(href=> {
            let link = document.querySelector(`link[href='${href}']`);
            if (!link) return;
            if (href === $0) {
               if (link.rel === 'preload') link.rel = 'stylesheet';
               if (link.disabled) link.disabled = false;
            } else if (link.rel === 'stylesheet' && !link.disabled) {
               link.disabled = true;
            }
          });
        };

        if (document.startViewTransition) {
          document.startViewTransition(applyTheme);
        } else {
          applyTheme();
        }
        """, href);
  }

}

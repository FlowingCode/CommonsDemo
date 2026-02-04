package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.communication.IndexHtmlRequestListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Service initialization listener that automatically applies a dynamic theme.
 * <p>
 * If the dynamic theme feature is supported, this listener checks for the presence of a
 * {@code /META-INF/dynamic-theme.properties} file. If found, it reads the {@code theme} property
 * (e.g., {@code theme=LUMO}) and registers an {@link IndexHtmlRequestListener} to initialize the
 * theme for all requests.
 * </p>
 */
@SuppressWarnings("serial")
public class DynamicThemeInitializer implements VaadinServiceInitListener {

  @Override
  public void serviceInit(ServiceInitEvent event) {
    if (DynamicTheme.isFeatureSupported()) {
      try (InputStream in = getClass().getResourceAsStream("/META-INF/dynamic-theme.properties")) {
        if (in != null) {
          Properties props = new Properties();
          props.load(in);
          String themeName = props.getProperty("theme");
          if (themeName != null) {
            DynamicTheme theme = DynamicTheme.valueOf(themeName.trim());
            event.addIndexHtmlRequestListener(theme::initialize);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException("Error reading dynamic-theme.properties", e);
      }
    }
  }

}

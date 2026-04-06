package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.communication.IndexHtmlRequestListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger logger = LoggerFactory.getLogger(DynamicThemeInitializer.class);

  private static final String PROPERTIES_PATH = "META-INF/dynamic-theme.properties";

  @Override
  public void serviceInit(ServiceInitEvent event) {
    if (DynamicTheme.isFeatureSupported()) {
      try {
        Enumeration<URL> resources = getClass().getClassLoader().getResources(PROPERTIES_PATH);
        while (resources.hasMoreElements()) {
          URL url = resources.nextElement();
          String source = getSourceName(url);
          readTheme(url).ifPresent(theme -> {
            logger.info("Applying dynamic theme '{}' from {}", theme, source);
            event.addIndexHtmlRequestListener(theme::initialize);
          });
        }
      } catch (IOException e) {
        throw new RuntimeException("Error reading dynamic-theme.properties", e);
      }
    }
  }

  private Optional<DynamicTheme> readTheme(URL url) throws IOException {
    try (InputStream in = url.openStream()) {
      Properties props = new Properties();
      props.load(in);
      String themeName = props.getProperty("theme");
      return Optional.ofNullable(themeName).map(String::trim).map(DynamicTheme::valueOf);
    }
  }

  // Extracts a short, readable source name from the URL.
  private String getSourceName(URL url) {
    String path = url.getPath();
    // JAR URLs look like: file:/path/to/file.jar!/META-INF/...
    int jarSeparator = path.indexOf("!/");
    if (jarSeparator > 0) {
      path = path.substring(0, jarSeparator);
      int lastSlash = path.lastIndexOf('/');
      if (lastSlash >= 0) {
        return path.substring(lastSlash + 1);
      }
    }
    return url.toString();
  }

}

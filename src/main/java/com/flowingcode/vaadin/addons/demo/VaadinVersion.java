package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.server.Version;
import java.util.Objects;
import java.util.Properties;
import org.slf4j.LoggerFactory;

public class VaadinVersion {

  private VaadinVersion() {
    throw new UnsupportedOperationException();
  }

  public static String getVaadinVersion() {
    try {
      Properties pom = new Properties();
      pom.load(VaadinVersion.class
          .getResourceAsStream("/META-INF/maven/com.vaadin/vaadin-core/pom.properties"));
      return Objects.requireNonNull((String) pom.get("version"));
    } catch (Exception e) {
      LoggerFactory.getLogger(Version.class.getName())
          .warn("Unable to determine Vaadin version number", e);
      return null;
    }
  }

}

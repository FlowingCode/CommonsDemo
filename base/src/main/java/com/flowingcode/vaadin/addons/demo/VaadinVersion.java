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

import com.vaadin.flow.server.Version;
import java.util.Objects;
import java.util.Properties;
import org.slf4j.LoggerFactory;

public class VaadinVersion {

  private static String version;

  private VaadinVersion() {
    throw new UnsupportedOperationException();
  }

  public static String getVaadinVersion() {
    if (version == null) {
      try {
        Properties pom = new Properties();
        pom.load(VaadinVersion.class
            .getResourceAsStream("/META-INF/maven/com.vaadin/vaadin-core/pom.properties"));
        version = Objects.requireNonNull((String) pom.get("version"));
      } catch (Exception e) {
        LoggerFactory.getLogger(Version.class.getName())
            .warn("Unable to determine Vaadin version number", e);
      }
    }
    return version;
  }

}

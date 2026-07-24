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
package com.flowingcode.vaadin.addons.demo.it;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;

public abstract class AbstractSourceCodeViewerIT extends AbstractViewTest {

  SourceCodeViewerElement viewer;

  private String expected;

  public AbstractSourceCodeViewerIT() {
    super(null);
  }

  private String getResourceName() {
    String method = new Throwable().getStackTrace()[2].getMethodName();
    return method.replaceFirst("^test", "");
  }

  protected String open(String... args) {
    return openSource(getResourceName(), "java", args);
  }

  /** Opens the stylesheet resource named after the test method. */
  protected String openCss(String... args) {
    return openSource(getResourceName(), "css", args);
  }

  private String openSource(String resource, String extension, String... args) {
    if (viewer != null) {
      throw new IllegalStateException();
    }

    String path = "com/flowingcode/vaadin/addons/demo/it/" + resource;
    String params = Stream.of(args).map(Object::toString).collect(Collectors.joining(";"));
    getDriver()
        .get(getURL(String.format("it/view/%s?src/test/resources/%s.%s", params, path, extension)));
    viewer = $(SourceCodeViewerElement.class).waitForFirst();
    return viewer.getText();
  }

  private String getExpectedText(String resource) {
    resource += ".txt";
    InputStream in = this.getClass().getResourceAsStream(resource);
    if (in == null) {
      throw new MissingResourceException(resource, null, null);
    }
    try {
      return new String(IOUtils.toByteArray(in), "UTF-8").trim().replaceAll("\r", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected final String expected() {
    return getExpectedText(getResourceName());
  }

}

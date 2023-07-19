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

  protected void open(String resource, String... args) {
    if (viewer != null) {
      throw new IllegalStateException();
    }
    expected = getExpectedText(resource);

    String path = "com/flowingcode/vaadin/addons/demo/it/" + resource;
    String params = Stream.of(args).map(Object::toString).collect(Collectors.joining(";"));
    getDriver().get(getURL(String.format("it/view/%s?src/test/resources/%s.java", params, path)));
    viewer = $(SourceCodeViewerElement.class).waitForFirst();
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
    return expected;
  }

  protected String getText() {
    return viewer.getText();
  }

}

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

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;
import org.openqa.selenium.By;

@Element("code-viewer")
public class SourceCodeViewerElement extends TestBenchElement {

  private static final String LANGUAGE_PREFIX = "language-";

  /**
   * Returns the language that was used for formatting the source, as identified by the
   * {@code language-} class of the rendered code, or {@code null} if there is none.
   */
  public String getLanguage() {
    String className = findElement(By.tagName("code")).getAttribute("class");
    if (className != null) {
      for (String s : className.split("\\s+")) {
        if (s.startsWith(LANGUAGE_PREFIX)) {
          return s.substring(LANGUAGE_PREFIX.length());
        }
      }
    }
    return null;
  }

}

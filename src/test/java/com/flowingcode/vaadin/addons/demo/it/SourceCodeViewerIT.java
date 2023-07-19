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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SourceCodeViewerIT extends AbstractSourceCodeViewerIT {

  @Test
  public void testSimpleSource() {
    open("SimpleSource");
    assertEquals(expected(), getText());
  }

  @Test
  public void testHideSource() {
    open("HideSource");
    assertEquals(expected(), getText());
  }

  @Test
  public void testShowSource() {
    open("ShowSource");
    assertEquals(expected(), getText());
  }

  @Test
  public void testPackageCleanup() {
    open("PackageCleanup");
    assertEquals(expected(), getText());
  }

  @Test
  public void testAnnotationCleanup() {
    open("AnnotationCleanup");
    assertEquals(expected(), getText());
  }

  @Test
  public void testLicenseCleanup() {
    open("LicenseCleanup");
    assertEquals(expected(), getText());
  }

}

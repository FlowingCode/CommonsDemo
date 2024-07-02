/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2024 Flowing Code
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

public class ConditionalSourceCodeViewerIT extends AbstractSourceCodeViewerIT {

  private static final String VAADIN_VERSION = "vaadin=23.4.5";

  @Test
  public void testConditionEq() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionNe() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionLt() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionLe() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionGt() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionGe() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionElse() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionElif() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionNested() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }

  @Test
  public void testConditionUnknownVariable() {
    assertEquals(expected(), open(VAADIN_VERSION));
  }
}

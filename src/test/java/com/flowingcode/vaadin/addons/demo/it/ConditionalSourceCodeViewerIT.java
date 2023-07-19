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
}

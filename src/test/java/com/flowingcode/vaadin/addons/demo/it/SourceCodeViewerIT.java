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

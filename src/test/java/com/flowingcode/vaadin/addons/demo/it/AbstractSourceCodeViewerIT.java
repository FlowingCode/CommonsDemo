package com.flowingcode.vaadin.addons.demo.it;

import java.io.IOException;
import java.io.InputStream;
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

  private static String getExpectedText(String resource) {
    InputStream in = AbstractSourceCodeViewerIT.class.getResourceAsStream(resource + ".txt");
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

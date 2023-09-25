package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import java.util.List;
import java.util.Map;

public class MultiSourceCodeViewer extends Div {

  private static final String DATA_URL = "source-url";
  private static final String DATA_LANGUAGE = "source-language";

  private SourceCodeViewer codeViewer;

  public MultiSourceCodeViewer(List<SourceCodeTab> sourceCodeTabs, Map<String, String> properties) {

    Tab tab;
    if (sourceCodeTabs.size() > 1) {
      Tabs tabs = new Tabs(createTabs(sourceCodeTabs));
      tabs.addSelectedChangeListener(ev -> onTabSelected(ev.getSelectedTab()));
      add(tabs);
      tab = tabs.getSelectedTab();
    } else {
      tab = createTab(sourceCodeTabs.get(0));
    }

    String url = (String) ComponentUtil.getData(tab, DATA_URL);
    String language = (String) ComponentUtil.getData(tab, DATA_LANGUAGE);
    codeViewer = new SourceCodeViewer(url, language, properties);

    add(codeViewer);
    codeViewer.getStyle().set("flex-grow", "1");
    getStyle().set("display", "flex");
    getStyle().set("flex-direction", "column");
  }

  private Tab[] createTabs(List<SourceCodeTab> sourceCodeTabs) {
    return sourceCodeTabs.stream().map(this::createTab).toArray(Tab[]::new);
  }

  private Tab createTab(SourceCodeTab sourceCodeTab) {
    String url = sourceCodeTab.getUrl();
    String language = sourceCodeTab.getLanguage();
    String caption = sourceCodeTab.getCaption();

    String filename = getFilename(url);
    if (caption == null) {
      caption = filename;
    }

    if (language == null) {
      String ext = getExtension(filename);
      switch (ext) {
        case "java":
          language = "java";
          break;
        case "css":
          language = "css";
          break;
        case "js":
          language = "js";
          break;
        case "ts":
          language = "ts";
          break;
        default:
          language = "unknown";
          break;
      }
    }

    Tab tab = new Tab(caption);
    ComponentUtil.setData(tab, DATA_URL, url);
    ComponentUtil.setData(tab, DATA_LANGUAGE, language);
    return tab;
  }

  private String getFilename(String url) {
    int i = url.lastIndexOf('/');
    return i >= 0 ? url.substring(i + 1) : url;
  }

  private String getExtension(String filename) {
    int i = filename.lastIndexOf('.');
    return i >= 0 ? filename.substring(i + 1) : filename;
  }

  private void onTabSelected(Tab tab) {
    String url = (String) ComponentUtil.getData(tab, DATA_URL);
    String language = (String) ComponentUtil.getData(tab, DATA_LANGUAGE);
    fetchContents(url, language);
  }

  private void fetchContents(String url, String language) {
    codeViewer.fetchContents(url, language);
  }

}

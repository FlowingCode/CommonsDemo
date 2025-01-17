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
package com.flowingcode.vaadin.addons.demo;

import com.flowingcode.vaadin.addons.enhancedtabs.EnhancedTabs;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import elemental.json.JsonValue;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MultiSourceCodeViewer extends Div {

  private static final String DATA_URL = "source-url";
  private static final String DATA_LANGUAGE = "source-language";
  private static final String DATA_POSITION = "source-position";

  private SourceCodeViewer codeViewer;
  private Tab selectedTab;
  private EnhancedTabs tabs;

  public MultiSourceCodeViewer(List<SourceCodeTab> sourceCodeTabs, Map<String, String> properties) {
    if (sourceCodeTabs.size() > 1) {
      tabs = new EnhancedTabs(createTabs(sourceCodeTabs));
      tabs.addSelectedChangeListener(ev -> onTabSelected(ev.getSelectedTab()));
      add(tabs);
      selectedTab = tabs.getSelectedTab();

      getElement().addEventListener("fragment-request", ev -> {
        JsonValue filename = ev.getEventData().get("event.detail.filename");
        findTabWithFilename(Optional.ofNullable(filename).map(JsonValue::asString).orElse(null))
            .ifPresent(tab -> {
          tabs.setSelectedTab(tab);
        });
      }).addEventData("event.detail.filename");
    } else {
      selectedTab = createTab(sourceCodeTabs.get(0));
    }

    String url = (String) ComponentUtil.getData(selectedTab, DATA_URL);
    String language = (String) ComponentUtil.getData(selectedTab, DATA_LANGUAGE);
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
    ComponentUtil.setData(tab, DATA_POSITION, sourceCodeTab.getSourcePosition());
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
    selectedTab = tab;

    String url = (String) ComponentUtil.getData(tab, DATA_URL);
    String language = (String) ComponentUtil.getData(tab, DATA_LANGUAGE);
    fetchContents(url, language);
  }

  private void fetchContents(String url, String language) {
    codeViewer.fetchContents(url, language);
  }

  public SourcePosition getSourcePosition() {
    return (SourcePosition) ComponentUtil.getData(selectedTab, DATA_POSITION);
  }

  private Optional<Tab> findTabWithFilename(String filename) {
    if (tabs != null) {
      return tabs.getTabs().filter(tab -> {
        String url = (String) ComponentUtil.getData(tab, DATA_URL);
        return filename == null || getFilename(url).equals(filename);
      }).findFirst();
    } else {
      return Optional.empty();
    }
  }

}

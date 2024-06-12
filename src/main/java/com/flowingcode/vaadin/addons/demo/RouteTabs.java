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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Extension of Tabs in order to allow to bind tabs with Routes.
 *
 * @see https://cookbook.vaadin.com/tabs-with-routes/a
 * @deprecated Deprecated for removal. Use {@link EnhancedRouteTabs} instead.
 */
@SuppressWarnings("serial")
@Deprecated(forRemoval = true)
public class RouteTabs extends Tabs implements BeforeEnterObserver {

  private final Map<RouterLink, Tab> routerLinkTabMap = new LinkedHashMap<>();

  public void add(RouterLink routerLink) {
    routerLink.setHighlightCondition(HighlightConditions.sameLocation());
    routerLink.setHighlightAction(
        (link, shouldHighlight) -> {
          if (shouldHighlight) {
            setSelectedTab(routerLinkTabMap.get(routerLink));
          }
        });
    routerLinkTabMap.put(routerLink, new Tab(routerLink));
    add(routerLinkTabMap.get(routerLink));
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    setSelectedTab(null);
    if (TabbedDemo.class.isAssignableFrom(event.getNavigationTarget())) {
      RouterLink first = getFirstRoute();
      if (first != null) {
        event.forwardTo(first.getHref());
      } else {
        getChildren().findFirst().ifPresent(tab -> setSelectedTab((Tab) tab));
      }
    }
  }

  public Map<RouterLink, Tab> getRouterLinkTabMap() {
    return routerLinkTabMap;
  }

  public RouterLink getFirstRoute() {
    Optional<RouterLink> first =
        routerLinkTabMap.entrySet().stream().map(Map.Entry::getKey).findFirst();
    return first.isPresent() ? first.get() : null;
  }

  @Deprecated
  public void addLegacyTab(String label, Component content) {
    Tab tab = new Tab(label);
    add(tab);
    addSelectedChangeListener(
        ev -> {
          if (ev.getSelectedTab() == tab) {
            TabbedDemo tabbedDemo = (TabbedDemo) getParent().get();
            String route = tabbedDemo.getClass().getAnnotation(Route.class).value();
            UI.getCurrent().getPage().getHistory().pushState(null, new Location(route));
            tabbedDemo.removeRouterLayoutContent(null);
            tabbedDemo.showRouterLayoutContent(content);
          }
        });
  }
}

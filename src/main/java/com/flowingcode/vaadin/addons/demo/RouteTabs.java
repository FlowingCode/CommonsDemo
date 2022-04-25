package com.flowingcode.vaadin.addons.demo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

/**
 * Extension of Tabs in order to allow to bind tabs with Routes.
 * 
 * @see https://cookbook.vaadin.com/tabs-with-routes/a
 */
public class RouteTabs extends Tabs implements BeforeEnterObserver {

  private final Map<RouterLink, Tab> routerLinkTabMap = new LinkedHashMap<>();

  public void add(RouterLink routerLink) {
    routerLink.setHighlightCondition(HighlightConditions.sameLocation());
    routerLink.setHighlightAction((link, shouldHighlight) -> {
      if (shouldHighlight)
        setSelectedTab(routerLinkTabMap.get(routerLink));
    });
    routerLinkTabMap.put(routerLink, new Tab(routerLink));
    add(routerLinkTabMap.get(routerLink));
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    // In case no tabs will match
    setSelectedTab(null);
  }

  public Map<RouterLink, Tab> getRouterLinkTabMap() {
    return routerLinkTabMap;
  }

  public RouterLink getFirstRoute() {
    Optional<RouterLink> first =
        routerLinkTabMap.entrySet().stream().map(Map.Entry::getKey).findFirst();
    return first.isPresent() ? first.get() : null;
  }
}

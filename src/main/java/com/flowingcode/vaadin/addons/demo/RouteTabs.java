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
 */
public class RouteTabs extends Tabs implements BeforeEnterObserver {

  private final Map<RouterLink, Tab> routerLinkTabMap = new LinkedHashMap<>();

  public void add(RouterLink routerLink) {
    routerLink.setHighlightCondition(HighlightConditions.sameLocation());
    routerLink.setHighlightAction((link, shouldHighlight) -> {
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
    addSelectedChangeListener(ev -> {
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

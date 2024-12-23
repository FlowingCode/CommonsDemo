package com.flowingcode.vaadin.addons.demo;

/**
 * Any attached component implementing this interface will receive an event when a new theme is
 * applied.
 */
@FunctionalInterface
public interface ThemeChangeObserver {

  void onThemeChange(String themeName);

}

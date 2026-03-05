/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2026 Flowing Code
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

/**
 * Any attached component implementing this interface will receive an event when a new theme is
 * applied.
 * <p>
 * Observers are notified twice to support backward compatibility:
 * first via the deprecated
 * {@link #onThemeChange(String) onThemeChange(String theme)} and then via the
 * {@link #onThemeChange(ThemeChangeEvent) onThemeChange(ThemeChangeEvent)}.
 * </p>
 */
public interface ThemeChangeObserver {

  /**
   * Called when a theme change occurs.
   *
   * @param themeName the name of the new theme
   * @deprecated Use {@link #onThemeChange(ThemeChangeEvent)} instead.
   */
  @Deprecated(forRemoval = true, since = "5.3.0")
  default void onThemeChange(String themeName) {
  };

  /**
   * Called when a theme change occurs.
   *
   * @param event the theme change event
   */
  default void onThemeChange(ThemeChangeEvent event) {
  }

}

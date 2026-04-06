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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import java.util.Optional;
import lombok.Getter;

/**
 * Event fired when a theme change occurs in the application. It contains information about the new
 * {@link ColorScheme} and {@link DynamicTheme}.
 */
@SuppressWarnings("serial")
public class ThemeChangeEvent extends ComponentEvent<Component> {

  @Getter
  private final ColorScheme colorScheme;

  private final DynamicTheme dynamicTheme;

  /**
   * Constructs a new {@code ThemeChangeEvent}.
   *
   * @param source the source component of the event
   * @param fromClient true if the event originated from the client side, false otherwise
   * @param colorScheme the new color scheme applied
   * @param dynamicTheme the new dynamic theme applied (may be null)
   */
  public ThemeChangeEvent(Component source, boolean fromClient, ColorScheme colorScheme, DynamicTheme dynamicTheme) {
    super(source, fromClient);
    this.colorScheme = colorScheme;
    this.dynamicTheme = dynamicTheme;
  }

  /**
   * Returns the dynamic theme applied, if any.
   *
   * @return an {@code Optional} containing the dynamic theme, or empty if dynamic theming is not
   *         initialized.
   */
  public Optional<DynamicTheme> getDynamicTheme() {
    return Optional.ofNullable(dynamicTheme);
  }

}

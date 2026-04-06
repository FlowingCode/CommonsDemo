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

import lombok.Getter;
/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of supported color scheme values.
 * <p>
 * These values correspond to the CSS color-scheme property values and control how the browser
 * renders UI elements and how the application responds to system color scheme preferences.
 */
@RequiredArgsConstructor
public enum ColorScheme {
  /**
   * Light color scheme. The application will use a light theme regardless of system preferences.
   */
  LIGHT("light"),

  /**
   * Dark color scheme. The application will use a dark theme regardless of system preferences.
   */
  DARK("dark");

  @Getter
  private final String value;

}

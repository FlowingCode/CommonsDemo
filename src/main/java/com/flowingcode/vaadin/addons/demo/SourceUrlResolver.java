/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2025 Flowing Code
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

import java.util.Optional;

/**
 * Interface for resolving the source URL of a demo class.
 *
 * @author Javier Godoy / Flowing Code
 */
public interface SourceUrlResolver {

  /**
   * Resolves the source URL for a given demo class and annotation.
   *
   * @param demo The {@link TabbedDemo} instance associated with the source.
   * @param annotatedClass The class that is annotated with {@link DemoSource}.
   * @param annotation The {@link DemoSource} annotation providing source metadata.
   * @return An {@link Optional} containing the resolved URL if available, otherwise an empty
   *         {@link Optional}.
   */
  Optional<String> resolveURL(TabbedDemo demo, Class<?> annotatedClass, DemoSource annotation);
  
}

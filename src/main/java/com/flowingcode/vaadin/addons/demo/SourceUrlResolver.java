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

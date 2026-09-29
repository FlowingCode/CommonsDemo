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

import com.vaadin.flow.component.page.AppShellConfigurator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables dynamic theme switching with the given theme as the default.
 * <p>
 * This annotation must be placed on the {@link AppShellConfigurator} of the application. It
 * replaces calling {@link DynamicTheme#initialize(com.vaadin.flow.server.AppShellSettings)
 * DynamicTheme.initialize} from {@link AppShellConfigurator#configurePage configurePage}. Since
 * the annotation is read when the application starts, the default theme also applies to sessions
 * that did not load {@code index.html} (for instance, after a server restart, or when
 * {@code index.html} was served from a cache).
 * </p>
 * <p>
 * Dynamic theme switching is only available with Vaadin 25+. With older versions this annotation
 * has no effect, so an {@link AppShellConfigurator} that runs on several Vaadin versions can be
 * annotated as well. The {@link AppShellConfigurator} must not be annotated with the legacy
 * {@link com.vaadin.flow.theme.Theme @Theme} annotation.
 * </p>
 * <p>
 * The annotation is ignored if it is placed on a class that is not the {@link AppShellConfigurator}
 * of the application. It is inherited by subclasses of the annotated class.
 * </p>
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultDynamicTheme {

  /**
   * The default theme.
   *
   * @return the default theme
   */
  DynamicTheme value();

}

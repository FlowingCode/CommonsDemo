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

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.IconFactory;
import java.util.Locale;

/**
 * CommonsDemo icons.
 *
 * @author Javier Godoy / Flowing Code
 */
public enum CommonsDemoIcons implements IconFactory {
  ROTATE, FLIP, HIDE_SOURCE, SHOW_SOURCE;

  /**
   * The Iconset name, i.e. {@code "fab"}."
   */
  public static final String ICONSET = "commons-demo";

  /**
   * Return the full icon name.
   *
   * @return the full icon name, i.e. {@code "commons-demo:name"}..
   */
  public String getIconName() {
    return ICONSET + ':' + getIconPart();
  }

  /**
   * Return the icon name within the iconset.
   *
   * @return the icon name, i.e. {@code "name"}..
   */
  public String getIconPart() {
    return name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceFirst("^-", "");
  }

  /**
   * Create a new {@link Icon} instance with the icon determined by the name.
   *
   * @return a new instance of {@link Icon} component
   */
  @Override
  public Icon create() {
    return new Icon(getIconPart());
  }

  /**
   * Server side component for {@code Brands}
   */
  @JsModule("./commons-demo-iconset.ts")
  @SuppressWarnings("serial")
  public static final class Icon extends com.vaadin.flow.component.icon.Icon {
    private Icon(String icon) {
      super(ICONSET, icon);
    }
  }

}

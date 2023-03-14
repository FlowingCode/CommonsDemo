/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2023 Flowing Code
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
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;

@SuppressWarnings("serial")
class SplitLayoutDemo extends Composite<SplitLayout> {

  private SourceCodeView code;

  public SplitLayoutDemo(Component demo, String sourceUrl) {
    getContent().setOrientation(Orientation.HORIZONTAL);
    code = new SourceCodeView(sourceUrl);

    getContent().addToPrimary(demo);
    getContent().addToSecondary(code);
    getContent().setSizeFull();
  }

  public void setOrientation(Orientation o) {
    getContent().setOrientation(o);
    getContent()
        .getPrimaryComponent()
        .getElement()
        .setAttribute("style", "width: 100%; height: 100%");
    code.setSizeFull();
  }

  public Orientation getOrientation() {
    return getContent().getOrientation();
  }

  public void setSplitterPosition(int pos) {
    getContent().setSplitterPosition(pos);
  }

  public void setSizeFull() {
    getContent().setSizeFull();
  }
}

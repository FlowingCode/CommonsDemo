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
import com.vaadin.flow.component.dialog.Dialog;

/**
 * A simple {@link DemoHelperViewer} that displays help content from {@link DemoHelperRenderer} in a
 * {@link Dialog}.
 * 
 */
@SuppressWarnings("serial")
public class DialogDemoHelperViewer implements DemoHelperViewer {

  @Override
  public void show(Component content) {
    new Dialog(content).open();
  }

}

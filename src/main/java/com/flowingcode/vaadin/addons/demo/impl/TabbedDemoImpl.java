/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 Flowing Code
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
package com.flowingcode.vaadin.addons.demo.impl;

import com.flowingcode.vaadin.addons.demo.TabbedDemo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import java.util.HashMap;
import java.util.Map;

@StyleSheet("context://frontend/styles/commons-demo/shared-styles.css")
@SuppressWarnings("serial")
public class TabbedDemoImpl<T extends Component> extends VerticalLayout implements TabbedDemo {

	private Tabs tabs;
	private HorizontalLayout footer;
	private SplitLayoutDemo<Component> currentLayout;
	private Map<Tab, SplitLayoutDemo<Component>> demos;

	public TabbedDemoImpl(T demo, String name, String sourceCodeUrl) {
		tabs = new Tabs();
		demos = new HashMap<>();
		tabs.setWidthFull();
		// Footer
		Checkbox orientationCB = new Checkbox("Toggle Orientation");
		orientationCB.setValue(true);
		orientationCB.addClassName("smallcheckbox");
		orientationCB.addValueChangeListener(cb -> {
			if (cb.getValue()) {
				currentLayout.setOrientation(Orientation.HORIZONTAL);
			} else {
				currentLayout.setOrientation(Orientation.VERTICAL);
			}
			currentLayout.getContent().getPrimaryComponent().getElement().setAttribute("style",
					"width: 100%; height: 100%");
			currentLayout.getSourceCodeView().setSizeFull();
		});
		Checkbox codeCB = new Checkbox("Show Source Code");
		codeCB.setValue(true);
		codeCB.addClassName("smallcheckbox");
		codeCB.addValueChangeListener(cb -> {
			if (cb.getValue()) {
				currentLayout.setSplitterPosition(50);
				orientationCB.setEnabled(true);
			} else {
				currentLayout.setSplitterPosition(100);
				orientationCB.setEnabled(false);
			}
		});
		footer = new HorizontalLayout();
		footer.setWidthFull();
		footer.setJustifyContentMode(JustifyContentMode.END);
		footer.add(codeCB, orientationCB);

		addDemo(demo, name, sourceCodeUrl);

		tabs.addSelectedChangeListener(e -> {
			removeAll();
			currentLayout = demos.get(e.getSelectedTab());
			currentLayout.initDemo();
			this.add(tabs, currentLayout, footer);
		});

		this.add(tabs, currentLayout, footer);
		currentLayout.initDemo();
		setSizeFull();
		currentLayout.setSizeFull();
	}


	@Override
	public void addDemo(Component demo, String name, String sourceCodeUrl) {
		Tab tab = new Tab(name);
		tabs.add(tab);
		currentLayout = new SplitLayoutDemo<>(demo, sourceCodeUrl);
		demos.put(tab, currentLayout);
	}
}

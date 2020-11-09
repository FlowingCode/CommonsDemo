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
	private Map<Tab, Component> demos;
	private Checkbox orientationCB;
	private Checkbox codeCB;

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param demo          the demo instance
	 * @param name          the demo name, populates its related tab
	 * @param sourceCodeUrl the url of the demo, <b>null</b> to not show source code
	 *                      section.
	 */
	public TabbedDemoImpl(T demo, String name, String sourceCodeUrl) {
		tabs = new Tabs();
		demos = new HashMap<>();
		tabs.setWidthFull();
		// Footer
		orientationCB = new Checkbox("Toggle Orientation");
		orientationCB.setValue(true);
		orientationCB.addClassName("smallcheckbox");
		orientationCB.addValueChangeListener(cb -> {
			updateSplitterOrientation();
		});
		codeCB = new Checkbox("Show Source Code");
		codeCB.setValue(true);
		codeCB.addClassName("smallcheckbox");
		codeCB.addValueChangeListener(cb -> {
			updateSplitterPosition();
		});
		footer = new HorizontalLayout();
		footer.setWidthFull();
		footer.setJustifyContentMode(JustifyContentMode.END);
		footer.add(codeCB, orientationCB);

		if (sourceCodeUrl != null) {
			addDemo(demo, name, sourceCodeUrl);
		} else {
			addDemo(demo, name);
		}

		tabs.addSelectedChangeListener(e -> {
			removeAll();
			// If current demo is instance of SplitLayoutDemo, add footer.
			Component currentDemo = demos.get(tabs.getSelectedTab());
			if (currentDemo instanceof SplitLayoutDemo) {
				currentLayout = (SplitLayoutDemo<Component>) currentDemo;
				this.add(tabs, currentLayout, footer);
			}
			else {
				this.add(tabs, currentDemo);
			}
			updateSplitterPosition();
			updateSplitterOrientation();
		});

		// If current demo is instance of SplitLayoutDemo, add footer.
		Component currentDemo = demos.get(tabs.getSelectedTab());
		if (currentDemo instanceof SplitLayoutDemo) {
			currentLayout = (SplitLayoutDemo<Component>) currentDemo;
			this.add(tabs, currentLayout, footer);
		} else {
			this.add(tabs, currentDemo);
		}

		setSizeFull();
	}

	@Override
	public void addDemo(Component demo, String name, String sourceCodeUrl) {
		Tab tab = new Tab(name);
		tabs.add(tab);
		demos.put(tab, new SplitLayoutDemo<>(demo, sourceCodeUrl));
	}

	@Override
	public void addDemo(Component demo, String name) {
		Tab tab = new Tab(name);
		tabs.add(tab);
		demos.put(tab, demo);
	}

	private void updateSplitterPosition() {
		boolean b = codeCB.getValue();
		if (b) {
			currentLayout.setSplitterPosition(50);
			orientationCB.setEnabled(true);
		} else {
			currentLayout.setSplitterPosition(100);
			orientationCB.setEnabled(false);
		}
	}

	private void updateSplitterOrientation() {
		boolean b = orientationCB.getValue();
		if (b) {
			currentLayout.setOrientation(Orientation.HORIZONTAL);
		} else {
			currentLayout.setOrientation(Orientation.VERTICAL);
		}
	}
}

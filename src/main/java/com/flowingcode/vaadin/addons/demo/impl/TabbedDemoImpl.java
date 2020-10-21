package com.flowingcode.vaadin.addons.demo.impl;

import com.flowingcode.vaadin.addons.demo.TabbedDemo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class TabbedDemoImpl extends VerticalLayout implements TabbedDemo {

	private Tabs tabs;
	private HorizontalLayout footer;
	private SplitLayoutDemo<Component> currentLayout;
	private Map<Tab, SplitLayoutDemo<Component>> demos;

	public TabbedDemoImpl(Component demo, String name, String sourceCodeUrl) {
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
		HorizontalLayout footer = new HorizontalLayout();
		footer.setWidthFull();
		footer.setJustifyContentMode(JustifyContentMode.END);
		footer.add(codeCB, orientationCB);

		addDemo(demo, name, sourceCodeUrl);

		tabs.addSelectedChangeListener(e -> {
			removeAll();
			currentLayout = demos.get(e.getSelectedTab());
			this.add(tabs, currentLayout, footer);
		});

		this.add(tabs, currentLayout, footer);
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

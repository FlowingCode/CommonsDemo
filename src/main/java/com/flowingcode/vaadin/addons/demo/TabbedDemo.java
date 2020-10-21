package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.Component;

public interface TabbedDemo<T extends Component> {

	void addDemo(T demo, String name, String sourceCodeUrl);
}

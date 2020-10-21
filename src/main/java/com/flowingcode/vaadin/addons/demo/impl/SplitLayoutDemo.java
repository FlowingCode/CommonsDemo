package com.flowingcode.vaadin.addons.demo.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;

@SuppressWarnings("serial")
public class SplitLayoutDemo<T extends Component> extends Composite<SplitLayout> {

	private SourceCodeView code;

	public SplitLayoutDemo(T demo, String sourceUrl) {
		// Split layout
		getContent().setOrientation(Orientation.HORIZONTAL);
		code = new SourceCodeView(sourceUrl);

		getContent().addToPrimary(demo);
		getContent().addToSecondary(code);
		getContent().setSizeFull();
	}

	public SourceCodeView getSourceCodeView() {
		return code;
	}
	public void setOrientation(Orientation o) {
		getContent().setOrientation(o);
	}
	public void setSplitterPosition(int pos) {
		getContent().setSplitterPosition(pos);
	}
	public void setSizeFull() {
		getContent().setSizeFull();
	}
}

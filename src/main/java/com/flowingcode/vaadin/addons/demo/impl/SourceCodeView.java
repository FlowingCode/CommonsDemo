package com.flowingcode.vaadin.addons.demo.impl;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.IFrame;

@SuppressWarnings("serial")
public class SourceCodeView extends Composite<IFrame> {

	public SourceCodeView(String sourceUrl) {
		getContent().getElement().setAttribute("frameborder", "0");
		getContent().setMinHeight("0");
		getContent().setMinWidth("0");
		getContent().getElement().setAttribute("srcdoc", getSrcdoc(sourceUrl));
		getContent().setSizeFull();
	}

	private String getSrcdoc(String sourceUrl) {
		return "<html style=\"overflow-y:hidden; height:100%;\"><body style=\"overflow-y: scroll; height:100%;\"><script src=\"https://gist-it.appspot.com/"
				+ sourceUrl + "\"></script></body></html>";
	}

	public void setSizeFull() {
		getContent().setSizeFull();
	}
}

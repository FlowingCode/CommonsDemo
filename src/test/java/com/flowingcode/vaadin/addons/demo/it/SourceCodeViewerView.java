package com.flowingcode.vaadin.addons.demo.it;

import com.flowingcode.vaadin.addons.demo.SourceCodeViewer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import java.util.HashMap;
import java.util.Map;

@Route("it/view")
public class SourceCodeViewerView extends Div implements HasUrlParameter<String> {

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    Map<String, String> properties = null;
    if (parameter != null) {
      String[] ss = parameter.split(";");

      properties = new HashMap<>();
      for (int i = 1; i < ss.length; i++) {
        String param[] = ss[i].split("=");
        properties.put(param[0], param[1]);
      }

      if (properties.isEmpty()) {
        properties = null;
      }
    }

    String url = event.getLocation().getQueryParameters().getQueryString();
    add(new SourceCodeViewer(url, properties));
  }

}

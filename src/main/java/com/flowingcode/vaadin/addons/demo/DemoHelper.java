package com.flowingcode.vaadin.addons.demo;

import com.vaadin.flow.component.icon.VaadinIcon;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be place in views having an associated {@link DemoHelperRenderer}.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DemoHelper {

  /** Demo helper renderer class */
  Class<? extends DemoHelperRenderer> renderer();


  /** Demo helper button icon */
  VaadinIcon icon() default VaadinIcon.QUESTION_CIRCLE_O;

  /** Demo helper button tooltip text */
  String tooltipText() default "";


}

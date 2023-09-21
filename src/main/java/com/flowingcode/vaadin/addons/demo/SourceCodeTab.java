package com.flowingcode.vaadin.addons.demo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

@Getter
@With
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceCodeTab {
  private final String url;
  private String caption;
  private String language;
}

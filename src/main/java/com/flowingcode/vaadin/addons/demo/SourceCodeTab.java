package com.flowingcode.vaadin.addons.demo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceCodeTab {
  
  @NonNull
  private final String url;
  private String caption;
  private String language;
  
}

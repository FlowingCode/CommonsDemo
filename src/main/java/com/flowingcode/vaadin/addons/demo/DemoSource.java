/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2022 Flowing Code
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
package com.flowingcode.vaadin.addons.demo;

import com.flowingcode.vaadin.addons.GithubBranch;
import com.flowingcode.vaadin.addons.GithubLink;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used for configuring the source code URL in a {@link TabbedDemo}. If no {@code
 * value} is specified, and the demo view is annotated with {@link GithubLink}, then the source URL
 * defaults to the location of the annotated class under {@code src/java/test} and the branch is
 * determined from the value of {@link GithubBranch} in the demo view class (if the annotation is
 * present) or the containing package of the demo view class. If the source URL is defaulted and no
 * {@code GithubBranch} annotation is present either in the demo view class or its containing
 * package, then the branch defaults to {@code master}.
 *
 * @author Javier Godoy / Flowing Code
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DemoSource {

  static final String GITHUB_SOURCE = "__GITHUB__";

  /** A link to the source code, if different from the annotated class. */
  String value() default GITHUB_SOURCE;
}

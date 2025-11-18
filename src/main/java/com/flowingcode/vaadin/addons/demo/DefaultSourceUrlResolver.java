/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2025 Flowing Code
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
import java.util.Optional;

/**
 * Implementation of {@code SourceUrlResolver}.
 * <p>
 * If no {@code value} or {@code clazz} is specified, and the demo view is annotated with
 * {@link GithubLink}, then the source URL defaults to the location of the annotated class under
 * {@code src/test/java} and the branch is determined from the value of {@link GithubBranch} in the
 * demo view class (if the annotation is present) or the containing package of the demo view class.
 * If the source URL is defaulted and no {@code GithubBranch} annotation is present either in the
 * demo view class or its containing package, then the branch defaults to {@code master}.
 *
 * @author Javier Godoy / Flowing Code
 */
class DefaultSourceUrlResolver implements SourceUrlResolver {

  @Override
  public Optional<String> resolveURL(TabbedDemo demo, Class<?> annotatedClass,
      DemoSource annotation) {
    String demoFile;
    String url = annotation.value();
    if (url.equals(DemoSource.DEFAULT_VALUE)) {
      String className;
      if (annotation.clazz() == DemoSource.class) {
        className = annotatedClass.getName().replace('.', '/');
      } else {
        className = annotation.clazz().getName().replace('.', '/');
      }
      demoFile = "src/test/java/" + className + ".java";
    } else if (url.startsWith("/src/test/")) {
      demoFile = url.substring(1);
    } else {
      demoFile = null;
    }

    if (demoFile != null) {
      String branch = TabbedDemo.lookupGithubBranch(demo.getClass());
      return Optional.ofNullable(demo.getClass().getAnnotation(GithubLink.class))
          .map(githubLink -> String.format("%s/blob/%s/%s", githubLink.value(), branch, demoFile));
    } else {
      return Optional.empty();
    }
  }

}

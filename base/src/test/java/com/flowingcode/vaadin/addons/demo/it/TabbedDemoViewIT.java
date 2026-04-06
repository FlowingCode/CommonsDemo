/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2026 Flowing Code
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
package com.flowingcode.vaadin.addons.demo.it;

import static org.hamcrest.CoreMatchers.not;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewConditionalFalse;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewConditionalTrue;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewMultiSource;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewNoSource;
import com.flowingcode.vaadin.addons.demo.it.TabbedDemoView.TabbedDemoViewSingleSource;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;

public class TabbedDemoViewIT extends AbstractViewTest {

  private SourceCodeViewerElement viewer;

  protected void open(Class<? extends Component> clazz) {
    if (viewer != null) {
      throw new IllegalStateException();
    }
    getDriver().get(getURL(clazz.getAnnotation(Route.class).value()));
    getCommandExecutor().waitForVaadin();
    getDriver().findElement(By.id("content"));
  }

  public static Matcher<SearchContext> hasElement(String cssSelector) {
    return new TypeSafeMatcher<>() {

      @Override
      protected boolean matchesSafely(SearchContext container) {
        return !container.findElements(By.cssSelector(cssSelector)).isEmpty();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("a page containing element: ").appendValue(cssSelector);
      }

      @Override
      protected void describeMismatchSafely(SearchContext container,
          Description mismatchDescription) {
        mismatchDescription.appendText("no elements matched selector: ").appendValue(cssSelector);
      }
    };
  }

  private Matcher<SearchContext> hasPrimaryCodeTabs() {
    return hasElement("[slot='primary'] vaadin-menu-bar");
  }

  private Matcher<SearchContext> hasPrimaryCodeViewer() {
    return hasElement("[slot='primary'] code-viewer");
  }

  private Matcher<SearchContext> hasSecondaryCodeTabs() {
    return hasElement("[slot='secondary'] vaadin-menu-bar");
  }

  private Matcher<SearchContext> hasSecondaryCodeViewer() {
    return hasElement("[slot='secondary'] code-viewer");
  }

  private void assertThat(Matcher<SearchContext> matcher) {
    Assert.assertThat(getDriver(), matcher);
  }

  @Test
  public void testSimpleNoSource() {
    open(TabbedDemoViewNoSource.class);
    assertThat(not(hasPrimaryCodeTabs()));
    assertThat(not(hasPrimaryCodeViewer()));
    assertThat(not(hasSecondaryCodeTabs()));
    assertThat(not(hasSecondaryCodeViewer()));
  }

  @Test
  public void testSimpleSingleSource() {
    open(TabbedDemoViewSingleSource.class);
    assertThat(not(hasPrimaryCodeTabs()));
    assertThat(not(hasPrimaryCodeViewer()));
    assertThat(not(hasSecondaryCodeTabs()));
    assertThat(hasSecondaryCodeViewer());
  }

  @Test
  public void testSimpleMultiSource() {
    open(TabbedDemoViewMultiSource.class);
    assertThat(not(hasPrimaryCodeTabs()));
    assertThat(not(hasPrimaryCodeViewer()));
    assertThat(hasSecondaryCodeTabs());
    assertThat(hasSecondaryCodeViewer());
  }

  @Test
  public void testSimpleConditionalTrue() {
    open(TabbedDemoViewConditionalTrue.class);
    assertThat(not(hasPrimaryCodeTabs()));
    assertThat(not(hasPrimaryCodeViewer()));
    assertThat(not(hasSecondaryCodeTabs()));
    assertThat(hasSecondaryCodeViewer());
  }

  @Test
  public void testSimpleConditionalFalse() {
    open(TabbedDemoViewConditionalFalse.class);
    assertThat(not(hasPrimaryCodeTabs()));
    assertThat(not(hasPrimaryCodeViewer()));
    assertThat(not(hasSecondaryCodeTabs()));
    assertThat(not(hasSecondaryCodeViewer()));
  }

}

# CommonsDemo

This library provides UI classes used in [Flowing Code Add-ons](https://addonsv24.flowingcode.com/) demos.

![image](https://github.com/FlowingCode/CommonsDemo/assets/11554739/7ef88475-85a1-4552-a16b-13a2d03e5eec)

## Tabbed Demo

The central component is the `TabbedDemo` layout, where a `SplitLayout` showcases add-on examples and, optionally, their corresponding source code.  The layout accommodates the inclusion of multiple demonstrations in tab form, which allows users to easily switch between different examples for a comprehensive exploration of the add-on features. `TabbedDemo` also has options for choosing between light and dark themes (enabling evaluation of styles in both scenarios), hiding the source code, and switching the orientation of the split layout (by default, it automatically adjusts to vertical or horizontal based on the screen ratio).

```
@Route
@GithubLink("https://github.com/FlowingCode/CommonsDemo")
public class Demo extends TabbedDemo {
  public Demo() {
    //...
    addDemo(SampleDemo.class, "Demo");
    addDemo(SampleDemoDefault.class);
    //...
  }
}
```

The `@GithubLink` and `@GithubBranch` annotations allow defaulting the demo sources to a given branch in a _public_ GitHub repository. The `@GithubLink` annotation must be configured with the HTTPS URL to the repository. Private repositories require an access token and are not supported by CommonsDemo. 

Different examples can be presented in routed demo views sharing a `TabbedDemo` layout. Since each example has its own route, users can easily reference and access them by using a URL in their web browser. This allows users to directly view the specific example they are interested in exploring.
```
@Route(value = "demo/demo-with-source", layout = Demo.class)
@PageTitle("Demo with source")
@DemoSource
public class SampleDemoDefault extends Div {
  public SampleDemoDefault() {
    add(new Span("Demo component with defaulted @DemoSource annotation"));    
  }
}
```

The demo view can be annotated with `@DemoSource`, in order to configure the URL for retrieving the source code. An optional `value` allows providing a link to the source code, in cases where it differs from the annotated class. When this annotation is used without a value, and the tabbed demo is annotated with `@GithubLink`, the source URL is automatically set to the location of the annotated class under `src/test/java` in the specified GitHub repository. The branch information is derived from the value specified in `@GithubBranch` within the tabbed demo or its containing package. If the source URL is defaulted and no `@GithubBranch` annotation is present either in the tabbed demo or its containing package, then the branch defaults to `master`. The `@DemoSource` annotation is repeatable (see [multiple sources](#multiple-sources)).

The `@DemoHelper` annotation can be used to link demo views with associated help content provided by `DemoHelperRenderer`.
The handling of how this help content is presented is managed by `DemoHelperViewer`, which is configured on the `TabbedDemo`.
By default, help content will be rendered in a `Dialog`.

![image](https://github.com/FlowingCode/CommonsDemo/assets/11554739/055a447e-a104-4ec7-a98c-fc1df8abef01)

## Code Viewer

The code viewer component (`SourceCodeViewer`) is responsible of rendering a Java source file along the demo.

The rendering process consists of the following steps:
1. Source code retrieval 
2. License abbreviation
3. Cleanup (conditional code, boilerplate removal and addition of synthetic sources)
4. Formatting (provided by [prism.js](https://prismjs.com/))
5. Post-processing (fragment highlighting)

### Source code retrieval
The specified URL (refer to the explanation of `@DemoSource` above) will be used to fetch sources. In development mode, sources will be retrieved from the local `src` directory (if available).

By default, URLs pointing to `github.com` will be rewritten into `raw.githubusercontent.com` URLs. A custom `SourceUrlResolver` can be configured to modify this behavior. 

```java
@Component
public final class SourceUrlResolverImpl implements SourceUrlResolver, VaadinServiceInitListener {

  @Override
  public void serviceInit(ServiceInitEvent event) {
    TabbedDemo.configureSourceUrlResolver(this);
  }

  @Override
  public Optional<String> resolveURL(TabbedDemo demo, Class<?> annotatedClass, DemoSource annotation) {
    return ...;
  }

}
```
### License abbreviation

A license comment block at the beginning of the file will be replaced with a short two-lines comment (author, year and link to the license terms), provided that:

- The license comment is be placed _before_ the package declaration.
- The license comment is formatted by [license-maven-plugin](https://www.mojohaus.org/license-maven-plugin) with default delimiters.
- The license is a well-known license supported by CommonsDemo.

The following well-known licenses are currently supported by CommonsDemo:
- [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

This feature cannot be disabled.

**Example**

If the source starts with:
```
/*-
 * #%L
 * Example
 * %%
 * Copyright (C) 2019 - 2023 Flowing Code
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
```

It will be rendered as:
```
//Copyright (C) 2019 - 2023 Flowing Code
//Licensed under the Apache License, Version 2.0
```

### Boilerplate removal

Commonly used boilerplate code from source snippets is automatically hidden:

- The package declaration
- All lines with a `// hide-source` comment
- The following annotations and their imports:
  - `@com.vaadin.flow.router.Route`
  - `@com.vaadin.flow.router.PageTitle`
  - `@com.flowingcode.vaadin.addons.demo.DemoSource`
  - `@java.lang.SupressWarning`
  - `@org.junit.Ignore`
- Calls to `SourceCodeViewer.highlight`, `SourceCodeViewer.highlightOnHover` and `SourceCodeViewer.highlightOnClick`

This feature cannot be disabled.

<!-- FROM https://github.com/FlowingCode/CommonsDemo/pull/37 -->
![image](https://github.com/FlowingCode/CommonsDemo/assets/11554739/083cf7ec-0f36-4db8-ab61-6c24650f4f13)

### Synthetic source

This feature allows comments to be formatted as if they were code. When used together with the `// hide-source` feature, it can improve the clarity of the example, particularly in scenarios where techniques like reflection are employed in order to circumvent binary compatibility issues.

For instance, if the demo source is: 

```
// show-source foo.bar();
Method m = Foo.class.getMethod("bar"); // hide-source
m.invoke(foo); // hide-source
```

It will be rendered as:
```
foo.bar();
```

### Conditional code

This feature allows controlling the code block rendering, depending on the version of the framework being used.

For instance, the following source would be rendered as `foo();` in Vaadin 23+, `bar()` in Vaadin 22 and `baz()` in other versions of the framework:
```
// #if vaadin ge 23
// show-source foo();
// #elif vaadin eq 22
// show-source bar();
// #else
// show-source baz();
// #endif
```

**Directives:** `#if`, `#elif` (else-if), `#else`, `#endif`. Nested conditionals are supported.

**Syntax:** `#if variable operator value`
- Supported variables are "vaadin" and "flow". (Note that Vaadin 14 uses Flow 1.x.x)
- Supported operators are `lt`, `le`, `ne`, `eq`, `gt`, `ge`.
- Value is a one-digit (`x`), two-digit (`x.y`) or three-digit (`x.y.z`) version number 

Strictly, the constructor of `SourceCodeViewer` receives a map with arbitrary variables defined by the caller, and `TabbedDemo` defines "vaadin" and "flow" variables. Implementation details in PR https://github.com/FlowingCode/CommonsDemo/pull/44.

### Fragment highlighting 

This feature supports highlighting a source code fragment in order to emphasize a section of the code snippet.
The highlighted fragment is automatically scrolled into view.

A fragment is highlighted either by calling `SourceCodeViewer.highlight(filenameAndId)` or when clicking/hovering a component that has been configured with `SourceCodeViewer.highlightOnClick` or `SourceCodeViewer.highlightOnHover`, where `filenameAndId` is the name of the fragment. If the component is in an additional source file, `filenameAndId` can be given as a string in the format `filename#id`. If no `'#'` is present, it is assumed that the identifier corresponds to a block in the first source panel. `SourceCodeViewer.highlight(null)` turns off the highlighting.

In the source code, a fragment is delimited by `// begin-block filenameAndId` and `// end-block` comments. Nested fragments are not supported.
The `// begin-block` and `// end-block` comments are removed after post-processing.

```
    // begin-block first
    Div first = new Div(new Text("Highlight block in first panel"));
    SourceCodeViewer.highlightOnHover(first, "first");
    add(first);
    // end-block
    
    Div other = new Div(new Text("Highlight additional source"));
    SourceCodeViewer.highlightOnHover(other, "AdditionalSource.java#other");
    add(other);
```

<!-- FROM https://github.com/FlowingCode/CommonsDemo/pull/62 -->
![image](https://github.com/FlowingCode/CommonsDemo/assets/11554739/02063272-029f-4b4b-bd6f-821f2f8a0158)

### Multiple sources

If several `@DemoSource` annotations are present, the layout will include a tab sheet for navigating among them. When the demo is rendered, the first `@DemoSource` will be displayed. The annotation allows customizing the caption of the source tab (which defaults to the file name) and the the language used to format the sources (which is otherwise inferred from the file extension).

![image](https://github.com/FlowingCode/CommonsDemo/assets/11554739/3c3e2094-3ddc-457c-934c-ebd1120592f5)


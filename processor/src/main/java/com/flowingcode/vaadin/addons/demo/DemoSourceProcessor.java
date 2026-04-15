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
package com.flowingcode.vaadin.addons.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Annotation processor that scans sources annotated with {@link DemoSource} and copies the
 * referenced source files into the class output (JAR).
 *
 * <p>This processor is intended to run as part of a Maven build, where
 * {@link StandardLocation#SOURCE_PATH} is configured automatically by
 * {@code maven-compiler-plugin}. When building inside an IDE, source files are served through
 * other mechanisms (e.g. {@code DevSourceRequestHandler}) that bypass the processor output, so
 * {@code SOURCE_PATH} may not be available. If {@link javax.tools.Filer#getResource} fails, the
 * processor logs a warning and skips the affected file rather than failing the build.
 *
 * @author Javier Godoy / Flowing Code
 */
@SupportedAnnotationTypes({
  "com.flowingcode.vaadin.addons.demo.DemoSource",
  "com.flowingcode.vaadin.addons.demo.DemoSources"
})
public class DemoSourceProcessor extends AbstractProcessor {

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private final Set<String> collectedPaths = new LinkedHashSet<>();

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) {
      if (!collectedPaths.isEmpty()) {
        writeResource();
      }
      return false;
    }

    TypeElement demoSourceType =
        processingEnv
            .getElementUtils()
            .getTypeElement("com.flowingcode.vaadin.addons.demo.DemoSource");
    TypeElement demoSourcesType =
        processingEnv
            .getElementUtils()
            .getTypeElement("com.flowingcode.vaadin.addons.demo.DemoSources");

    Set<Element> processed = new LinkedHashSet<>();
    processed.addAll(roundEnv.getElementsAnnotatedWith(demoSourceType));
    processed.addAll(roundEnv.getElementsAnnotatedWith(demoSourcesType));

    for (Element element : processed) {
      if (!(element instanceof TypeElement)) {
        continue;
      }
      TypeElement annotatedClass = (TypeElement) element;
      for (AnnotationMirror am :
          getDemoSourceAnnotations(element, demoSourceType, demoSourcesType)) {
        String path = resolvePath(annotatedClass, am, demoSourceType);
        if (path != null) {
          collectedPaths.add(path);
        }
      }
    }

    return false;
  }

  private List<AnnotationMirror> getDemoSourceAnnotations(
      Element element, TypeElement demoSourceType, TypeElement demoSourcesType) {
    List<AnnotationMirror> result = new ArrayList<>();
    TypeMirror demoSourceMirror = demoSourceType.asType();
    TypeMirror demoSourcesMirror = demoSourcesType.asType();

    for (AnnotationMirror am : element.getAnnotationMirrors()) {
      if (processingEnv.getTypeUtils().isSameType(am.getAnnotationType(), demoSourceMirror)) {
        result.add(am);
      } else if (processingEnv
          .getTypeUtils()
          .isSameType(am.getAnnotationType(), demoSourcesMirror)) {
        // unwrap the @DemoSources container
        AnnotationValue containerValue = getAnnotationValue(am, "value");
        if (containerValue != null) {
          @SuppressWarnings("unchecked")
          List<? extends AnnotationValue> list =
              (List<? extends AnnotationValue>) containerValue.getValue();
          for (AnnotationValue av : list) {
            result.add((AnnotationMirror) av.getValue());
          }
        }
      }
    }
    return result;
  }

  private String resolvePath(
      TypeElement annotatedClass, AnnotationMirror annotation, TypeElement demoSourceType) {
    Map<? extends ExecutableElement, ? extends AnnotationValue> values =
        processingEnv.getElementUtils().getElementValuesWithDefaults(annotation);

    String value = getStringValue(values, "value");
    TypeMirror clazzMirror = getTypeValue(values, "clazz");

    boolean hasValue = !DemoSource.DEFAULT_VALUE.equals(value);
    boolean hasClazz =
        !processingEnv.getTypeUtils().isSameType(clazzMirror, demoSourceType.asType());

    if (hasValue && hasClazz) {
      processingEnv
          .getMessager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              "Cannot specify both 'value' and 'clazz' in @DemoSource.",
              annotatedClass,
              annotation);
      return null;
    }

    if (hasValue) {
      return value;
    } else {
      TypeElement typeElement;
      if (hasClazz) {
        Element clazzElement = processingEnv.getTypeUtils().asElement(clazzMirror);
        if (!(clazzElement instanceof TypeElement)) {
          processingEnv
              .getMessager()
              .printMessage(
                  Diagnostic.Kind.ERROR,
                  "The 'clazz' element of @DemoSource must be a declared type.",
                  annotatedClass,
                  annotation);
          return null;
        }
        typeElement = (TypeElement) clazzElement;
      } else {
        // annotation.clazz() == DemoSource.class (sentinel default)
        typeElement = annotatedClass;
      }
      // Walk up to the top-level type so that inner classes resolve to Outer.java
      while (typeElement.getEnclosingElement() instanceof TypeElement) {
        typeElement = (TypeElement) typeElement.getEnclosingElement();
      }
      String className = typeElement.getQualifiedName().toString().replace('.', '/');
      return "src/test/java/" + className + ".java";
    }
  }

  private static String getStringValue(
      Map<? extends ExecutableElement, ? extends AnnotationValue> values, String name) {
    return (String) getRawValue(values, name);
  }

  private static TypeMirror getTypeValue(
      Map<? extends ExecutableElement, ? extends AnnotationValue> values, String name) {
    return (TypeMirror) getRawValue(values, name);
  }

  private static Object getRawValue(
      Map<? extends ExecutableElement, ? extends AnnotationValue> values, String name) {
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
        values.entrySet()) {
      if (entry.getKey().getSimpleName().contentEquals(name)) {
        return entry.getValue().getValue();
      }
    }
    return null;
  }

  private AnnotationValue getAnnotationValue(AnnotationMirror annotation, String name) {
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
        processingEnv.getElementUtils().getElementValuesWithDefaults(annotation).entrySet()) {
      if (entry.getKey().getSimpleName().contentEquals(name)) {
        return entry.getValue();
      }
    }
    return null;
  }

  private static final String[] SOURCE_ROOTS = {"src/test/java/", "src/main/java/"};

  private static final String[] RESOURCE_ROOTS = {"src/test/resources/", "src/main/resources/"};

  private void writeResource() {
    int count = collectedPaths.size();
    if (count > 0) {
      processingEnv
          .getMessager()
          .printMessage(
              Diagnostic.Kind.NOTE,
              "Copying " + count + " demo-source " + (count == 1 ? "file" : "files") + " to class output");
    }
    for (String rawPath : collectedPaths) {
      String sourcePath = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;
      // Files under META-INF/resources/ are already packaged into the JAR by Maven's
      // resource-processing phase; the processor does not need to copy them.
      if (isFromMetaInfResources(sourcePath)) {
        continue;
      }
      try {
        FileObject source = openSourceFile(sourcePath);
        FileObject resource =
            processingEnv
                .getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", sourcePath);
        try (OutputStream out = resource.openOutputStream();
            InputStream in = source.openInputStream()) {
          in.transferTo(out);
        }
      } catch (IOException | IllegalArgumentException e) {
        processingEnv
            .getMessager()
            .printMessage(
                Diagnostic.Kind.WARNING, "Failed to copy " + sourcePath + ": " + e.getMessage());
      }
    }
  }

  private static boolean isFromMetaInfResources(String normalizedPath) {
    for (String root : RESOURCE_ROOTS) {
      if (normalizedPath.startsWith(root)) {
        String relative = normalizedPath.substring(root.length());
        return relative.startsWith("META-INF/resources/") || relative.equals("META-INF/resources");
      }
    }
    return false;
  }

  private FileObject openSourceFile(String path) throws IOException {
    for (String root : SOURCE_ROOTS) {
      if (path.startsWith(root)) {
        return processingEnv
            .getFiler()
            .getResource(StandardLocation.SOURCE_PATH, "", path.substring(root.length()));
      }
    }
    // Resource files (src/test/resources/, src/main/resources/) are copied to CLASS_OUTPUT
    // by Maven's process-*-resources phase, which runs before test-compile (and therefore
    // before annotation processors). Read them from CLASS_OUTPUT using the path relative to
    // the resources root.
    for (String root : RESOURCE_ROOTS) {
      if (path.startsWith(root)) {
        return processingEnv
            .getFiler()
            .getResource(StandardLocation.CLASS_OUTPUT, "", path.substring(root.length()));
      }
    }
    return processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", path);
  }
}

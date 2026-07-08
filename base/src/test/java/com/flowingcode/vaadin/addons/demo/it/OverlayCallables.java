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

import com.flowingcode.vaadin.addons.demo.SourcePosition;
import com.flowingcode.vaadin.testbench.rpc.RmiCallable;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;

/**
 * testbench-rpc callable contract used by the level-E integration tests to read the server-side
 * state coordinated by {@code TabbedDemo} directly from the browser test, instead of scraping DOM
 * status elements. Implemented (with {@code @ClientCallable}) by {@link OverlayView}.
 */
public interface OverlayCallables extends RmiCallable {

  /** Current split orientation, {@code HORIZONTAL} or {@code VERTICAL}. */
  Orientation orientation();

  /** Whether the source panel is currently collapsed (latest collapse event). */
  boolean sourceCollapsed();

  /** Latest source position, {@code PRIMARY} or {@code SECONDARY} (null before any change). */
  SourcePosition sourcePosition();

  /** {@code isFromClient()} of the most recent {@code SourceCollapseChangedEvent}. */
  boolean lastCollapseFromClient();

  /** {@code isFromClient()} of the most recent {@code SourcePositionChangedEvent}. */
  boolean lastPositionFromClient();

  /** {@code isFromClient()} of the most recent {@code OrientationChangedEvent}. */
  boolean lastOrientationFromClient();

}

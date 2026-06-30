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
import {html, LitElement} from 'lit';
import {customElement} from 'lit/decorators.js';
import '@vaadin/button/vaadin-button.js';
import './commons-demo-iconset.js';

/**
 * Overlay controls pinned over the source code. Each button dispatches a bubbling DOM event that is
 * handled by an enclosing layout (see TabbedDemo), which is what actually collapses, repositions or
 * reorients the source panel. Firing the events on the client avoids a server roundtrip on click.
 */
@customElement("source-code-viewer-buttons")
export class SourceCodeViewerButtons extends LitElement {

  // Render in light DOM so the shared stylesheet (shared-styles.css) styles the buttons.
  createRenderRoot() {
    return this;
  }

  private fire(type: string, detail?: any) {
    this.dispatchEvent(new CustomEvent(type, {bubbles: true, detail}));
  }

  render() {
    return html`
      <vaadin-button theme="icon tertiary-inline" aria-label="Show source code"
          class="source-code-viewer-button source-code-viewer-show-button"
          @click=${() => this.fire('source-collapse-changed', {collapsed: false})}>
        <vaadin-icon icon="commons-demo:show-source"></vaadin-icon>
      </vaadin-button>
      <vaadin-button theme="icon tertiary-inline" aria-label="Hide source code"
          class="source-code-viewer-button source-code-viewer-hide-button"
          @click=${() => this.fire('source-collapse-changed', {collapsed: true})}>
        <vaadin-icon icon="commons-demo:hide-source"></vaadin-icon>
      </vaadin-button>
      <vaadin-button theme="icon tertiary-inline" aria-label="Flip source code"
          class="source-code-viewer-button source-code-viewer-flip-button"
          @click=${() => this.fire('source-flip')}>
        <vaadin-icon icon="commons-demo:flip"></vaadin-icon>
      </vaadin-button>
      <vaadin-button theme="icon tertiary-inline" aria-label="Rotate source code"
          class="source-code-viewer-button source-code-viewer-rotate-button"
          @click=${() => this.fire('source-rotate')}>
        <vaadin-icon icon="commons-demo:rotate"></vaadin-icon>
      </vaadin-button>
    `;
  }
}

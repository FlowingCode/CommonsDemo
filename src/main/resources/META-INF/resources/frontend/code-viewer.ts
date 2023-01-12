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
import {
  html,
  LitElement,
  unsafeCSS,
} from 'lit';

import {customElement} from 'lit/decorators.js';

//@ts-ignore
import * as Prism from "./prism.js";
@customElement("code-viewer")
export class CodeViewer extends LitElement {

  createRenderRoot() {
    return this;
  }

  render() {
    return html`
	  <style>
	  /* PrismJS 1.20.0
https://prismjs.com/download.html#themes=prism-okaidia&languages=markup+css+clike+javascript+java+typescript */
/**
 * okaidia theme for JavaScript, CSS and HTML
 * Loosely based on Monokai textmate theme by http://www.monokai.nl/
 * @author ocodia
 */

code[class*="language-"],
pre[class*="language-"] {
  color: #f8f8f2;
  background: none;
  text-shadow: 0 1px rgba(0, 0, 0, 0.3);
  font-family: Consolas, Monaco, "Andale Mono", "Ubuntu Mono", monospace;
  font-size: 1em;
  text-align: left;
  white-space: pre;
  word-spacing: normal;
  word-break: normal;
  word-wrap: normal;
  line-height: 1.5;

  -moz-tab-size: 4;
  -o-tab-size: 4;
  tab-size: 4;

  -webkit-hyphens: none;
  -moz-hyphens: none;
  -ms-hyphens: none;
  hyphens: none;
}

/* Code blocks */
pre[class*="language-"] {
  padding: 1em;
  margin: 0.5em 0;
  overflow: auto;
  border-radius: 0.3em;
}

:not(pre) > code[class*="language-"],
pre[class*="language-"] {
  background: #272822;
}

/* Inline code */
:not(pre) > code[class*="language-"] {
  padding: 0.1em;
  border-radius: 0.3em;
  white-space: normal;
}

.token.comment,
.token.prolog,
.token.doctype,
.token.cdata {
  color: #8292a2;
}

.token.punctuation {
  color: #f8f8f2;
}

.token.namespace {
  opacity: 0.7;
}

.token.property,
.token.tag,
.token.constant,
.token.symbol,
.token.deleted {
  color: #f92672;
}

.token.boolean,
.token.number {
  color: #ae81ff;
}

.token.selector,
.token.attr-name,
.token.string,
.token.char,
.token.builtin,
.token.inserted {
  color: #a6e22e;
}

.token.operator,
.token.entity,
.token.url,
.language-css .token.string,
.style .token.string,
.token.variable {
  color: #f8f8f2;
}

.token.atrule,
.token.attr-value,
.token.function,
.token.class-name {
  color: #e6db74;
}

.token.keyword {
  color: #66d9ef;
}

.token.regex,
.token.important {
  color: #fd971f;
}

.token.important,
.token.bold {
  font-weight: bold;
}
.token.italic {
  font-style: italic;
}

.token.entity {
  cursor: help;
}
</style>

      <style>
        code-viewer {
          display: block;
          background-color: #272822;
          font-size: 10pt;
        }

		pre[class*="language-"] {
          background: inherit;
        }
      </style>

      <pre><code id="code"></code></pre>
    `;
  }

  async fetchContents(sourceUrl: string, language: string) {
    var self=this;
    var xhr = new XMLHttpRequest();
    
    xhr.onreadystatechange = async function() {
    if (this.readyState == 4 && this.status == 200) {
	  // Wait for LitElement to finish updating the DOM before higlighting
      await self.updateComplete;
      var code = self.querySelector("code") as HTMLElement;
	
      code.setAttribute("class", "language-" + language);
      code.innerHTML = self.escapeHtml(this.responseText); 
      
      //@ts-ignore
      Prism.highlightAllUnder(self);
    }};
    xhr.open('GET', sourceUrl, true);
    xhr.send();
  }

  escapeHtml(unsafe: string) {
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

}

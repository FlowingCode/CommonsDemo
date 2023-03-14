/*-
 * #%L
 * Commons Demo
 * %%
 * Copyright (C) 2020 - 2023 Flowing Code
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
  customElement,
  html,
  LitElement
} from "lit-element";

import "./prism.js";

@customElement("code-viewer")
export class CodeViewer extends LitElement {

  private __license : Element[] = [];
  
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
    
.token.license a {
  color: #f8f8f2;
  opacity: 0.7;
  text-decoration: underline;
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
      var text = self.removeLicense(this.responseText);
      code.setAttribute("class", "language-" + language);
      code.innerHTML = self.escapeHtml(text);
      
      (window as any).Prism.highlightAllUnder(self);
      self.__license.reverse().forEach(e=>self.querySelector('pre code')?.prepend(e));
    }};
    xhr.open('GET', sourceUrl, true);
    xhr.send();
  }

  removeLicense(text: string) : string {
    this.__license = [];
    do {
      //parse license header
      if (!text.startsWith('/*-')) break;
      let end = text.indexOf('*/');
      if (end<0) break;
      
      let pos = text.indexOf('#%L');
      if (pos<0 || end<pos) break;
      
      let license = text.substring(pos+3,end).split('%%');
      if (license.length<3) break;
      license[1]=license[1].trim().replace(/\*/g,'').trim();
      license[2]=license[2].trim().replace(/\*/g,'').trim();
      
      if (license[1].indexOf('\n')>0) break;
      
      let newSpan = () => {
        let span = document.createElement('span');
        span.className='token comment license';
        this.__license.push(span);
        return span;
      }
      
      if (license[2].startsWith('Licensed under the Apache License, Version 2.0')) {
        newSpan().innerText='//'+license[1]+'\n';
        newSpan().innerHTML = '//Licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0" target="_blank">Apache License, Version 2.0</a>\n';
      }
      
      if (license.length) {
        text = text.substring(end+2).trimStart();
      }
    } while (0);
    return text;
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

import "@vaadin/vaadin-tabs";
import {
  customElement,
  html,
  LitElement,
  property,
  unsafeCSS,
} from "lit-element";
import { unsafeHTML } from "lit-html/directives/unsafe-html";
//@ts-ignore
import * as Prism from "./prism.js";
import prismCss from "./prism.css";

@customElement("code-viewer")
export class CodeViewer extends LitElement {

  @property({ type: String })
  contents = "";

  @property({ type: String })
  language = "none";

  createRenderRoot() {
    return this;
  }

  render() {
    return html`
      <style>
        code-viewer {
          display: block;
          background-color: #272822;
          font-size: 10pt;
        }

        ${unsafeCSS(prismCss)} 

		pre[class*="language-"] {
          background: inherit;
        }
      </style>

      ${/*Don't reuse these elements. This is needed because Prism
          removes the markers lit-html uses to track slots */
      unsafeHTML(
        `<pre><code class="language-${this.language}">${this.escapeHtml(
          this.contents
        )}
        </code></pre>`
      )}
    `;
  }

  async fetchContents(sourceUrl: string, language: string) {
    var self=this;
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = async function() {
    if (this.readyState == 4 && this.status == 200) {
      self.contents = this.responseText;
      self.language = language;
	  // Wait for LitElement to finish updating the DOM before higlighting
      await self.updateComplete;
	
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

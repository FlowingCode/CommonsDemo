import "@vaadin/vaadin-tabs";
import {
  customElement,
  html,
  LitElement,
  unsafeCSS,
} from "lit-element";

//@ts-ignore
import * as Prism from "./prism.js";
import prismCss from "./prism.css";

@customElement("code-viewer")
export class CodeViewer extends LitElement {

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

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

(function () { 
  (window as any).Vaadin.Flow.fcCodeViewerConnector = {
      
      //highlight a marked block in the single code-viewer of the UI
      highlight: (id:string|null) => {
        const viewer = document.querySelector("code-viewer") as CodeViewer;
        if (viewer) viewer.highligth(id);
      },

      //highlight a marked block in the single code-viewer of the UI, on hover of element
      highlightOnHover: (element : HTMLElement, id:string) => {
        element.addEventListener('mouseenter', ()=>{
            (window as any).Vaadin.Flow.fcCodeViewerConnector.highlight(id);
        });
        element.addEventListener('mouseleave', ()=>{
            (window as any).Vaadin.Flow.fcCodeViewerConnector.highlight(null);
        });
      }
  }
})();

@customElement("code-viewer")
export class CodeViewer extends LitElement {

  private __license : Element[] = [];
  
  env: any = {};
  
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
          position: relative;
        }

		pre[class*="language-"] {
          background: inherit;
        }
        
        .highlight {
            position: absolute;
            background: rgba(255,255,128,25%);
            right: 0;
            left: 0;
        }
      </style>

      <div class='highlight'></div>
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
      code.innerHTML = self.escapeHtml(self.cleanupCode(text));
      
      (window as any).Prism.highlightAllUnder(self);
      self.__license.reverse().forEach(e=>self.querySelector('pre code')?.prepend(e));
      self.process(code);
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
  
  cleanupCode(text: string) : string {
    let lines  : (string|null)[] = text.split('\n');
    let guards : (string|undefined)[] = [];
    let stack  : string[] = [];
    
    let __elif = (guard:(string|undefined),value:(string|undefined)) => {
        return guard=='isfalse' ? value : 'wastrue';
    };
    
    let transition = (top:string, next:string) => {
        let result = stack.pop()==top;
        stack.push(result?next:'error'); 
        return result;
    };

    for (let i=0;i<lines.length;i++) {
        let m = lines[i]!.match("^\\s*//\\s*#(?<directive>\\w+)\\s*(?<line>.*)");
        if (m && m.groups) {
            let line = m.groups.line;
            switch (m.groups.directive) {
                case 'if':
                    stack.push('if');
                    guards.push(this.__eval(line));
                    lines[i]=null;
                    break;
                case 'else':
                    if (!transition('if', 'else')) break;
                    guards.push(__elif(guards.pop(), 'istrue'));
                    lines[i]=null;
                    break;
                case 'elif':
                    if (!transition('if', 'if')) break;
                    guards.push(__elif(guards.pop(), this.__eval(line)));
                    lines[i]=null;
                    break;
                case 'endif':
                    stack.pop();
                    guards.pop();
                    lines[i]=null;
            }
        }

        if (!guards.every(x=>x=='istrue')) {
            lines[i] = null;
        }
    }
    
    return lines.filter(line=>line!==null)
    .map(line=>line!)
    .filter(line=>
       !line.match("//\\s*hide-source(\\s|$)")
    && !line.startsWith('@Route')
    && !line.startsWith('@PageTitle')
    && !line.startsWith('@DemoSource')
    && !line.startsWith('@SuppressWarnings')
    && !line.startsWith('@Ignore')
    && !line.startsWith('package ')
    && !line.trim().startsWith('SourceCodeViewer.highlightOnHover(')
    && !line.trim().startsWith('SourceCodeViewer.highlightOnClick(')
    && !line.trim().startsWith('SourceCodeViewer.highlight(')
    && line != 'import com.vaadin.flow.router.PageTitle;'
    && line != 'import com.vaadin.flow.router.Route;'
    && line != 'import com.flowingcode.vaadin.addons.demo.DemoSource;'
    && line != 'import org.junit.Ignore;'
    ).map(line=>{
        let m= line!.match("^(?<spaces>\\s*)//\\s*show-source\\s(?<line>.*)");
        return m?m.groups!.spaces+m.groups!.line : line;
    })
    .join('\n');
  }
  
  escapeHtml(unsafe: string) {
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  __eval(line: string) : string|undefined {
    let expr = line.split(' ');
    if (expr.length==3) {
        const value = this.env[expr[0]];
        if (value==undefined) {
            return 'isfalse';
        }
        
        let op = (a:string,b:string) => { 
            switch (expr[1]) {
                case 'lt': return this.__compare(a,b)<0; 
                case 'le': return this.__compare(a,b)<=0; 
                case 'eq': return this.__compare(a,b)==0;
                case 'ge': return this.__compare(a,b)>=0;
                case 'gt': return this.__compare(a,b)>0;
                case 'ne': return this.__compare(a,b)!=0;;
                default: return undefined;
        }};
        
        switch (op(value, expr[2])) {
            case true: return 'istrue';
            case false: return 'isfalse';
        }
    }
    return undefined;
  }
  
  __compare(a: string, b:string) : number {
     let aa = a.split('.');
     let bb = b.split('.');
     for (let i=0; i<Math.min(aa.length,bb.length); i++) {
         let ai = parseInt(aa[i]);
         let bi = parseInt(bb[i]);
         if (ai<bi) return -1;
         if (ai>bi) return +1;
     }
     return 0;
   }


  //process begin-block and end-block instructions 
  process(code : HTMLElement) {
    var nodes = code.childNodes;
    
    var trimEnd = (i:number) => {
        //remove trailing \n and spaces from text node i
        const node = nodes[i]
        if (node && node.nodeType==3) {
            node.textContent=(node.textContent as any).replaceAll(/\n[\t\x20]+$/g,'');
        }
    }
      
    var last : string|undefined;
    for (var i=0; i<nodes.length; i++) {
        //process instructions in element nodes
        if (nodes[i].nodeType!=1) continue;
          
        const text = nodes[i].textContent!;
        var m = text.match("^//\\s*begin-block\\s+(\\S+)\\s*");
        
        if (m) {
            last = m[1];
            (nodes[i] as HTMLElement).classList.add('begin-'+m[1]);
            nodes[i].textContent='';
            trimEnd(i-1);
            continue;
        }
        
        if (text.match("^//\\s*end-block\\s*") && last) {
            (nodes[i] as HTMLElement).classList.add('end-'+last);
            nodes[i].textContent='';
            trimEnd(i-1);
            continue;
        }
    }
   
  }
  
  //highligth a marked block
  highligth(id:string|null) {
    const div = this.querySelector('.highlight') as HTMLElement;
    
    div.style.removeProperty('top');
    div.style.removeProperty('height');
    if (id!==null) {
        var begin = this.querySelector('.begin-'+id) as HTMLElement;
        var end = this.querySelector('.end-'+id) as HTMLElement;
        if (begin && end && begin.offsetTop<=end.offsetTop) {
            var top = begin.offsetTop;
            var height = end.offsetTop+end.offsetHeight-top;
            div.style.top= `calc( ${top}px + 0.75em)`;
            div.style.height= `${height}px`;
            
            const scrollIntoView = elem => {
                if ((elem as any).scrollIntoViewIfNeeded) {
                    (elem as any).scrollIntoViewIfNeeded();
                }  else {
                    (elem as any).scrollIntoView()
                }
            }
            
            scrollIntoView(end);
            scrollIntoView(begin);
        }
    }
  }
}


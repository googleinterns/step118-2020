// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const script = require('../../main/webapp/past-deeds')

const TITLE = "title";
const DESCRIPTION = "description";
const LINK_NULL = "null";
const LINK_NOT_NULL = "https://www.google.com/";

const DIV = "div";
const H5 = "h5";
const P = "p";
const BUTTON = "button";
const CLICK = 'click';

const CLASS_CARD = "card bg-light mb-3 deedCard";
const CLASS_HEADER = 'card-header';
const CLASS_BODY = 'card-body';
const CLASS_TITLE = 'card-title goodDeedTitle';
const CLASS_DES = 'card-text goodDeeddes';
const CLASS_FOOTER = 'card-footer';
const CLASS_LINK = 'card-link';
const CLASS_BUTTON = 'btn btn-outline-dark';

const HEADER_TEXT = 'Previous Deed';
const LINK_BUTTON_TEXT = 'Associated Link';

const DEED_LIST = "deed-list";
const FETCH_URL = "/ghost-of-deeds-past";
const NO_DEEDS_TEXT = "There are no pasts deeds to display";

const DUMMY_HTML = "<ul id ='deed-list'></ul>";

beforeEach(() => {
    fetch.resetMocks();
})


test("Test getLink(deed)", () => {
    const deed = {link: LINK_NOT_NULL};
    
    delete window.location;
    window.location = Object.create(window);
    window.location.href = LINK_NULL;

    script.getLink(deed);

    expect(window.location.href).toEqual(LINK_NOT_NULL);
})

test("Test createDeedElement(deed) link null", () => {
    const deed = {title: TITLE, description: DESCRIPTION, link: LINK_NULL};
    
    // Generate Deed Element maunally
    const deedElement = document.createElement(DIV);
    deedElement.className = CLASS_CARD;

    const cardHeader = document.createElement(DIV);
    cardHeader.className = CLASS_HEADER;
    cardHeader.innerText = HEADER_TEXT;
    
    const cardBody = document.createElement(DIV);
    cardBody.className = CLASS_BODY;
    
    const titleElement = document.createElement(H5);
    titleElement.className = CLASS_TITLE;
    titleElement.innerText = deed.title;

    const descriptionElement = document.createElement(P);
    descriptionElement.className = CLASS_DES;
    descriptionElement.innerText = deed.description;

    const cardFooter = document.createElement(DIV);
    cardFooter.className = CLASS_FOOTER;

    cardBody.appendChild(titleElement);
    cardBody.appendChild(descriptionElement);

    deedElement.appendChild(cardHeader);
    deedElement.appendChild(cardBody)
    deedElement.appendChild(cardFooter);
    
    // Call function
    const actualElement = script.createDeedElement(deed);

    expect(deedElement.isEqualNode(actualElement)).toBe(true);

})

test("Test createDeedElement(deed) link not null", () => {
    const deed = {title: TITLE, description: DESCRIPTION, link: LINK_NOT_NULL};
    
    
    // Generate Deed Element maunally
    const deedElement = document.createElement(DIV);
    deedElement.className = CLASS_CARD;

    const cardHeader = document.createElement(DIV);
    cardHeader.className = CLASS_HEADER;
    cardHeader.innerText = HEADER_TEXT;
    
    const cardBody = document.createElement(DIV);
    cardBody.className = CLASS_BODY;
    
    const titleElement = document.createElement(H5);
    titleElement.className = CLASS_TITLE;
    titleElement.innerText = deed.title;

    const descriptionElement = document.createElement(P);
    descriptionElement.className = CLASS_DES;
    descriptionElement.innerText = deed.description;

    const cardFooter = document.createElement(DIV);
    cardFooter.className = CLASS_FOOTER;

    const linkButtonElement = document.createElement(BUTTON);
        
    linkButtonElement.classList.add(CLASS_LINK);
    linkButtonElement.className = CLASS_BUTTON;
    linkButtonElement.innerText = LINK_BUTTON_TEXT;

    linkButtonElement.addEventListener(CLICK, () => {
        script.getLink(deed);
    });

    cardFooter.appendChild(linkButtonElement);

    cardBody.appendChild(titleElement);
    cardBody.appendChild(descriptionElement);

    deedElement.appendChild(cardHeader);
    deedElement.appendChild(cardBody)
    deedElement.appendChild(cardFooter);
    
    // Call function
    const actualElement = script.createDeedElement(deed);

    expect(deedElement.isEqualNode(actualElement)).toBe(true);

})

test("test displayPastDeeds() w/ 1 deed", async () => {
    const deed = [{
        title: TITLE,
        description: DESCRIPTION,
        link: LINK_NOT_NULL
    }];
    
    fetch.mockResponseOnce(JSON.stringify(deed));

    document.body.innerHTML = DUMMY_HTML;

    const deedElement = script.createDeedElement(deed[0]);
    const deed_list = document.getElementById(DEED_LIST);

    return script.displayPastDeeds().then(response => {
        const child = document.getElementById(DEED_LIST).getElementsByClassName(CLASS_CARD)[0];
        
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith(FETCH_URL);
        expect(child.isEqualNode(deedElement)).toBe(true);
    });
})

test("test displayPastDeeds() w/ no deed", async () => {
    const deed = [];

    fetch.mockResponseOnce(JSON.stringify(deed));

    document.body.innerHTML = DUMMY_HTML;

    return script.displayPastDeeds().then(response => {        
        const deed_list = document.getElementById(DEED_LIST);

        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith(FETCH_URL);
        expect(deed_list.innerText).toBe(NO_DEEDS_TEXT);
    });
})

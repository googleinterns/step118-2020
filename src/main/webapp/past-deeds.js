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

function onLoad() {
    authInitializeFirebase();
    authCheckLogin(false);
    displayPastDeeds();
}

async function displayPastDeeds() {
    const DEED_LIST = "deed-list";
    const DEED_URL = "/ghost-of-deeds-past";
    const NO_DEEDS_MESSAGE = "There are no pasts deeds to display";

    const deedList = document.getElementById(DEED_LIST);

    const response = await fetch(DEED_URL);
    const deeds = await response.json();

    if (deeds.length == 0) {
            deedList.innerText = NO_DEEDS_MESSAGE;
        }
    else {
        deeds.forEach(deed => {
            deedList.appendChild(createDeedElement(deed));
        })
    }

}

function createDeedElement(deed) {
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

    const NULL = "null";


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

    if (deed.link != NULL) {
        const linkButtonElement = document.createElement(BUTTON);
        
        linkButtonElement.classList.add(CLASS_LINK);
        linkButtonElement.className = CLASS_BUTTON;
        linkButtonElement.innerText = LINK_BUTTON_TEXT;

        linkButtonElement.addEventListener(CLICK, () => {
            getLink(deed);
        });

        cardFooter.appendChild(linkButtonElement);
    }

    cardBody.appendChild(titleElement);
    cardBody.appendChild(descriptionElement);

    deedElement.appendChild(cardHeader);
    deedElement.appendChild(cardBody)
    deedElement.appendChild(cardFooter);

    return deedElement;
}

function getLink(deed) {
    window.location.href = deed.link;
}

module.exports = {
    displayPastDeeds: displayPastDeeds,
    createDeedElement: createDeedElement,
    getLink: getLink
}
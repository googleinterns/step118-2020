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

function displayPastDeeds() {
    const deedList = document.getElementById("deed-list");
    
    fetch('/ghost-of-deed-past').then(response => response.json()).then(deeds => {
        if (deeds.length == 0) {
            deedList.innerText = "There are no pasts deeds to display";
        }
        else {
            deeds.forEach(deed => {
                deedList.appendChild(createDeedElement(deed));
            })
        }
    });
}

function createDeedElement(deed) {
    const deedElement = document.createElement('div');
    deedElement.className = 'card bg-light mb-3 deedCard';

    const cardHeader = document.createElement('div');
    cardHeader.className = 'card-header';
    cardHeader.innerText = 'Previous Deed';
    
    const cardBody = document.createElement('div');
    cardBody.className = 'card-body';
    
    const titleElement = document.createElement('h5');
    titleElement.className = 'card-title goodDeedTitle';
    titleElement.innerText = deed.title;

    const descriptionElement = document.createElement('p');
    descriptionElement.className = 'card-text goodDeeddes';
    descriptionElement.innerText = deed.description;

    const cardFooter = document.createElement('div');
    cardFooter.className = 'card-footer';

    if (deed.link != "null") {
        const linkButtonElement = document.createElement('button');
        
        linkButtonElement.classList.add('card-link');
        linkButtonElement.className = 'btn btn-outline-dark';
        linkButtonElement.innerText = 'Associated Link';

        linkButtonElement.addEventListener('click', () => {
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
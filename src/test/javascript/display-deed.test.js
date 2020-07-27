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

const scriptMain = require('../../main/webapp/script')

const TITLE = "title";
const DESCRIPTION = "description";
const LINK_NULL = "null";
const LINK_NOT_NULL = "https://www.google.com/";

const DUMMY_HTML =  '<div class="card-body">' +
                    '<h5 class="card-title goodDeedTitle" id="good-deed-title"></h5>' +
                    '<p class="card-text goodDeedDes" id="good-deed"></p>' +
                    '</div>' +
                    '<div class="card-footer">' +
                    '<a class="btn btn-outline-dark" role="button" href="#" class="card-link" id="associated-link" onclick="getLink()">Associated link</a>' +
                    '</div>'


beforeEach(() =>{
    fetch.resetMocks();
});

test('Test displayDeed() with null link', async () => {
    // set fetch response
    fetch.mockResponseOnce(JSON.stringify({
        title: TITLE,
        description: DESCRIPTION,
        link: LINK_NULL
    }));

    // set up dummy HTML
    document.body.innerHTML = DUMMY_HTML;

    document.getElementById('good-deed-title').innerText = TITLE;
    document.getElementById('good-deed').innerText = DESCRIPTION;
    document.getElementById('associated-link').style.display = "none";

    return scriptMain.displayDailyDeed().then(response => {
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith('/goodDeeds');

        expect(document.getElementById('good-deed-title').innerText).toEqual(TITLE);
        expect(document.getElementById('good-deed').innerText).toEqual(DESCRIPTION);
        expect(document.getElementById('associated-link').style.display).toEqual("none");
    });
});

test('Test getLink()', async () => {
    // Set fetch
    fetch.mockResponseOnce(JSON.stringify({
        link: LINK_NOT_NULL
    }));

    delete window.location;
    window.location = Object.create(window);
    window.location.href = 'null';

    return scriptMain.getLink().then(response => {
        expect(window.location.href).toEqual(LINK_NOT_NULL);
    })
})
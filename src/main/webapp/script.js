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

async function fetchDeedsFromDatabase() {
    const DEED_TITLE_ID = "good-deed-title";
    const DEED_DESCRIPTION_ID = "good-deed";
    var DEED_ARRAY_INDEX_VALUE = 0;


    const response = await fetch('/goodDeeds');
    const json_deeds = await response.json();
    console.log(json_deeds);

    const deedTitle = document.getElementById(DEED_TITLE_ID);
    const deedDescription = document.getElementById(DEED_DESCRIPTION_ID);

    var daily_deed = json_deeds[DEED_ARRAY_INDEX_VALUE];
    console.log(daily_deed);

    deedTitle.innerText = daily_deed.title;
    deedDescription.innerText = daily_deed.description;
}
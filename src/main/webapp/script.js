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

const MONTHS = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November",
    "December"
];

// onload function when body loads
function onLoad() {
    checkLogin();
    displayDailyDeed();
    displayDate();
    getComments();
}

// check if user is logged in and redirect correspondingly
function checkLogin() {
    fetch('/login').then(response => response.json()).then((login) => {
        if (login.loggedIn) {
            // don't show logout button
            /*
            document.getElementById('loginBtn').style.display = 'none';
            document.getElementById('logoutBtn').style.display = 'block';
            document.getElementById('logoutBtn').href = login.redirectLink;
            */
        }
        else {
            document.getElementById('loginBtn').href = login.redirectLink;
            document.getElementById('loginBtn').style.display = 'block';
            document.getElementById('logoutBtn').style.display = 'none';
        }
    });
}

//Fetch Deed Object from servlet and display on index
async function displayDailyDeed() {
    const DEED_TITLE_ID = "good-deed-title";
    const DEED_DESCRIPTION_ID = "good-deed";

    const response = await fetch('/goodDeeds');
    const daily_deed = await response.json();
    /* Deed Object:
    {
        key: (Datastore Key object)
        id: (Number) Uniquely generated by database
        title: (String) Input by user
        description: (String) Input by user
        posted_yet: (Boolean) Should always be false
        timestamp: (Number) System time, miliseconds, when the Deed was added to the database
    }
    */
    console.log(daily_deed);

    const deedTitle = document.getElementById(DEED_TITLE_ID);
    const deedDescription = document.getElementById(DEED_DESCRIPTION_ID);

    deedTitle.innerText = daily_deed.title;
    deedDescription.innerText = daily_deed.description;
}

/**
 *displays the current date using the built in Date() class
 */
function displayDate() {
    const current_date = document.getElementById("curDate");

    var cur_date =  new Date();
    var cur_year = cur_date.getFullYear();
    var cur_month = cur_date.getMonth();
    var cur_day = cur_date.getDate();

    current_date.innerText = MONTHS[cur_month]+" "+cur_day+", "+cur_year+": ";
}

/**
 *gets the comments, stored as a Json, and displays them to the webapp
 */
function getComments() {
    fetch('/comments').then(response => response.json()).then((userComments) => {
 
        const commentsListElement = document.getElementById('comment-container');
        commentsListElement.innerHTML = '';
 
        for (x in userComments) {
            commentsListElement.appendChild(createListElement(userComments[x].comment));
        }
        
    });
}
 
/** 
 *Creates an <li> element containing text.
 */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

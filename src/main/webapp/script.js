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
    authInitializeFirebase();
    authCheckLogin();
    displayDailyDeed();
    displayDate();
    getComments();
}

//Fetch Deed Object from servlet and display on index
async function displayDailyDeed() {
    const DEED_TITLE_ID = "good-deed-title";
    const DEED_DESCRIPTION_ID = "good-deed";
    const ASSOCIATED_LINK_ID = "associated-link";

    const response = await fetch('/goodDeeds');
    const daily_deed = await response.json();
    /* Deed Object:
    {
        key: (Datastore Key object)
        id: (Number) Uniquely generated by database
        title: (String) Input by user
        description: (String) Input by user
        posted_yet: (Boolean) Should always be true
        timestamp: (Number) System time, miliseconds, when the Deed was added to the database
        link: either an associated link, or "null"
    }
    */

    const deedTitle = document.getElementById(DEED_TITLE_ID);
    const deedDescription = document.getElementById(DEED_DESCRIPTION_ID);

    deedTitle.innerText = daily_deed.title;
    deedDescription.innerText = daily_deed.description;


    if (daily_deed.link == "null") {
        document.getElementById(ASSOCIATED_LINK_ID).style.display = "none";
    }
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
 
        for(i = 0; i < userComments.length; i = i + 2) {
            commentsListElement.appendChild(createComment(userComments[i], userComments[i+1]));
        }
        
    });
}

/**
 * creates a styled comment box to display on the web page
 */
function createComment(text, time) {
    var commentBox = document.getElementById("commentTemplate").cloneNode(true);
    comment = sanitizeString(text);

    var userData = getProfilePicture();

    commentBox.getElementsByTagName("p")[0].innerHTML = userData['1']+" - "+time;
    commentBox.getElementsByTagName("img")[0].src = userData['0'];
    commentBox.getElementsByTagName("p")[1].innerHTML = comment;
    

    commentBox.style.display = "flex";

    return commentBox;
}

// Sanitize the string to avoid HTML injection
function sanitizeString(string) {
    string = string.replace(/</g, "&lt;").replace(/>/g, "&gt;");

    return string;
}

/**
 * Provides a profile picture for users that do not have one
 */
function getProfilePicture() {
    var userData = new Object();
    const index = Math.round(Math.random()*5);

    const pictures = ["/images/profile_pictures/blathers.jpg", 
    "/images/profile_pictures/isabelle.jpg", "/images/profile_pictures/kk.jpg",
    "/images/profile_pictures/timmy.jpg", "/images/profile_pictures/tommy.jpg",
    "/images/profile_pictures/tomnook.jpg"];

    const names = ["Blathers", "Isabelle", "KK", "Timmy", "Tommy", "Tom Nook"];

    userData['0'] = pictures[index];
    userData['1'] = names[index];

    return userData;
}

async function getLink() {
    const response = await fetch('/goodDeeds');
    const daily_deed = await response.json();

    window.location.href = daily_deed.link;
}

module.exports = {
    displayDailyDeed: displayDailyDeed,
    getLink: getLink
}

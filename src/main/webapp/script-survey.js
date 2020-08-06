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

var ideaNumber = 0; //used to give each idea a specific number

// onload function when body loads
function onLoad() {
    authInitializeFirebase();
    authCheckLogin(true);
    getIdeas();
}

/** 
  * When user clicks the down button, disables the up button and sets the input value to "down"
  * also, decrements the artificial vote counter once and increments if user clicks again
  */
function clickedDown(uniqueNum) {

    /*This if else takes care of decrementing the artificial front end counter only once and incrementing
    it if the user clicks again, since user should only click down again if they want to remove their downvote*/
    if (document.getElementById("hasClickedDown"+uniqueNum).innerHTML.localeCompare("false") == 0) {
        document.getElementById("counter"+uniqueNum).innerHTML--;
        document.getElementById("hasClickedDown"+uniqueNum).innerHTML = "true";

        /*These lines set the value of an input that we are sending to our servlet
        as 'down' so that it knows the user has downvoted */
        const ideaForm = document.getElementById('header'+uniqueNum);
        ideaForm.getElementsByTagName("input")[1].value = "down";

    } else {
        document.getElementById("counter"+uniqueNum).innerHTML++;
        document.getElementById("hasClickedDown"+uniqueNum).innerHTML = "false";

        /*These lines set the value of an input that we are sending to our servlet
        as '' since the user wants to remove his/her downvote */
        const ideaForm = document.getElementById('header'+uniqueNum);
        ideaForm.getElementsByTagName("input")[1].value = "";
    }

    /*These two lines disable the up button so the user cannot click it, 
    it also enables it should the user click again*/
    document.getElementById("up["+uniqueNum+"]").disabled = 
    !document.getElementById("up["+uniqueNum+"]").disabled;
}

/** 
  * When user clicks the up button, disables the down button and sets the input value to "up"
  * also, increments the artificial vote counter once and decrements if user clicks again
  */
function clickedUp(uniqueNum) {

    /*This if else takes care of incrementing the artificial front end counter only once and decrementing
    it if the user clicks again, since user should only click up again if they want to remove their upvote*/
    if (document.getElementById("hasClickedUp"+uniqueNum).innerHTML.localeCompare("false") == 0) {
        document.getElementById("counter"+uniqueNum).innerHTML++;
        document.getElementById("hasClickedUp"+uniqueNum).innerHTML = "true";

        /*These lines set the value of an input that we are sending to our servlet
        as 'up' so that it knows the user has upvoted */
        const ideaForm = document.getElementById('header'+uniqueNum);
        ideaForm.getElementsByTagName("input")[1].value = "up";
        
    } else {
        document.getElementById("counter"+uniqueNum).innerHTML--;
        document.getElementById("hasClickedUp"+uniqueNum).innerHTML = "false";

        /*These lines set the value of an input that we are sending to our servlet
        as '' since the user wants to remove his/her upvote */
        const ideaForm = document.getElementById('header'+uniqueNum);
        ideaForm.getElementsByTagName("input")[1].value = "";
    }

    /*These two lines disable the down button so the user cannot click it
    it also enables it should the user click again*/
    document.getElementById("down["+uniqueNum+"]").disabled = 
    !document.getElementById("down["+uniqueNum+"]").disabled;
}
 
/** Creates a styled div that contains the current user idea*/
function createIdea(title, description, votes, entity_id) {
    var ideaBox = document.getElementById("userIdeasTemplate").cloneNode(true);
    ideaTitle = sanitizeString(title);
    ideaDescription = sanitizeString(description);
 
    ideaBox.getElementsByTagName("div")[0].id = "header"+ideaNumber; //Used to access the specific idea by id
 
    /*Set the functionality on the buttons, gives them a unique number so we reference the specic idea */
    ideaBox.getElementsByTagName("button")[0].id = "up["+ideaNumber+"]"; //unique id for up button
    ideaBox.getElementsByTagName("button")[0].setAttribute("onclick", "clickedUp("+ideaNumber+")");

    ideaBox.getElementsByTagName("button")[1].id = "down["+ideaNumber+"]"; //unique id for down button
    ideaBox.getElementsByTagName("button")[1].setAttribute("onclick", "clickedDown("+ideaNumber+")");

    ideaBox.getElementsByTagName("p")[0].id = "hasClickedUp"+ideaNumber; //used so user can only increment
    ideaBox.getElementsByTagName("p")[0].innerHTML = "false"             //vote counter once

    ideaBox.getElementsByTagName("p")[1].id = "hasClickedDown"+ideaNumber; //used so user can only decrement
    ideaBox.getElementsByTagName("p")[1].innerHTML = "false"               //vote counter once

    ideaBox.getElementsByTagName("p")[2].id = "counter"+ideaNumber; //unique id for vote counter
    ideaNumber++;
 
    //id to access the idea from the datastore, hidden from the user 
    ideaBox.getElementsByTagName("input")[0].value = entity_id; 

    ideaBox.getElementsByTagName("p")[2].innerHTML = votes;
    ideaBox.getElementsByTagName("h5")[0].innerHTML = ideaTitle;
    ideaBox.getElementsByTagName("p")[3].innerHTML = ideaDescription;
    
    ideaBox.style.display = "block";
 
    return ideaBox;
}

/**
 *gets the ideas, stored as a Json, and displays them to the webapp
 */
function getIdeas() {
    fetch('/survey-input').then(response => response.json()).then((userIdeas) => {
 
        const ideasListElement = document.getElementById('ideas-container');
        ideasListElement.innerHTML = '';
 
        for (x in userIdeas) {
            ideasListElement.appendChild(createIdea(userIdeas[x].title, userIdeas[x].description, 
            userIdeas[x].votes, userIdeas[x].id));
        }
        
    });
}

// Sanitize the string to avoid HTML injection
function sanitizeString(string) {
    string = string.replace(/</g, "&lt;").replace(/>/g, "&gt;");

    return string;
}

/**Function to submit the votes, the original idea was to call this when the user exited
  * the page but so far, it has not worked and is simply mapped to a button
  */
function submitVotes() {
    document.getElementById('votesForm').submit();
}



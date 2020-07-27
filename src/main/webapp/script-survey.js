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


// onload function when body loads
function onLoad() {
    checkLogin();
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

// Sanitize the string to avoid HTML injection
function sanitizeString(string) {
    string = string.replace(/</g, "&lt;").replace(/>/g, "&gt;");

    return string;
}

/**The following two functions disable the other button when one is clicked and 
  *enable it if it is clicked again
  * i.e disables the downvote button when the upvote button is clicked or vice versa
  * - takes in a number to access the specific pair of buttons 
  */
function disableUp(uniqueNum) {
    document.getElementById("up["+uniqueNum+"]").disabled = !document.getElementById("up["+uniqueNum+"]").disabled;
}

function disableDown(uniqueNum) {
    document.getElementById("down["+uniqueNum+"]").disabled = !document.getElementById("down["+uniqueNum+"]").disabled;
}

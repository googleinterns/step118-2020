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
            document.getElementById('loginBtn').style.display = 'none';
            document.getElementById('logoutBtn').style.display = 'block';
            document.getElementById('logoutBtn').href = login.redirectLink;
        }
        else {
            document.getElementById('loginBtn').href = login.redirectLink;
            document.getElementById('loginBtn').style.display = 'block';
            document.getElementById('logoutBtn').style.display = 'none';
        }
    });
}
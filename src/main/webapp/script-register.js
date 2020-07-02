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

const INTERNAL_ERROR = 'Internal error. Please try again.';

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

// send information to /register-servlet
function submitRegistration() {
    var name = document.getElementById('name').value;
    var email = document.getElementById('email').value;

    var fetchURL = '/register-servlet?name=' + name + '&email=' + email;

    fetch(fetchURL).then(response => response.json()).then((response) => {
        showRegisterStatus(response);
    });
}

// duplicated submitRegistration() for testing because needs to return the fetch to be tested or else await won't work
function submitRegistrationForTesting() {
    var name = document.getElementById('name').value;
    var email = document.getElementById('email').value;

    var fetchURL = '/register-servlet?name=' + name + '&email=' + email;

    return fetch(fetchURL).then(response => response.json()).then((response) => {
        showRegisterStatus(response);
        return true;
    });
}

function showRegisterStatus(response) {
    // check to make sure error property exists and is a boolean
    if(typeof response.error == 'boolean') {
        if(response.error) {
            document.getElementById('register-confirm').style.display = 'none';
            document.getElementById('register-error-alert').innerHTML = response.message;
            document.getElementById('register-error-alert').style.display = 'block';
        }
        else {
            document.getElementById('register-error-alert').style.display = 'none';
            document.getElementById('register-confirm-link').href = response.link;
            document.getElementById('register-confirm').style.display = 'block';
        }
    }
    else {
        document.getElementById('register-confirm').style.display = 'none';
        document.getElementById('register-error-alert').innerHTML = INTERNAL_ERROR;
        document.getElementById('register-error-alert').style.display = 'block';
    }
}

module.exports = {
    submitRegistrationForTesting: submitRegistrationForTesting
}
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

const passMatchError = {
    code: 'password/match',
    message: 'The passwords you entered did not match.'
}

function onLoad() {
    authInitializeFirebase();
    authCheckLogin();
}

// send information to /register-servlet
function submitRegistration() {
    hideAlerts();

    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;

    if (password != confirmPassword) {
        displayError(passMatchError);
    }
    else {
        createUser(email, password);
    }
}

// create the user
async function createUser(email, password) {
    try {
        await firebase.auth().createUserWithEmailAndPassword(email, password);
        var user = firebase.auth().currentUser;
        sendEmailVerify(user);
    }
    catch(error) {
        displayError(error);
    }
}

// send verification email
async function sendEmailVerify(user) {
    try {
        await user.sendEmailVerification();
    }
    catch(error) {
        displayError(error);
    }
    finally {
        signOutAfterRegistration();
    }
}

async function signOutAfterRegistration() {
    try {
        await firebase.auth().signOut();
    }
    catch(error) {
        // don't display error on page because sign out is automatic functionality, user doesn't know they've signed in
        console.log(error);
    }
    finally {
        showConfirm();
    }
}

function hideAlerts() {
    document.getElementById('register-confirm').style.display = 'none';
    document.getElementById('register-error-alert').style.display = 'none';
}

function displayError(error) {
    document.getElementById('register-error-alert').innerHTML = error.code + ': ' + error.message;
    document.getElementById('register-error-alert').style.display = 'block';
}

function showConfirm() {
    document.getElementById('register-confirm').style.display = 'block';
}

/** SLIGHTLY MODIFIED FUNCTIONS FOR TESTING */
async function signOutAfterRegistrationTest(user, mockFn) {
    try {
        await mockFn.signOut();
        return user;
    }
    catch(error) {
        // don't display error on page because sign out is automatic functionality, user doesn't know they've signed in
        return error;
    }
    finally {
        mockFn.showConfirm();
    }
}

async function sendEmailVerifyTest(user, mockFn) {
    try {
        await mockFn.sendEmailVerification();
    } catch(error) {
        mockFn.displayError(error);
    } finally {
        mockFn.signOutAfterRegistration();
    }
}

async function createUserTest(email, password, user, mockFn) {
    try {
        await mockFn.createUserWithEmailAndPassword(email, password);
        mockFn.sendEmailVerify(user);
    }
    catch(error) {
        mockFn.displayError(error);
    }
}

function submitRegistrationTest(mockFn) {
    mockFn.hideAlerts();

    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;

    if (password != confirmPassword) {
        mockFn.displayError(passMatchError);
    }
    else {
        mockFn.createUser(email, password);
    }
}

module.exports = {
    displayError: displayError,
    hideAlerts: hideAlerts,
    showConfirm: showConfirm,
    signOutAfterRegistrationTest: signOutAfterRegistrationTest,
    sendEmailVerifyTest: sendEmailVerifyTest,
    createUserTest: createUserTest,
    submitRegistrationTest: submitRegistrationTest
}

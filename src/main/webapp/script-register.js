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

const PASS_MATCH_ERROR = {
    code: 'password/match',
    message: 'The passwords you entered did not match.'
}
const EMAIL_ID = 'email';
const PASSWORD_ID = 'password';
const CONFIRM_PASSWORD_ID = 'confirmPassword';
const REGISTER_CONFIRM_ID = 'register-confirm';
const REGISTER_ERROR_ID = 'register-error-alert';

/**
    defined with REGISTER_ prefix because on deployment, HIDE_DISPLAY interferes with
    script-auth.js initialization of HIDE_DISPLAY but can't remove initialization here 
    altogether because needed for testing
*/
const REGISTER_HIDE_DISPLAY = 'none';
const REGISTER_SHOW_DISPLAY = 'block';

function onLoad() {
    authInitializeFirebase();
    authCheckLogin();
}

// send information to /register-servlet
function submitRegistration() {
    hideAlerts();

    var email = document.getElementById(EMAIL_ID).value;
    var password = document.getElementById(PASSWORD_ID).value;
    var confirmPassword = document.getElementById(CONFIRM_PASSWORD_ID).value;

    if (password != confirmPassword) {
        displayError(PASS_MATCH_ERROR);
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
    document.getElementById(REGISTER_CONFIRM_ID).style.display = REGISTER_HIDE_DISPLAY;
    document.getElementById(REGISTER_ERROR_ID).style.display = REGISTER_HIDE_DISPLAY;
}

function displayError(error) {
    document.getElementById(REGISTER_ERROR_ID).innerHTML = error.code + ': ' + error.message;
    document.getElementById(REGISTER_ERROR_ID).style.display = REGISTER_SHOW_DISPLAY;
}

function showConfirm() {
    document.getElementById(REGISTER_CONFIRM_ID).style.display = REGISTER_SHOW_DISPLAY;
}

/**
    SLIGHTLY MODIFIED FUNCTIONS FOR TESTING

    Can't directly test the functions, because firebase needs to be initialized to be defined.
    However, to test it, it needs to be defined and then initialized.
    This problem could be solved via a complex number of solutions, but considering short timeframe of project,
    a more convenient testing is being sacrified for more feature development.
*/
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

    var email = document.getElementById(EMAIL_ID).value;
    var password = document.getElementById(PASSWORD_ID).value;
    var confirmPassword = document.getElementById(CONFIRM_PASSWORD_ID).value;

    if (password != confirmPassword) {
        mockFn.displayError(PASS_MATCH_ERROR);
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

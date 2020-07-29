var firebaseConfig;

try {
    // if in development environment, try goes through because uses node.js, and firebaseConfig = FIREBASE_CONFIG
    firebaseConfig = require('./config');
}
catch {
    // if not in development environment, try returns an error, but config.js is defined earlier in the .html file
    firebaseConfig = FIREBASE_CONFIG;
}

const LOGIN_URL = '/login.html';
const LOGIN_BTN_ID = 'loginBtn';
const LOGOUT_BTN_ID = 'logoutBtn';
const HIDE_DISPLAY = 'none';
const SHOW_DISPLAY = 'block';

// initialize firebase
function authInitializeFirebase() {
    firebase.initializeApp(firebaseConfig);
    firebase.analytics();
}

// check if user is logged in and show buttons accordingly
function authCheckLogin(restrictedAccess, loggedInFunction) {
    firebase.auth().onAuthStateChanged(function(user) {
        if (user) {
            document.getElementById(LOGIN_BTN_ID).style.display = HIDE_DISPLAY;
            document.getElementById(LOGOUT_BTN_ID).style.display = SHOW_DISPLAY;

            if(loggedInFunction != null) {
                loggedInFunction(user);
            }
        } else {
            document.getElementById(LOGIN_BTN_ID).style.display = SHOW_DISPLAY;
            document.getElementById(LOGOUT_BTN_ID).style.display = HIDE_DISPLAY;

            if(restrictedAccess) {
                window.location.replace(LOGIN_URL);
            }
        }
    });
}


// sign the user out
async function authLogout() {
    try {
        await firebase.auth().signOut();
        authCheckLogin();
    }
    catch(error) {
        console.log(error);
    }
}

/**
    SLIGHTLY MODIFIED FUNCTIONS FOR TESTING

    Can't directly test the functions, because firebase needs to be initialized to be defined.
    However, to test it, it needs to be defined and then initialized.
    This problem could be solved via a complex number of solutions, but considering short timeframe of project,
    a more convenient testing is being sacrified for more feature development.
*/
function authCheckLoginTest(restrictedAccess, loggedInFunction, mockFn) {
    mockFn.onAuthStateChanged(function(user) {
        if (user) {
            document.getElementById(LOGIN_BTN_ID).style.display = HIDE_DISPLAY;
            document.getElementById(LOGOUT_BTN_ID).style.display = SHOW_DISPLAY;

            if(loggedInFunction != null) {
                loggedInFunction(user);
            }
        } else {
            document.getElementById(LOGIN_BTN_ID).style.display = SHOW_DISPLAY;
            document.getElementById(LOGOUT_BTN_ID).style.display = HIDE_DISPLAY;

            if(restrictedAccess) {
                window.location.href = LOGIN_URL;
            }
        }
    });
}

// needs to return a value asynchronously to deal with testing a promise
async function authLogoutTest(user, mockFn) {
    try {
        await mockFn.signOut();
        mockFn.authCheckLogin();
        return true;
    }
    catch(error) {
        return false;
    }
}

// export modules to be used for testing
module.exports = {
    authCheckLoginTest: authCheckLoginTest,
    authLogoutTest: authLogoutTest
}
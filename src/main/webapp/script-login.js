/**
    defined with LOGIN_ prefix because on deployment, LOGIN_BTN_ID interferes with
    script-auth.js initialization of LOGIN_BTN_ID but can't remove initialization here 
    altogether because needed for testing
*/
const LOGIN_LOGIN_BTN_ID = 'loginBtn';
const LOGIN_LOGOUT_BTN_ID = 'logoutBtn';
const LOGIN_HIDE_DISPLAY = 'none';
const LOGIN_SHOW_DISPLAY = 'block';

const DISCLAIMER_ID = 'disclaimer';
const FIREBASE_CONTAINER_ID = 'firebaseui-auth-container';

const ROOT_DIRECTORY = '/';

const LOGGED_IN_MESSAGE = 'You have already logged in. Log out to sign in with a different account!';

// initialize the firebase and the UI
function onLoad() {
    authInitializeFirebase();
    checkLoginWithUI();
}

// check if user is logged in and show buttons accordingly
function checkLoginWithUI() {
    firebase.auth().onAuthStateChanged(function(user) {
        if (user) {
            showLogoutNoUI();
        }
        else {
            showLoginAndUI();
        }
    });
}

// show login button with UI, because user is not logged in
function showLogoutNoUI() {
    document.getElementById(LOGIN_LOGIN_BTN_ID).style.display = LOGIN_HIDE_DISPLAY;
    document.getElementById(LOGIN_LOGOUT_BTN_ID).style.display = LOGIN_SHOW_DISPLAY;

    document.getElementById(DISCLAIMER_ID).style.display = LOGIN_HIDE_DISPLAY;

    // if logged in, display this message
    document.getElementById(FIREBASE_CONTAINER_ID).innerHTML = LOGGED_IN_MESSAGE;
}

// show logout button without UI, because user is logged in
function showLoginAndUI() {
    document.getElementById(LOGIN_LOGIN_BTN_ID).style.display = LOGIN_SHOW_DISPLAY;
    document.getElementById(LOGIN_LOGOUT_BTN_ID).style.display = LOGIN_HIDE_DISPLAY;

    document.getElementById(DISCLAIMER_ID).style.display = LOGIN_SHOW_DISPLAY;

    // if not logged in, display login UI
    initializeFirebaseUI();
}

// initialize drop-in UI for firebase
function initializeFirebaseUI() {
    //clear container
    document.getElementById(FIREBASE_CONTAINER_ID).innerHTML = '';
    
    var ui = new firebaseui.auth.AuthUI(firebase.auth());

    ui.start('#' + FIREBASE_CONTAINER_ID, {
        signInSuccessUrl: ROOT_DIRECTORY,
        signInOptions: [
            firebase.auth.EmailAuthProvider.PROVIDER_ID,
            firebase.auth.GoogleAuthProvider.PROVIDER_ID
        ],
    });
}

/**
    SLIGHTLY MODIFIED FUNCTIONS FOR TESTING

    Can't directly test the functions, because firebase needs to be initialized to be defined.
    However, to test it, it needs to be defined and then initialized.
    This problem could be solved via a complex number of solutions, but considering short timeframe of project,
    a more convenient testing is being sacrified for more feature development.
*/
function showLoginAndUITest(mockFn) {
    document.getElementById(LOGIN_LOGIN_BTN_ID).style.display = LOGIN_SHOW_DISPLAY;
    document.getElementById(LOGIN_LOGOUT_BTN_ID).style.display = LOGIN_HIDE_DISPLAY;

    document.getElementById(DISCLAIMER_ID).style.display = LOGIN_SHOW_DISPLAY;

    // if not logged in, display login UI
    mockFn.initializeFirebaseUI();
}

function checkLoginWithUITest(mockFn) {
    mockFn.onAuthStateChanged(function(user) {
        if (user) {
            mockFn.showLogoutNoUI();
        }
        else {
            mockFn.showLoginAndUI();
        }
    });
}

module.exports = {
    showLogoutNoUITest: showLogoutNoUI,
    showLoginAndUITest: showLoginAndUITest,
    checkLoginWithUITest: checkLoginWithUITest
}
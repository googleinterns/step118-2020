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
    document.getElementById('loginBtn').style.display = 'none';
    document.getElementById('logoutBtn').style.display = 'block';

    // if logged in, display this message
    document.getElementById('firebaseui-auth-container').innerHTML = 'You have already logged in. Log out to sign in with a different account!';
}

// show logout button without UI, because user is logged in
function showLoginAndUI() {
    document.getElementById('loginBtn').style.display = 'block';
    document.getElementById('logoutBtn').style.display = 'none';

    // if not logged in, display login UI
    initializeFirebaseUI();
}

// initialize drop-in UI for firebase
function initializeFirebaseUI() {
    var ui = new firebaseui.auth.AuthUI(firebase.auth());

    ui.start('#firebaseui-auth-container', {
        signInSuccessUrl: '/',
        signInOptions: [
            firebase.auth.EmailAuthProvider.PROVIDER_ID,
            firebase.auth.GoogleAuthProvider.PROVIDER_ID
        ],
    });
}

/** SLIGHTLY MODIFIED FUNCTIONS FOR TESTING */
function showLoginAndUITest(mockFn) {
    document.getElementById('loginBtn').style.display = 'block';
    document.getElementById('logoutBtn').style.display = 'none';

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
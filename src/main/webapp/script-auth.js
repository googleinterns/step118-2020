// firebase configuration details (nonsecret)
var firebaseConfig = {
    apiKey: "AIzaSyDkYtj0ZlLE4ggunFI8c6ov_wnVuDUsY6Y",
    authDomain: "wear-systems-interns-step-2020.firebaseapp.com",
    databaseURL: "https://wear-systems-interns-step-2020.firebaseio.com",
    projectId: "wear-systems-interns-step-2020",
    storageBucket: "wear-systems-interns-step-2020.appspot.com",
    messagingSenderId: "372274219809",
    appId: "1:372274219809:web:5eba81e89b21d1355e4c60",
    measurementId: "G-EHL6SKF61R"
};

// initialize firebase
function authInitializeFirebase() {
    firebase.initializeApp(firebaseConfig);
    firebase.analytics();
}

// check if user is logged in and show buttons accordingly
function authCheckLogin() {
    firebase.auth().onAuthStateChanged(function(user) {
        if (user) {
            document.getElementById('loginBtn').style.display = 'none';
            document.getElementById('logoutBtn').style.display = 'block';
        } else {
            document.getElementById('loginBtn').style.display = 'block';
            document.getElementById('logoutBtn').style.display = 'none';
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

/** SLIGHTLY MODIFIED FUNCTIONS FOR TESTING */
function authCheckLoginTest(mockFn) {
    mockFn.onAuthStateChanged(function(user) {
        if (user) {
            document.getElementById('loginBtn').style.display = 'none';
            document.getElementById('logoutBtn').style.display = 'block';
        } else {
            document.getElementById('loginBtn').style.display = 'block';
            document.getElementById('logoutBtn').style.display = 'none';
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
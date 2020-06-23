// onload function when body loads
function onLoad() {
    checkLogin();
}

// check if user is logged in and redirect correspondingly
function checkLogin() {
    fetch('/login').then(response => response.json()).then((login) => {
        if (login.loggedIn) {
            console.log('logged in');
        }
        else {
            console.log('not logged in');
        }
    });
}
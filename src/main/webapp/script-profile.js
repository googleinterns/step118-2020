function onLoad() {
    authInitializeFirebase();
    authCheckLogin(true, getLoginInfo);
}

function getLoginInfo() {
    return fetch('/profile-servlet').then(response => response.json()).then((profile) => {
        return profile;
    })
}

module.exports = {
    getLoginInfo
}
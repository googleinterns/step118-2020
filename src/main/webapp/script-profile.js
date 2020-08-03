const FNAME_ID = 'fname';
const LNAME_ID = 'lname';
const EMAIL_ID = 'email';
const LOCATION_ID = 'location';
const BIO_ID = 'bio';

function onLoad() {
    authInitializeFirebase();
    authCheckLogin(true, getProfileInfo);
}

function getProfileInfo(user) {
    const profileServletSlug = '/profile-servlet?email=' + user.email;

    return fetch(profileServletSlug).then(response => response.json()).then((profile) => {
        profile.email = user.email;
        
        fillProfile(profile);
        return profile;
    })
}

function fillProfile(profile) {
    document.getElementById(FNAME_ID).value = profile.fname;
    document.getElementById(LNAME_ID).value = profile.lname;
    document.getElementById(EMAIL_ID).value = profile.email;
    document.getElementById(LOCATION_ID).value = profile.location;
    document.getElementById(BIO_ID).value = profile.bio;
}

function updateProfile() {
    authCheckLogin(true, updateProfileInfo);
}

// update login profile 
function updateProfileInfo(user) {
    const fname = document.getElementById(FNAME_ID).value;
    const lname = document.getElementById(LNAME_ID).value;
    const email = user.email;
    const location = document.getElementById(LOCATION_ID).value;
    const bio = document.getElementById(BIO_ID).value;

    const postParams = {
        fname,
        lname,
        email,
        location,
        bio
    }

    const postURL = '/profile-servlet' + getParamSlug(postParams);
    const postOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    }

    return fetch(postURL, postOptions).then(response => response.json()).then((response) => {
        fillProfile(response);
        return response;
    })
}

function getParamSlug(params) {
    var slug = '?';
    for (variable in params) {
        slug = slug + variable + '=' + encodeURIComponent(params[variable]) + '&';
    }
    // get rid of last &
    slug = slug.slice(0, -1);
    return slug;
}

module.exports = {
    getProfileInfo,
    fillProfile,
    updateProfileInfo,
    getParamSlug
}
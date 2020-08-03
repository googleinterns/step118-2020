const scriptProfile = require('../../main/webapp/script-profile');

const FNAME_ID = 'fname';
const LNAME_ID = 'lname';
const EMAIL_ID = 'email';
const LOCATION_ID = 'location';
const BIO_ID = 'bio';

const TEST_PROFILE = {
    fname: 'John',
    lname: 'Doe',
    email: 'test@example.com',
    location: 'Houston, TX',
    bio: 'My name is John Doe, and I\'m from Houston, Texas.'
}

const TEST_HTML = '<main class="content">' +
            '<h1>Profile</h1>' +
            '<p>Fill out your profile here!</p>' +
            '<form>' +
                '<input type="text" class="form-control" id="fname" name="fname">' +
                '<input type="text" class="form-control" id="lname" name="lname">' +
                '<input class="form-control" type="text" id="email" name ="email" placeholder="" readonly>' +
                '<input type="text" class="form-control" id="location" name="location">' +
                '<textarea class="form-control" id="bio" name="bio"></textarea>' +
                '<button type="button" class="btn btn-primary" onclick="updateLoginInfo()">Update</button>' +
            '</form>' +
        '</main>'

// clear the fetch mock before each test
beforeEach(() => {
    fetch.resetMocks();
})

test('getProfileInfo() fetch works', async () => {
    document.body.innerHTML = TEST_HTML;
    
    fetch.mockResponseOnce(JSON.stringify(TEST_PROFILE));

    await scriptProfile.getProfileInfo(TEST_PROFILE).then(response => {
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(response).toStrictEqual(TEST_PROFILE);
    });
})

test('fillProfile() works', () => {
    document.body.innerHTML = TEST_HTML;

    scriptProfile.fillProfile(TEST_PROFILE);

    expect(document.getElementById(FNAME_ID).value).toBe(TEST_PROFILE.fname);
    expect(document.getElementById(LNAME_ID).value).toBe(TEST_PROFILE.lname);
    expect(document.getElementById(EMAIL_ID).value).toBe(TEST_PROFILE.email);
    expect(document.getElementById(LOCATION_ID).value).toBe(TEST_PROFILE.location);
    expect(document.getElementById(BIO_ID).value).toBe(TEST_PROFILE.bio);
})

test('updateProfileInfo() works', async () => {
    // set the test HTML
    document.body.innerHTML = TEST_HTML;

    fetch.mockResponseOnce(JSON.stringify(TEST_PROFILE));

    document.getElementById(FNAME_ID).value = TEST_PROFILE.fname;
    document.getElementById(LNAME_ID).value = TEST_PROFILE.lname;
    document.getElementById(LOCATION_ID).value = TEST_PROFILE.location;
    document.getElementById(BIO_ID).value = TEST_PROFILE.bio;

    await scriptProfile.updateProfileInfo(TEST_PROFILE).then(response => {
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(response).toStrictEqual(TEST_PROFILE);
    });
})

test('getParamSlug() works', () => {
    const paramSlug = scriptProfile.getParamSlug(TEST_PROFILE);

    const fnameEncoded = encodeURIComponent(TEST_PROFILE.fname);
    const lnameEncoded = encodeURIComponent(TEST_PROFILE.lname);
    const emailEncoded = encodeURIComponent(TEST_PROFILE.email);
    const locationEncoded = encodeURIComponent(TEST_PROFILE.location);
    const bioEncoded = encodeURIComponent(TEST_PROFILE.bio);

    const expectedParamSlug = '?fname=' + fnameEncoded + '&lname=' + lnameEncoded + '&email=' + emailEncoded + '&location=' + locationEncoded + '&bio=' + bioEncoded;

    expect(paramSlug).toBe(expectedParamSlug);
})
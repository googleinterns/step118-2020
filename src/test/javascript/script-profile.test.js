const scriptProfile = require('../../main/webapp/script-profile');

const TEST_PROFILE = {
    fname: 'John',
    lname: 'Doe',
    email: 'test@example.com',
    location: 'Houston, TX',
    bio: 'My name is John Doe, and I\'m from Houston, Texas.'
}

// clear the fetch mock before each test
beforeEach(() => {
    fetch.resetMocks();
})

test('getLoginInfo() fetch works', async () => {
    fetch.mockResponseOnce(JSON.stringify(TEST_PROFILE));

    await scriptProfile.getLoginInfo().then(response => {
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(response).toStrictEqual(TEST_PROFILE);
    });
})
const scriptAuth = require('../../main/webapp/script-auth');

const LOGIN_BTN_ID = 'loginBtn';
const LOGOUT_BTN_ID = 'logoutBtn';
const HIDE_DISPLAY = 'none';
const SHOW_DISPLAY = 'block';

const ERROR_OBJ = new Error('test/code');
const TEST_USER = {
    displayName: 'test',
    email: 'test@example.com',
    uid: '0123456789'
};

const authCheckLoginMockFn = jest.fn();

// keep selective parts of navbar and login
const TEST_HTML = 
    '<form class="form-inline my-2 my-lg-0">' +
    '   <a class="btn btn-outline-light my-2 my-sm-0" type="submit" href="/login.html" id="loginBtn" style="display: none;" role="button">Login here</a>' +
    '   <a class="btn btn-outline-light my-2 my-sm-0" type="submit" href="#" onclick="authLogout()" id="logoutBtn" style="display: none;" role="button">Logout here</a>' +
    '</form>' + 
    '<h1>Login</h1>' + 
    '<div id="firebaseui-auth-container"></div>';

// reset mock function tracker after each test
afterEach(() => {
    jest.clearAllMocks();
});

test('authCheckLogin() displays logout button when user is logged in', () => {
    // set the HTML
    document.body.innerHTML = TEST_HTML;

    const onAuthStateChangedMockFn = jest.fn(getUserFunc => {
        getUserFunc(true);
    });

    const mockFn = {
        onAuthStateChanged: onAuthStateChangedMockFn
    }

    // run authCheckLogin with user
    scriptAuth.authCheckLoginTest(mockFn);

    expect(document.getElementById(LOGIN_BTN_ID).style.display).toEqual(HIDE_DISPLAY);
    expect(document.getElementById(LOGOUT_BTN_ID).style.display).toEqual(SHOW_DISPLAY);
});

test('authCheckLogin() displays login button when user is logged out', () => {
    // set the HTML
    document.body.innerHTML = TEST_HTML;

    const onAuthStateChangedMockFn = jest.fn(getUserFunc => {
        getUserFunc(false);
    });

    const mockFn = {
        onAuthStateChanged: onAuthStateChangedMockFn
    }

    // run authCheckLogin without a user
    scriptAuth.authCheckLoginTest(mockFn);

    expect(document.getElementById(LOGIN_BTN_ID).style.display).toEqual(SHOW_DISPLAY);
    expect(document.getElementById(LOGOUT_BTN_ID).style.display).toEqual(HIDE_DISPLAY);
});

test('authLogout() updates the login/logout button', async () => {
    // set the HTML
    document.body.innerHTML = TEST_HTML;

    // set signed in test user
    var user = TEST_USER;

    // logout mock has no error
    const logoutMockFn = jest.fn(user = null).mockResolvedValue(true);

    const mockFn = {
        signOut: logoutMockFn,
        authCheckLogin: authCheckLoginMockFn
    }

    await scriptAuth.authLogoutTest(user, mockFn).then(response => {
        expect(logoutMockFn).toHaveBeenCalledTimes(1);
        expect(authCheckLoginMockFn).toHaveBeenCalledTimes(1);
    });
});

test('authLogout() has an error and does not update login/logout button', async () => {
    // set the HTML
    document.body.innerHTML = TEST_HTML;

    // set signed in test user
    var user = TEST_USER;

    // logout mock has no error
    const logoutMockFn = jest.fn(user = null).mockImplementation(() => {
        throw ERROR_OBJ;
    });

    const mockFn = {
        signOut: logoutMockFn,
        authCheckLogin: authCheckLoginMockFn
    }

    await scriptAuth.authLogoutTest(user, mockFn).then(response => {
        expect(logoutMockFn).toHaveBeenCalledTimes(1);
        // only one call (from before, when confirming the user existed)
        expect(authCheckLoginMockFn).not.toHaveBeenCalled();
    });
});
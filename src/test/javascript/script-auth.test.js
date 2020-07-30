const scriptAuth = require('../../main/webapp/script-auth');

const LOGIN_BTN_ID = 'loginBtn';
const LOGOUT_BTN_ID = 'logoutBtn';
const HIDE_DISPLAY = 'none';
const SHOW_DISPLAY = 'block';

const DEFAULT_URL = 'http://localhost'
const LOGIN_URL = '/login.html';

const OPEN_ACCESS = false;
const RESTRICTED_ACCESS = true;

const NO_LOGGED_IN_FN = null;

const ERROR_OBJ = new Error('test/code');
const TEST_USER = {
    displayName: 'test',
    email: 'test@example.com',
    uid: '0123456789'
};

const authCheckLoginMockFn = jest.fn();
const onAuthStateChangedMockFnLoggedOut = jest.fn(getUserFunc => {
        getUserFunc(false);
    });
const onAuthStateChangedMockFnLoggedIn = jest.fn(getUserFunc => {
        getUserFunc(true);
    });

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

test('authCheckLogin() displays logout button when user is logged in and doesn\'t redirect when open access', () => {
    // set window object to test window.location.href
    global.window = Object.create(window);
    Object.defineProperty(window, 'location', {
      value: {
        href: DEFAULT_URL,
      },
      writable: true,
    });

    // set the HTML
    document.body.innerHTML = TEST_HTML;

    const mockFn = {
        onAuthStateChanged: onAuthStateChangedMockFnLoggedIn
    }

    // run authCheckLogin with user
    scriptAuth.authCheckLoginTest(OPEN_ACCESS, NO_LOGGED_IN_FN, mockFn);

    expect(window.location.href).toBe(DEFAULT_URL);
    expect(document.getElementById(LOGIN_BTN_ID).style.display).toEqual(HIDE_DISPLAY);
    expect(document.getElementById(LOGOUT_BTN_ID).style.display).toEqual(SHOW_DISPLAY);
});

test('authCheckLogin() displays login button when user is logged out and redirects when restricted access', () => {
    // set window object to test window.location.href
    global.window = Object.create(window);
    Object.defineProperty(window, 'location', {
      value: {
        href: DEFAULT_URL,
      },
      writable: true,
    });

    document.body.innerHTML = TEST_HTML;

    const mockFn = {
        onAuthStateChanged: onAuthStateChangedMockFnLoggedOut
    }

    // run authCheckLogin without a user
    scriptAuth.authCheckLoginTest(RESTRICTED_ACCESS, NO_LOGGED_IN_FN, mockFn);
    
    expect(window.location.href).toBe(LOGIN_URL);
    expect(document.getElementById(LOGIN_BTN_ID).style.display).toEqual(SHOW_DISPLAY);
    expect(document.getElementById(LOGOUT_BTN_ID).style.display).toEqual(HIDE_DISPLAY);
});

test('authCheckLogin() displays login button when user is logged out and doesn\'t redirect when open access', () => {
    // set window object to test window.location.href
    global.window = Object.create(window);
    Object.defineProperty(window, 'location', {
      value: {
        href: DEFAULT_URL,
      },
      writable: true,
    });

    // set the HTML
    document.body.innerHTML = TEST_HTML;

    const mockFn = {
        onAuthStateChanged: onAuthStateChangedMockFnLoggedOut
    }

    // run authCheckLogin without a user
    scriptAuth.authCheckLoginTest(OPEN_ACCESS, NO_LOGGED_IN_FN, mockFn);

    expect(window.location.href).toBe(DEFAULT_URL);
    expect(document.getElementById(LOGIN_BTN_ID).style.display).toEqual(SHOW_DISPLAY);
    expect(document.getElementById(LOGOUT_BTN_ID).style.display).toEqual(HIDE_DISPLAY);
});

test('authCheckLogin() calls loggedInFunction when logged in and a function is passed', () => {
    // set the HTML
    document.body.innerHTML = TEST_HTML;

    const mockFn = {
        onAuthStateChanged: onAuthStateChangedMockFnLoggedIn
    }

    const loggedInFunction = jest.fn();

    scriptAuth.authCheckLoginTest(OPEN_ACCESS, loggedInFunction, mockFn);

    expect(loggedInFunction).toHaveBeenCalledTimes(1);
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
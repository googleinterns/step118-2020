const scriptLogin = require('../../main/webapp/script-login');

const LOGGED_IN_MESSAGE = 'You have already logged in. Log out to sign in with a different account!';

// define empty mock functions to be referenced throughout tests
const showLogoutNoUIMockFn = jest.fn();
const showLoginAndUIMockFn = jest.fn();

const TEST_HTML = '<nav class="navbar navbar-expand-lg navbar-dark bg-dark">' +
            '<a class="navbar-brand" href="/">1Deed1Day</a>' +
            '<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">' +
                '<span class="navbar-toggler-icon"></span>' +
            '</button>' +

            '<div class="collapse navbar-collapse" id="navbarSupportedContent">' +
                '<ul class="navbar-nav mr-auto">' +
                    '<li class="nav-item">' +
                        '<a class="nav-link" href="/">Home</a>' +
                    '</li>' +
                    '<li class="nav-item">' +
                        '<a class="nav-link" href="/register.html">Register</span></a>' +
                    '</li>' +
                    '<li class="nav-item active">' +
                        '<a class="nav-link" href="#">Login <span class="sr-only">(current)</a>' +
                    '</li>' +
                '</ul>' +
                '<form class="form-inline my-2 my-lg-0">' +
                    '<a class="btn btn-outline-light my-2 my-sm-0" type="submit" href="/login.html" id="loginBtn" style="display: none;" role="button">Login here</a>' +
                    '<a class="btn btn-outline-light my-2 my-sm-0" type="submit" href="#" onclick="authLogout()" id="logoutBtn" style="display: none;" role="button">Logout here</a>' +
                '</form>' +
            '</div>' +
        '</nav>' +
        '<main class="content">' +
            '<h1>Login</h1>' +
            '<p id="disclaimer" style="display: none;">If you don\'t have a Gmail account, please register <a href="/login.html" class="alert-link" id="register-confirm-link">here</a>. Otherwise, you can log directly into 1Deed1Day with your Gmail credentials.</p>' +
            '<div id="firebaseui-auth-container"></div>' +
        '</main>'


// reset mock function tracker after each test
afterEach(() => {
    jest.clearAllMocks();
});


test('showLogoutNoUI()', () => {
    document.body.innerHTML = TEST_HTML;

    scriptLogin.showLogoutNoUITest();

    expect(document.getElementById('loginBtn').style.display).toBe('none');
    expect(document.getElementById('logoutBtn').style.display).toBe('block');

    expect(document.getElementById('disclaimer').style.display).toBe('none');

    expect(document.getElementById('firebaseui-auth-container').innerHTML).toBe(LOGGED_IN_MESSAGE);
})


test('showLoginAndUI()', () => {
    document.body.innerHTML = TEST_HTML;

    const initializeFirebaseUIMockFn = jest.fn();

    const mockFn = {
        initializeFirebaseUI: initializeFirebaseUIMockFn
    }

    scriptLogin.showLoginAndUITest(mockFn);

    expect(document.getElementById('loginBtn').style.display).toBe('block');
    expect(document.getElementById('logoutBtn').style.display).toBe('none');

    expect(document.getElementById('disclaimer').style.display).toBe('block');

    expect(initializeFirebaseUIMockFn).toHaveBeenCalledTimes(1);
})


test('checkLoginWithUI() where user is logged in', () => {
    const onAuthStateChangedMockFn = jest.fn(getUserFunc => {
        getUserFunc(true);
    });

    const mockFn = {
        onAuthStateChanged: onAuthStateChangedMockFn,
        showLogoutNoUI: showLogoutNoUIMockFn,
        showLoginAndUI: showLoginAndUIMockFn
    }

    scriptLogin.checkLoginWithUITest(mockFn);

    expect(onAuthStateChangedMockFn).toHaveBeenCalledTimes(1);
    expect(showLogoutNoUIMockFn).toHaveBeenCalledTimes(1);
    expect(showLoginAndUIMockFn).not.toHaveBeenCalled();
})


test('checkLoginWithUI where user is not logged in', () => {
    const onAuthStateChangedMockFn = jest.fn(getUserFunc => {
        getUserFunc(false);
    });

    const mockFn = {
        onAuthStateChanged: onAuthStateChangedMockFn,
        showLogoutNoUI: showLogoutNoUIMockFn,
        showLoginAndUI: showLoginAndUIMockFn
    }

    scriptLogin.checkLoginWithUITest(mockFn);

    expect(onAuthStateChangedMockFn).toHaveBeenCalledTimes(1);
    expect(showLogoutNoUIMockFn).not.toHaveBeenCalled();
    expect(showLoginAndUIMockFn).toHaveBeenCalledTimes(1);
})
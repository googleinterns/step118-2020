// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const scriptRegister = require('../../main/webapp/script-register');

const REGISTER_ERROR_ID = 'register-error-alert';
const REGISTER_CONFIRM_ID = 'register-confirm';
const EMAIL_ID = 'email';
const PASSWORD_ID = 'password';
const CONFIRM_PASSWORD_ID = 'confirmPassword';

const SHOW_DISPLAY = 'block';
const HIDE_DISPLAY = 'none';

const ERROR_CODE = 'test/code';
const ERROR_MESSAGE = 'There is an error';
const ERROR = {
    code: ERROR_CODE,
    message: ERROR_MESSAGE
};
const ERROR_OBJ = new Error(ERROR_CODE);
const PASS_MATCH_ERROR = {
    code: 'password/match',
    message: 'The passwords you entered did not match.'
}
const NO_LINK = '';
const EMAIL = 'test@example.com';
const PASS_1 = 'password1';
const PASS_2 = 'password2';

const TEST_USER = {
    displayName: 'test',
    email: EMAIL,
    uid: '0123456789'
};

// define empty mock functions to be referenced throughout tests
const displayErrorMockFn = jest.fn();
const showConfirmMockFn = jest.fn();
const createUserMockFn = jest.fn();
const hideAlertsMockFn = jest.fn();
const sendEmailVerifyMockFn = jest.fn();
const signOutAfterRegistrationMockFn = jest.fn();

const TEST_HTML = 
    '<h1>Registration</h1>' +
    '<p>Please enter your name and email to register! You\'ll login with your email account.</p>' +
    '<div class="alert alert-danger" role="alert" id="register-error-alert" style="display: none;">' +
    '</div>' +
    '<div class="alert alert-success" role="alert" id="register-confirm" style="display: none;">' +
    '    You\'ve registered! Verify your email in your inbox and then login <a href="/login.html" class="alert-link" id="register-confirm-link">here</a>.' +
    '</div>' +
    '<form>' +
    '   <div class="form-group">' +
    '        <label for="email">Email address</label>' +
    '        <input type="email" class="form-control" id="email" name="email">' +
    '   </div>' +
    '   <div class="form-group">' +
    '        <label for="password">Password</label>' +
    '        <input type="password" class="form-control" id="password" name="password">' +
    '    </div>' +
    '    <div class="form-group">' +
    '        <label for="confirmPassword">Confirm password</label>' +
    '        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword">' +
    '    </div>' +
    '    <button type="button" class="btn btn-primary" onclick="submitRegistration()">Register</button>' +
    '</form>'

// reset mock function tracker after each test
afterEach(() => {
    jest.clearAllMocks();
});


test('displayError() correctly shows the error', () => {
    document.body.innerHTML = TEST_HTML;

    scriptRegister.displayError(ERROR);

    expect(document.getElementById(REGISTER_ERROR_ID).style.display).toBe(SHOW_DISPLAY);
    expect(document.getElementById(REGISTER_ERROR_ID).innerHTML).toBe(ERROR_CODE + ': ' + ERROR_MESSAGE);
    expect(document.getElementById(REGISTER_CONFIRM_ID).style.display).toBe(HIDE_DISPLAY);
});


test('hideAlerts() correctly hides all alerts', () => {
    document.body.innerHTML = TEST_HTML;

    // show both alerts
    document.getElementById(REGISTER_ERROR_ID).style.display = SHOW_DISPLAY;
    document.getElementById(REGISTER_CONFIRM_ID).style.display = SHOW_DISPLAY;

    scriptRegister.hideAlerts();

    expect(document.getElementById(REGISTER_ERROR_ID).style.display).toBe(HIDE_DISPLAY);
    expect(document.getElementById(REGISTER_CONFIRM_ID).style.display).toBe(HIDE_DISPLAY);
});


test('showConfirm() correctly shows confirmation', () => {
    document.body.innerHTML = TEST_HTML;

    scriptRegister.showConfirm();

    expect(document.getElementById(REGISTER_CONFIRM_ID).style.display).toBe(SHOW_DISPLAY);
})


test('signOutAfterRegistration() signs the user out and displays the login alert', async () => {
    document.body.innerHTML = TEST_HTML;

    // set signed in test user
    var user = TEST_USER;

    const logoutMockFn = jest.fn(user = null);

    const mockFn = {
        signOut: logoutMockFn,
        showConfirm: showConfirmMockFn
    }

    await scriptRegister.signOutAfterRegistrationTest(user, mockFn).then(response => {
        expect(logoutMockFn).toHaveBeenCalledTimes(1);
        // successfully ran
        expect(response).toBeNull();
        expect(showConfirmMockFn).toHaveBeenCalledTimes(1);
    });
});


test('signOutAfterRegistration() has an error and does not display the login alert', async () => {
    document.body.innerHTML = TEST_HTML;

    // set signed in user
    var user = TEST_USER
    
    const logoutMockFn = jest.fn(user = null).mockImplementation(() => {
        throw ERROR_OBJ;
    });

    const mockFn = {
        signOut: logoutMockFn,
        showConfirm: showConfirmMockFn
    }

    await scriptRegister.signOutAfterRegistrationTest(user, mockFn).then(response => {
        expect(logoutMockFn).toHaveBeenCalledTimes(1);
        // check error occured, strict equals to check object equality
        expect(response).toStrictEqual(ERROR_OBJ);
        expect(showConfirmMockFn).toHaveBeenCalledTimes(1);
    });
});


test('sendEmailVerify() has no error and calls signOutAfterRegistration()', async () => {
    document.body.innerHTML = TEST_HTML;

    const sendEmailVerificationMockFn = jest.fn();

    const mockFn = {
        sendEmailVerification: sendEmailVerificationMockFn,
        signOutAfterRegistration: signOutAfterRegistrationMockFn,
        displayError: displayErrorMockFn
    }

    await scriptRegister.sendEmailVerifyTest(TEST_USER, mockFn).then(response => {
        expect(sendEmailVerificationMockFn).toHaveBeenCalledTimes(1);
        expect(displayErrorMockFn).not.toHaveBeenCalled();
        expect(signOutAfterRegistrationMockFn).toHaveBeenCalledTimes(1);
    })
});


test('sendEmailVerify() has an error and calls signOutAfterRegistration() as well as displays the error', async () => {
    document.body.innerHTML = TEST_HTML;

    const sendEmailVerificationMockFn = jest.fn().mockImplementation(() => {
        throw ERROR_OBJ;
    });

    const mockFn = {
        sendEmailVerification: sendEmailVerificationMockFn,
        signOutAfterRegistration: signOutAfterRegistrationMockFn,
        displayError: displayErrorMockFn
    }

    await scriptRegister.sendEmailVerifyTest(TEST_USER, mockFn).then(response => {
        expect(sendEmailVerificationMockFn).toHaveBeenCalledTimes(1);
        expect(displayErrorMockFn).toHaveBeenCalledTimes(1);
        expect(displayErrorMockFn).toHaveBeenCalledWith(ERROR_OBJ);
        expect(signOutAfterRegistrationMockFn).toHaveBeenCalledTimes(1);
    });
});


test('createUser() has no error and calls sendEmailVerify()', async () => {
    document.body.innerHTML = TEST_HTML;

    const createUserWithEmailAndPasswordMockFn = jest.fn();

    const mockFn = {
        createUserWithEmailAndPassword: createUserWithEmailAndPasswordMockFn,
        sendEmailVerify: sendEmailVerifyMockFn,
        displayError: displayErrorMockFn
    }

    await scriptRegister.createUserTest(EMAIL, PASS_1, TEST_USER, mockFn).then(response => {
        expect(createUserWithEmailAndPasswordMockFn).toHaveBeenCalledTimes(1);
        expect(sendEmailVerifyMockFn).toHaveBeenCalledTimes(1);
        expect(sendEmailVerifyMockFn).toHaveBeenCalledWith(TEST_USER);
        expect(displayErrorMockFn).not.toHaveBeenCalled();
    });
});


test('createUser() has an error and displays error as well as does not sendEmailVerify()', async () => {
    const createUserWithEmailAndPasswordMockFn = jest.fn().mockImplementation(() => {
        throw ERROR_OBJ;
    });

    const mockFn = {
        createUserWithEmailAndPassword: createUserWithEmailAndPasswordMockFn,
        sendEmailVerify: sendEmailVerifyMockFn,
        displayError: displayErrorMockFn
    }

    await scriptRegister.createUserTest(EMAIL, PASS_1, TEST_USER, mockFn).then(response => {
        expect(createUserWithEmailAndPasswordMockFn).toHaveBeenCalledTimes(1);
        expect(sendEmailVerifyMockFn).not.toHaveBeenCalled();
        expect(displayErrorMockFn).toHaveBeenCalledTimes(1);
        expect(displayErrorMockFn).toHaveBeenCalledWith(ERROR_OBJ);
    });
});


test('test submitRegistration() without error', async () => {
    document.body.innerHTML = TEST_HTML;
    
    // set input values for name and email
    document.getElementById(EMAIL_ID).value = EMAIL;
    document.getElementById(PASSWORD_ID).value = PASS_1;
    document.getElementById(CONFIRM_PASSWORD_ID).value = PASS_1;

    const mockFn = {
        createUser: createUserMockFn,
        hideAlerts: hideAlertsMockFn,
        displayError: displayErrorMockFn
    }

    scriptRegister.submitRegistrationTest(mockFn);

    expect(hideAlertsMockFn).toHaveBeenCalledTimes(1);
    expect(createUserMockFn).toHaveBeenCalledTimes(1);
    expect(displayErrorMockFn).not.toHaveBeenCalled();
});


test('test submitRegistration() with error, passwords don\'t match', async () => {
    document.body.innerHTML = TEST_HTML;
    
    // set input values for name and email
    document.getElementById(EMAIL_ID).value = EMAIL;
    document.getElementById(PASSWORD_ID).value = PASS_1;
    document.getElementById(CONFIRM_PASSWORD_ID).value = PASS_2;

    const mockFn = {
        createUser: createUserMockFn,
        hideAlerts: hideAlertsMockFn,
        displayError: displayErrorMockFn
    }

    scriptRegister.submitRegistrationTest(mockFn);

    expect(hideAlertsMockFn).toHaveBeenCalledTimes(1);
    expect(createUserMockFn).not.toHaveBeenCalled();
    expect(displayErrorMockFn).toHaveBeenCalledTimes(1);
    expect(displayErrorMockFn).toHaveBeenCalledWith(PASS_MATCH_ERROR);
});
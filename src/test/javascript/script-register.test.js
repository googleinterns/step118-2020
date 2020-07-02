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
const { submitRegistration } = require('../../main/webapp/script-register');

const YES_ERROR = true;
const NO_ERROR = false;
const ERROR_MESSAGE = 'There is an error';
const NO_ERROR_MESSAGE = 'There is not an error';
const INTERNAL_ERROR_MESSAGE = 'Internal error. Please try again.';
const NO_LINK = '';
const TEST_LINK = '/test';
const TEST_NAME = 'name';
const TEST_EMAIL = 'test@example.com';

const DUMMY_HTML = '<input type="text" class="form-control" id="name" name="name">' +
        '<input type="email" class="form-control" id="email" name="email">' +
        '<div class="alert alert-danger" role="alert" id="register-error-alert" style="display: none;">' + 
        '</div>' + 
        '<div class="alert alert-success" role="alert" id="register-confirm" style="display: none;">' +
        '   Login <a href="" class="alert-link" id="register-confirm-link">here</a> to start doing good deeds!' +
        '</div>';

// mock the fetch
/*
global.fetch = jest.fn(() => 
    Promise.resolve({
        json: () => Promise.resolve({
            error: YES_ERROR,
            message: ERROR_MESSAGE,
            link: NO_LINK
        })
    })
);
*/

// clear the fetch mock before each test
beforeEach(() => {
    fetch.resetMocks();
});

test('test submitRegistration() with error', async () => {
    // set the fetch response to have an error
    fetch.mockResponseOnce(JSON.stringify({
            error: YES_ERROR,
            message: ERROR_MESSAGE,
            link: NO_LINK
        }));

    // set up dummy HTML
    document.body.innerHTML = DUMMY_HTML;
    
    // set input values for name and email
    document.getElementById('name').value = TEST_NAME;
    document.getElementById('email').value = TEST_EMAIL;

    // call the tested function
    return scriptRegister.submitRegistrationForTesting().then(response => {

        // check to make sure right fetch happened
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith(
            '/register-servlet?name=' + TEST_NAME + "&email=" + TEST_EMAIL
        );

        // check to make sure DOM manipulation worked
        expect(document.getElementById('register-error-alert').style.display).toEqual('block');
        expect(document.getElementById('register-confirm').style.display).toEqual('none');
        expect(document.getElementById('register-error-alert').innerHTML).toEqual(ERROR_MESSAGE);
    });
});

test('test submitRegistration() without error', async () => {
    // set the fetch response for no error
    fetch.mockResponseOnce(JSON.stringify({
            error: NO_ERROR,
            message: NO_ERROR_MESSAGE,
            link: TEST_LINK
        }));

    // set up dummy HTML
    document.body.innerHTML = DUMMY_HTML;

    // set input values for name and email
    document.getElementById('name').value = TEST_NAME;
    document.getElementById('email').value = TEST_EMAIL;

    // call the tested function
    return scriptRegister.submitRegistrationForTesting().then(response => {

        // check to make sure right fetch happened
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith(
            '/register-servlet?name=' + TEST_NAME + "&email=" + TEST_EMAIL
        );

        // check to make sure DOM manipulation worked
        expect(document.getElementById('register-error-alert').style.display).toEqual('none');
        expect(document.getElementById('register-confirm').style.display).toEqual('block');
        expect(document.getElementById('register-confirm-link').getAttribute('href')).toEqual(TEST_LINK);
    });
});

test('test submitRegistration() with empty return', async () => {
    // set the fetch response for no error
    fetch.mockResponseOnce(JSON.stringify({}));

    // set up dummy HTML
    document.body.innerHTML = DUMMY_HTML;
    
    // set input values for name and email
    document.getElementById('name').value = TEST_NAME;
    document.getElementById('email').value = TEST_EMAIL;

    // call the tested function
    return scriptRegister.submitRegistrationForTesting().then(response => {

        // check to make sure right fetch happened
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith(
            '/register-servlet?name=' + TEST_NAME + "&email=" + TEST_EMAIL
        );

        // check to make sure DOM manipulation worked
        expect(document.getElementById('register-error-alert').style.display).toEqual('block');
        expect(document.getElementById('register-confirm').style.display).toEqual('none');
        expect(document.getElementById('register-error-alert').innerHTML).toEqual(INTERNAL_ERROR_MESSAGE);
    });
});

test('test submitRegistration() with non-boolean error', async () => {
    // set the fetch response for no error
    fetch.mockResponseOnce(JSON.stringify({
            error: ERROR_MESSAGE
        }));

    // set up dummy HTML
    document.body.innerHTML = DUMMY_HTML;
    
    // set input values for name and email
    document.getElementById('name').value = TEST_NAME;
    document.getElementById('email').value = TEST_EMAIL;

    // call the tested function
    return scriptRegister.submitRegistrationForTesting().then(response => {

        // check to make sure right fetch happened
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith(
            '/register-servlet?name=' + TEST_NAME + "&email=" + TEST_EMAIL
        );

        // check to make sure DOM manipulation worked
        expect(document.getElementById('register-error-alert').style.display).toEqual('block');
        expect(document.getElementById('register-confirm').style.display).toEqual('none');
        expect(document.getElementById('register-error-alert').innerHTML).toEqual(INTERNAL_ERROR_MESSAGE);
    });
});

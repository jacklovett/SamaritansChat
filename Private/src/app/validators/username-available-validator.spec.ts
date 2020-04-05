import { UsernameAvailableValidator } from './username-available-validator';

import { UserService } from './../services/user.service';
import { AlertService } from './../services/alert.service';

import { UserDetailsRequest } from './../models/user.details.request';
import { Router } from '@angular/router';

let userService: UserService;
let alertService: AlertService;
let router: Router;

let usernameRequest: UserDetailsRequest;

describe('UsernameAvailableValidator', () => {

  beforeEach(() => {
    usernameRequest = new UserDetailsRequest();
    alertService = new AlertService(router);
  });

  it('should create an instance', () => {
    const directive = new UsernameAvailableValidator(
      userService, alertService, usernameRequest
    );
    expect(directive).toBeTruthy();
  });
});

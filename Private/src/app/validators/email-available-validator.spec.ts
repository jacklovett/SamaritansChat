import { EmailAvailableValidator } from './email-available-validator';

import { UserService } from './../services/user.service';
import { AlertService } from './../services/alert.service';

import { CheckUsernameEmailRequest } from './../models/check.username.email.request';

let userService: UserService;
let alertService: AlertService;

let checkUsernameEmailRequest: CheckUsernameEmailRequest

describe('EmailAvailableValidator', () => {

  beforeEach(() => {
    checkUsernameEmailRequest = <CheckUsernameEmailRequest>({
      userId: '1',
      usernameOrEmail: ''
    });
    
  });

  it('should create an instance', () => {
    const directive = new EmailAvailableValidator(
      userService, alertService, checkUsernameEmailRequest
    );
    expect(directive).toBeTruthy();
  });
});

import { Injectable } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';

import { AlertService } from '../services/alert.service';
import { UserService } from '../services/user.service';

import { UserDetailsRequest } from '../models/user.details.request';

import { checkPassword } from './../validators/check-password-validator';
import { isUsernameAvailable } from './../validators/username-available-validator';
import { isEmailAvailable } from './../validators/email-available-validator';
import { chatAvailabilityValidation } from '../validators/chat-availability-validator';
import { passwordMatchValidation } from '../validators/password-match-validator';

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  constructor(private alertService: AlertService,
    private userService: UserService) { }

  getErrorMessage(formControl: FormControl): string {

    if (formControl.errors.required) {
      return 'This is a required field';
    }

    if (formControl.errors.maxlength) {
      return 'Username must be max 20 characters long';
    }

    if (formControl.errors.email) {
      return 'Please enter a valid email address';
    }

    if (formControl.errors.pattern) {
      return 'Please enter a valid phone number';
    }

    if (formControl.errors.usernameTaken) {
      return 'This username is already taken';
    }

    if (formControl.errors.emailTaken) {
      return 'This email address is already taken';
    }

    if (formControl.errors.invalidTimes) {
      return 'Available Until time must be later than the Available From time';
    }

    if (formControl.errors.passwordIncorrect) {
      return 'Incorrect Password';
    }

    if (formControl.errors.passwordMisMatch) {
      return 'Passwords do not match';
    }

  }

  phoneNumberValidators() {
    return ['', Validators.pattern('[+0-9]+')];
  }

  usernameValidators(checkUsernameRequest: UserDetailsRequest) {
    return ['', [Validators.required, Validators.maxLength(20)], isUsernameAvailable(this.userService,
      this.alertService, checkUsernameRequest)];
  }

  emailValidators(checkEmailRequest: UserDetailsRequest) {
   return ['', [Validators.required, Validators.email], isEmailAvailable(this.userService,
    this.alertService, checkEmailRequest)];
  }

  chatAvailabilityValidators(availableFrom: string, availableUntil: string) {
    return chatAvailabilityValidation(availableFrom, availableUntil);
  }

  passwordMatchValidators(newPassword: string, confirmNewPassword: string) {
    return passwordMatchValidation(newPassword, confirmNewPassword);
  }

  checkPasswordValidators(checkPasswordRequest: UserDetailsRequest) {
    return ['', Validators.required, checkPassword(this.userService,
      this.alertService, checkPasswordRequest)];
  }
}

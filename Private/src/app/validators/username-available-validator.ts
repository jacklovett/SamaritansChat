import { Directive } from '@angular/core';
import { NG_ASYNC_VALIDATORS } from '@angular/forms';
import { Observable } from 'rxjs';
import { AsyncValidator, AsyncValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';

import { UserService } from '../services/user.service';
import { AlertService } from '../services/alert.service';
import { UserDetailsRequest } from '../models/user.details.request';

export function isUsernameAvailable(userService: UserService,
   alertService: AlertService, checkUsernameRequest: UserDetailsRequest): AsyncValidatorFn {
    return async (control: AbstractControl): Promise<ValidationErrors | null> => {
      try {
        checkUsernameRequest.value = control.value;
        const res = await userService.isUsernameAvailable(checkUsernameRequest).toPromise();
        return res.success ? null : { usernameTaken: true };
      } catch (error) {
        alertService.error(error);
        return null;
      }
    };
}

@Directive({
  selector: '[appUsernameAvailableValidator]',
  providers: [{provide: NG_ASYNC_VALIDATORS,
      useExisting: UsernameAvailableValidator,
      multi: true}],
})
export class UsernameAvailableValidator implements AsyncValidator {

  constructor(
    private userService: UserService,
    private alertService: AlertService,
    private checkUsernameRequest: UserDetailsRequest
    ) { }

  validate(control: AbstractControl):
    Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
        return isUsernameAvailable(this.userService,
          this.alertService, this.checkUsernameRequest)(control);
    }
}

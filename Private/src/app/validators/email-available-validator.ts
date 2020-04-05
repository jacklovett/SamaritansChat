import { Directive } from '@angular/core';
import { NG_ASYNC_VALIDATORS } from '@angular/forms';
import { Observable } from 'rxjs';
import { AsyncValidator, AsyncValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';

import { UserService } from '../services/user.service';
import { AlertService } from '../services/alert.service';

import { UserDetailsRequest } from '../models/user.details.request';

export function isEmailAvailable(userService: UserService,
   alertService: AlertService, checkEmailRequest: UserDetailsRequest): AsyncValidatorFn {
    return async (control: AbstractControl): Promise<ValidationErrors | null> => {
      try {
        checkEmailRequest.value = control.value;
        const res = await userService.isEmailAvailable(checkEmailRequest).toPromise();
        return res.success ? null : { emailTaken: true };
      } catch (error) {
        alertService.error(error);
        return null;
      }
    };
}

@Directive({
  selector: '[appEmailAvailableValidator]',
  providers: [{provide: NG_ASYNC_VALIDATORS,
      useExisting: EmailAvailableValidator,
      multi: true}],
})
export class EmailAvailableValidator implements AsyncValidator {
  constructor(
    private userService: UserService,
    private alertService: AlertService,
    private checkEmailRequest: UserDetailsRequest,
    ) { }

  validate(control: AbstractControl):
    Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
        return isEmailAvailable(this.userService,
           this.alertService, this.checkEmailRequest)(control);
    }
}

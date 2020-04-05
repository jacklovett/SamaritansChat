import { Directive } from '@angular/core';
import { NG_ASYNC_VALIDATORS } from '@angular/forms';
import { Observable } from 'rxjs';
import { AsyncValidator, AsyncValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';

import { UserDetailsRequest } from '../models/user.details.request';

import { AlertService } from '../services/alert.service';
import { UserService } from '../services/user.service';

export function checkPassword(userService: UserService,
  alertService: AlertService, userDetailsRequest: UserDetailsRequest): AsyncValidatorFn {
   return async (control: AbstractControl): Promise<ValidationErrors | null> => {
     try {
       userDetailsRequest.value = control.value;
       const res = await userService.checkPassword(userDetailsRequest).toPromise();
       return res.success ? null : { passwordIncorrect: true };
     } catch (error) {
       alertService.error(error);
       return null;
     }
   };
}

@Directive({
  selector: '[appCheckPasswordValidator]',
  providers: [{provide: NG_ASYNC_VALIDATORS,
      useExisting: CheckPasswordValidator,
      multi: true}],
})
export class CheckPasswordValidator implements AsyncValidator  {

  constructor(
    private userService: UserService,
    private alertService: AlertService,
    private userDetailsRequest: UserDetailsRequest
    ) { }

  validate(control: AbstractControl):
    Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
        return checkPassword(this.userService,
          this.alertService, this.userDetailsRequest)(control);
    }

}

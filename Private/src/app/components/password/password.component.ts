import { Component, Inject } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms'

import { UserDetailsRequest } from 'src/app/models/user.details.request'

import { ValidationService } from 'src/app/services/validation.service'
import { AuthenticationService } from 'src/app/services/authentication.service'

@Component({
  selector: 'app-password',
  templateUrl: './password.component.html',
  styleUrls: ['./password.component.scss'],
})
export class PasswordComponent {
  loading = false
  submitted = false

  userId: number
  passwordForm: FormGroup

  constructor(
    @Inject(MAT_DIALOG_DATA) data: any,
    private formBuilder: FormBuilder,
    private validationService: ValidationService,
    private authenticationService: AuthenticationService,
    public dialogRef: MatDialogRef<PasswordComponent>,
  ) {
    this.userId = data.userId
    const userDetailsRequest: UserDetailsRequest = <UserDetailsRequest>{
      userId: this.currentUser.userId,
    }

    this.passwordForm = this.formBuilder.group(
      {
        currentPassword: this.validationService.checkPasswordValidators(
          userDetailsRequest,
        ),
        newPassword: ['', Validators.required],
        confirmNewPassword: ['', Validators.required],
      },
      {
        validator: this.validationService.passwordMatchValidators(
          'newPassword',
          'confirmNewPassword',
        ),
      },
    )
  }

  getErrorMessage(formControl: FormControl) {
    return this.validationService.getErrorMessage(formControl)
  }

  get currentUser() {
    return this.authenticationService.getUserDetailsFromJWT()
  }

  // convenience getter for easy access to form fields
  get controls() {
    return this.passwordForm.controls
  }

  onSubmit() {
    this.submitted = true

    if (!this.passwordForm.valid) {
      return
    }

    const passwordRequest: UserDetailsRequest = <UserDetailsRequest>{
      userId: this.userId,
      value: this.controls.newPassword.value,
    }

    this.dialogRef.close(passwordRequest)
  }
}

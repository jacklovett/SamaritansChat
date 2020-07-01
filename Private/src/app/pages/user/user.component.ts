import { Component, OnInit, OnDestroy } from '@angular/core'
import { Subscription } from 'rxjs'
import { Router, ActivatedRoute } from '@angular/router'
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms'
import { MatDialog, MatDialogConfig } from '@angular/material/dialog'

import { User } from 'src/app/models/user'
import { UserDetailsRequest } from 'src/app/models/user.details.request'

import { PasswordComponent } from 'src/app/components/password/password.component'

import { UserService } from 'src/app/services/user.service'
import { AlertService } from 'src/app/services/alert.service'
import { ValidationService } from 'src/app/services/validation.service'
import { AuthenticationService } from 'src/app/services/authentication.service'

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit, OnDestroy {
  user: User
  editUserId: number
  userForm: FormGroup
  dummyPassword = '**********'

  loading = false
  submitted = false

  private userSubscription: Subscription
  private editUserSubscription: Subscription

  constructor(
    public passwordDialog: MatDialog,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private alertService: AlertService,
    private validationService: ValidationService,
    private authenticationService: AuthenticationService,
  ) {}

  ngOnInit() {
    this.editUserSubscription = this.route.params.subscribe((params) => {
      this.editUserId = params['id']

      if (!this.editUserId) {
        return this.buildRegisterForm()
      }

      // non admin users can only edit their own user details
      if (
        this.editUserId !== this.currentUser.userId &&
        !this.currentUser.admin
      ) {
        this.router.navigate(['/chat'])
      }

      this.loadUser()
      this.buildEditForm()
    })
  }

  get title() {
    return this.editUserId ? 'User' : 'Add User'
  }

  get currentUser() {
    return this.authenticationService.getUserDetailsFromJWT()
  }

  // convenience getter for easy access to form fields
  get controls() {
    return this.userForm.controls
  }

  getErrorMessage(formControl: FormControl) {
    return this.validationService.getErrorMessage(formControl)
  }

  openPasswordDialog() {
    const dialogConfig = new MatDialogConfig()

    dialogConfig.disableClose = true
    dialogConfig.autoFocus = false

    dialogConfig.data = { userId: this.editUserId }

    dialogConfig.position = {
      'top': '32px',
    }

    const dialogRef = this.passwordDialog.open(PasswordComponent, dialogConfig)

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.updatePassword(result)
      }
    })
  }

  public onSubmit() {
    this.submitted = true

    if (!this.userForm.valid) {
      return
    }

    this.loading = true

    return this.editUserId ? this.edit() : this.register()
  }

  private buildEditForm() {
    const checkUsernameEmailRequest: UserDetailsRequest = <UserDetailsRequest>{
      userId: this.editUserId,
      value: '',
    }

    this.userForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      contactNumber: this.validationService.phoneNumberValidators(),
      email: this.validationService.emailValidators(checkUsernameEmailRequest),
      username: [{ value: '', disabled: true }],
      password: [{ value: '', disabled: true }],
      admin: false,
    })
  }

  private buildRegisterForm() {
    const checkUsernameEmailRequest: UserDetailsRequest = new UserDetailsRequest()

    this.userForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      contactNumber: this.validationService.phoneNumberValidators(),
      email: this.validationService.emailValidators(checkUsernameEmailRequest),
      username: this.validationService.usernameValidators(
        checkUsernameEmailRequest,
      ),
      password: ['', Validators.required],
      admin: false,
    })
  }

  private populateForm(user: User) {
    if (!user) {
      return
    }

    this.controls.firstName.setValue(user.firstName)
    this.controls.lastName.setValue(user.lastName)
    this.controls.contactNumber.setValue(user.contactNumber)
    this.controls.email.setValue(user.email)
    this.controls.username.setValue(user.username)
    this.controls.password.setValue(this.dummyPassword)
    this.controls.admin.setValue(user.admin)
  }

  private updatePassword(passwordRequest: UserDetailsRequest) {
    this.userService.updatePassword(passwordRequest).subscribe(
      (response) => {
        this.alertService.handleResponse(response)
      },
      (error) => {
        this.alertService.error(error)
      },
    )
  }

  private async loadUser() {
    this.loading = true
    this.userSubscription = this.userService.getById(this.editUserId).subscribe(
      (user) => {
        this.user = user
        this.populateForm(this.user)
      },
      (error) => {
        this.alertService.error(error)
      },
    )
    this.loading = false
  }

  private edit() {
    const user: User = <User>{
      firstName: this.controls.firstName.value,
      lastName: this.controls.lastName.value,
      contactNumber: this.controls.contactNumber.value,
      email: this.controls.email.value,
      admin: this.controls.admin.value,
    }

    user.id = this.user.id
    this.userService.update(user)
    this.loading = false
  }

  private register() {
    const user: User = <User>{
      firstName: this.controls.firstName.value,
      lastName: this.controls.lastName.value,
      contactNumber: this.controls.contactNumber.value,
      email: this.controls.email.value,
      username: this.controls.username.value,
      password: this.controls.password.value,
      admin: this.controls.admin.value,
    }
    this.userService.register(user)
    this.loading = false
  }

  ngOnDestroy() {
    this.editUserSubscription.unsubscribe()

    if (this.userSubscription) {
      this.userSubscription.unsubscribe()
    }
  }
}

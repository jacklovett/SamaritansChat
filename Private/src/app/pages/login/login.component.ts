import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormControl,
} from '@angular/forms';

import { AlertService } from 'src/app/services/alert.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { AppComponent } from './../../app.component';
import { ValidationService } from 'src/app/services/validation.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private validationService: ValidationService,
    private appComponent: AppComponent,
  ) {
    // redirect if already logged in
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/users']);
    }
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });

    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/users';
  }

  // convenience getter for easy access to form fields
  get controls() {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;

    this.authenticationService
      .login(this.controls.username.value, this.controls.password.value)
      .subscribe(
        () => {
          // refresh appComponent to ensure admin property is set correctly
          // see if you can resolve this with a subscribe/subscription
          this.appComponent.ngOnInit();
          this.router.navigate([this.returnUrl]);
        },
        error => {
          this.alertService.error(error);
          this.loading = false;
        },
      );
  }

  getErrorMessage(formControl: FormControl) {
    return this.validationService.getErrorMessage(formControl);
  }
}

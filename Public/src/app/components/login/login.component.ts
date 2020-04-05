import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

import { MatDialog, MatDialogConfig } from '@angular/material/dialog';

import { AlertService } from 'src/app/services/alert.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { DialogComponent } from '../dialog/dialog.component';
import { ChatAvailabilityResponse } from 'src/app/models/chat.availability.response';
import { ChatAvailabilityService } from 'src/app/services/chat.availability.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  returnUrl = '';
  chatAvailabilityResponse: ChatAvailabilityResponse;
  chatAvailabilitySubscription: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private chatAvailabilityService: ChatAvailabilityService,
    public dialog: MatDialog,
  ) {
    // redirect if already logged in
    if (this.authenticationService.isAuthenticated) {
      this.router.navigate(['/']);
    }

    this.chatAvailabilityService.connect();
    this.chatAvailabilitySubscription = this.chatAvailabilityService
      .getChatAvailability()
      .subscribe((response: ChatAvailabilityResponse) => {
        this.chatAvailabilityResponse = response;
      });

    this.isChatAvailable();
  }

  ngOnInit() {
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  public onSubmit() {
    this.submitted = true;
    this.loading = true;

    this.authenticationService.login().subscribe(
      () => {
        this.router.navigate([this.returnUrl]);
      },
      error => {
        this.alertService.error(error);
        this.loading = false;
      },
    );
  }

  public openDisclaimerDialog(): void {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = false;

    dialogConfig.data = {
      title: 'Disclaimer',
      content:
        'You are about to be put in contact with one of our trained representatives. ' +
        'You are completely anonymous, and no information you disclose will be stored. ' +
        'However, by continuing you agree that if you decide to provide us with your details, ' +
        'we have a duty of care and will take the necessary steps to prevent any harm to you.',
      submitMessage: 'I agree, continue',
      submitValue: 'login',
      cancel: 'Go Back',
    };

    dialogConfig.position = {
      top: '48px',
    };

    const dialogRef = this.dialog.open(DialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'login') {
        this.onSubmit();
      }
    });
  }

  private async isChatAvailable() {
    this.loading = true;
    try {
      const response = await this.chatAvailabilityService
        .isChatAvailable()
        .toPromise();
      this.chatAvailabilityResponse = response;
    } catch (error) {
      this.alertService.error(error);
    }
    this.loading = false;
  }

  ngOnDestroy() {
    this.chatAvailabilitySubscription.unsubscribe();
  }
}

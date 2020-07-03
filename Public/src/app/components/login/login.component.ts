import { Component, OnInit, OnDestroy } from '@angular/core'
import { Router, ActivatedRoute } from '@angular/router'
import { Subscription } from 'rxjs'
import { MatDialog, MatDialogConfig } from '@angular/material/dialog'
import { ReCaptchaV3Service } from 'ng-recaptcha'

import { AlertService } from 'src/app/services/alert.service'
import { AuthenticationService } from 'src/app/services/authentication.service'
import { DialogComponent } from '../dialog/dialog.component'
import { ChatAvailabilityResponse } from 'src/app/models/chat.availability.response'
import { RxStompService } from '@stomp/ng2-stompjs'
import { Message } from 'stompjs'

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loading = false
  submitted = false
  returnUrl = ''

  chatAvailabilityResponse: ChatAvailabilityResponse

  reCaptchaSubscription: Subscription
  chatAvailabilitySubscription: Subscription

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alertService: AlertService,
    private recaptchaV3Service: ReCaptchaV3Service,
    private authenticationService: AuthenticationService,
    private rxStompService: RxStompService,
    public dialog: MatDialog,
  ) {
    // redirect if already logged in
    if (this.authenticationService.isAuthenticated) {
      this.router.navigate(['/'])
    }

    this.isChatAvailable()
  }

  ngOnInit() {
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/'

    this.chatAvailabilitySubscription = this.rxStompService
      .watch('/topic/availability')
      .subscribe((message: Message) => {
        this.chatAvailabilityResponse = JSON.parse(message.body)
      })
  }

  executeRecaptcha(): void {
    this.loading = true
    this.reCaptchaSubscription = this.recaptchaV3Service
      .execute('register')
      .subscribe(
        (token) => {
          this.onSubmit(token)
        },
        (error) => {
          this.alertService.error(error)
          this.loading = false
        },
      )
  }

  onSubmit(token: string) {
    this.submitted = true

    this.authenticationService.login(token).subscribe(
      () => {
        this.router.navigate([this.returnUrl])
      },
      (error) => {
        this.alertService.error(error)
        this.loading = false
      },
    )
  }

  openDisclaimerDialog() {
    const dialogConfig = new MatDialogConfig()

    dialogConfig.disableClose = true
    dialogConfig.autoFocus = false
    dialogConfig.data = {
      title: 'Disclaimer',
      content:
        'You are about to be put in contact with one of our trained representatives. ' +
        'You are completely anonymous, and no information you disclose will be stored. ' +
        'However, by continuing you agree that if you decide to provide us with your details, ' +
        'we have a duty of care and will take the necessary steps to prevent any harm to you.',
      successLabel: 'I agree, continue',
      cancelLabel: 'Go Back',
    }

    dialogConfig.position = {
      top: '48px',
    }

    const dialogRef = this.dialog.open(DialogComponent, dialogConfig)

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.executeRecaptcha()
      }
    })
  }

  private async isChatAvailable() {
    this.loading = true
    try {
      this.chatAvailabilityResponse = await this.authenticationService
        .isChatAvailable()
        .toPromise()
    } catch (error) {
      this.alertService.error(error)
    }
    this.loading = false
  }

  ngOnDestroy() {
    this.chatAvailabilitySubscription.unsubscribe()
    this.reCaptchaSubscription.unsubscribe()
  }
}

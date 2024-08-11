import { Component } from '@angular/core'
import { NavigationEnd, Router } from '@angular/router'
import { MatDialog, MatDialogConfig } from '@angular/material/dialog'
import { filter } from 'rxjs/operators'

import { AuthenticationService } from './services/authentication.service'
import { DialogComponent } from './components/dialog/dialog.component'
import { ChatService } from './services/chat.service'

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {
  isChatVisible = true

  constructor(
    private router: Router,
    private chatService: ChatService,
    private authenticationService: AuthenticationService,
    public dialog: MatDialog,
  ) {}

  ngOnInit() {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) =>
        event.urlAfterRedirects === '/chat'
          ? document.body.classList.add('hide-recaptcha')
          : document.body.classList.remove('hide-recaptcha'),
      )
  }

  leaveChatDialog() {
    const dialogConfig = new MatDialogConfig()

    dialogConfig.disableClose = true
    dialogConfig.autoFocus = false

    dialogConfig.data = {
      title: 'Leave Chat',
      content: 'Are you sure you want to leave the current chat session?',
      successLabel: 'Continue',
      cancelLabel: 'Go Back',
    }

    dialogConfig.position = {
      top: '32px',
    }

    const dialogRef = this.dialog.open(DialogComponent, dialogConfig)

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.logout()
      }
    })
  }

  logout() {
    this.chatService.disconnect()
    this.authenticationService.logout()
  }

  toggleChat() {
    this.isChatVisible = !this.isChatVisible
    const display = this.isChatVisible ? 'show' : 'hide'
    this.router.navigate(['chat'], { queryParams: { display } })
  }

  get isAuthenticated() {
    return this.authenticationService.isAuthenticated()
  }
}

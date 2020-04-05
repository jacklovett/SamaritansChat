import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AdminGuard } from './guards/admin.guard';
import { AuthenticationService } from './services/authentication.service';
import { ChatService } from './services/chat.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {
  isAdmin: boolean;

  constructor(
    private router: Router,
    private adminGuard: AdminGuard,
    private chatService: ChatService,
    private authenticationService: AuthenticationService,
  ) {}

  ngOnInit() {
    this.isAdmin = this.adminGuard.isAdmin();
  }

  get currentUser() {
    return this.authenticationService.getUserDetailsFromJWT();
  }

  logout() {
    this.chatService.disconnect();
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }

  viewProfile() {
    this.router.navigate(['user', this.currentUser.userId]);
  }

  get isAuthenticated() {
    return this.authenticationService.isAuthenticated();
  }
}

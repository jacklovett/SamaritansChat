import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';

import { AuthenticationService } from './../services/authentication.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (this.isAdmin()) {
      return true;
    }

    // if user is not an admin but has logged in, then direct them to the chat page
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/chat'], {
        queryParams: { returnUrl: state.url },
      });
    }
    return false;
  }

  public isAdmin() {
    const details = this.authenticationService.getUserDetailsFromJWT();
    return details?.admin;
  }
}

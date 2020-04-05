import { Injectable } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';
import { Observable, Subject } from 'rxjs';

import { ApiResponse } from './../models/api.response';
import { Alert } from '../models/alert';

@Injectable({ providedIn: 'root' })
export class AlertService {
  private subject = new Subject<Alert>();
  private keepAfterNavigationChange = false;

  constructor(private router: Router) {
    // clear alert message on route change
    this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (this.keepAfterNavigationChange) {
          // only keep for a single location change
          this.keepAfterNavigationChange = false;
        } else {
          // clear alert
          this.close();
        }
      }
    });
  }

  handleResponse(response: ApiResponse) {
    return response.success
      ? this.success(response.message)
      : this.error(response.message);
  }

  success(message: string, keepAfterNavigationChange = false) {
    this.keepAfterNavigationChange = keepAfterNavigationChange;
    this.subject.next({ type: 'success', message });
  }

  error(message: string, keepAfterNavigationChange = false) {
    this.keepAfterNavigationChange = keepAfterNavigationChange;
    const errorMessage = message ?? 'Unexpected Error';
    this.subject.next({ type: 'error', message: errorMessage });
  }

  getMessage(): Observable<Alert> {
    return this.subject.asObservable();
  }

  close() {
    this.subject.next();
  }
}

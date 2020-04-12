import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from './../../environments/environment';

import { User } from './../models/user';

export interface UserDetails {
  userId: string;
  username: string;
}

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  apiUrl: string = environment.apiUrl;
  public currentUser: Observable<User>;
  private currentUserSubject: BehaviorSubject<User>;

  constructor(private http: HttpClient, private router: Router) {
    this.currentUserSubject = new BehaviorSubject<User>(
      JSON.parse(localStorage.getItem('currentUser')),
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  login(token: string) {
    return this.http.post<any>(`${this.apiUrl}/auth/chat/login`, token).pipe(
      map((jwt) => {
        if (jwt?.token) {
          // store jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify(jwt));
          this.currentUserSubject.next(jwt);
        }
      }),
    );
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  public getUsername(): string {
    return this.getUserDetails().username;
  }

  public getUserDetails(): UserDetails {
    if (this.currentUserValue?.token) {
      const jwt = this.currentUserValue.token;
      const jwtData = jwt.split('.')[1];
      const decodedJwtData = JSON.parse(window.atob(jwtData));
      return JSON.parse(decodedJwtData.sub);
    } else {
      console.log('No token found');
      this.logout();
    }
  }

  isAuthenticated(): boolean {
    if (this.currentUserValue) {
      return true;
    }
  }

  public get currentUserValue(): User {
    return this.currentUserSubject.value;
  }
}

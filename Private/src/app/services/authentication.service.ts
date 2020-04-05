import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from './../../environments/environment';

import { User } from './../models/user';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  apiUrl: string = environment.apiUrl;

  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User>(
      JSON.parse(localStorage.getItem('currentUser')),
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User {
    return this.currentUserSubject.value;
  }

  login(usernameOrEmail: string, password: string) {
    return this.http
      .post<any>(`${this.apiUrl}/auth/login`, { usernameOrEmail, password })
      .pipe(
        map(user => {
          // login successful if there's a jwt token in the response
          if (user.token) {
            // store user details and jwt token in local storage to keep user logged in between page refreshes
            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);
          }
          return user;
        }),
      );
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  public getUserDetailsFromJWT() {
    const jwt = this.currentUserValue?.token;

    if (jwt) {
      const jwtData = jwt.split('.')[1];
      const decodedJwtData = JSON.parse(window.atob(jwtData));
      return JSON.parse(decodedJwtData.sub);
    }
  }

  isAuthenticated(): boolean {
    if (this.currentUserValue) {
      return true;
    }
  }
}

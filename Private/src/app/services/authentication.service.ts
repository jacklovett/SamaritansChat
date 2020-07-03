import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { BehaviorSubject } from 'rxjs'
import { map } from 'rxjs/operators'

import { environment } from './../../environments/environment'

import { JwtResponse } from '../models/jwt.response'
import { Router } from '@angular/router'

export interface UserDetails {
  userId: number
  username: string
  admin: boolean
}

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  apiUrl: string = environment.apiUrl

  private jwtSubject: BehaviorSubject<JwtResponse>

  constructor(private http: HttpClient, private router: Router) {
    this.jwtSubject = new BehaviorSubject<JwtResponse>(
      JSON.parse(localStorage.getItem('jwt')),
    )
  }

  get jwtResponse(): JwtResponse {
    return this.jwtSubject.value
  }

  login(usernameOrEmail: string, password: string) {
    return this.http
      .post<JwtResponse>(`${this.apiUrl}/auth/login`, {
        usernameOrEmail,
        password,
      })
      .pipe(
        map((jwt) => {
          if (!jwt.token) {
            return
          }

          // store jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('jwt', JSON.stringify(jwt))
          this.jwtSubject.next(jwt)
        }),
      )
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('jwt')
    this.jwtSubject.next(null)
    this.router.navigate(['/login'])
  }

  getUserDetailsFromJWT(): UserDetails {
    const jwtToken = this.jwtResponse?.token
    if (!jwtToken) {
      console.log('No token found')
      this.logout()
      return
    }
    const jwtData = jwtToken.split('.')[1]
    const decodedJwtData = JSON.parse(window.atob(jwtData))
    return JSON.parse(decodedJwtData.sub)
  }

  isAuthenticated(): boolean {
    if (this.jwtResponse?.token) {
      return true
    }
  }
}

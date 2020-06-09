import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Router } from '@angular/router'
import { BehaviorSubject } from 'rxjs'
import { map } from 'rxjs/operators'

import { environment } from './../../environments/environment'

import { ChatAvailabilityResponse } from '../models/chat.availability.response'
import { JwtResponse } from '../models/jwt.response'

export interface UserDetails {
  userId: string
  username: string
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

  login(token: string) {
    return this.http
      .post<JwtResponse>(`${this.apiUrl}/auth/chat/login`, token)
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
    localStorage.removeItem('jwt')
    this.jwtSubject.next(null)
    this.router.navigate(['/login'])
  }

  public getUsername(): string {
    return this.getUserDetails().username
  }

  public getUserDetails(): UserDetails {
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

  public isChatAvailable() {
    return this.http.get<ChatAvailabilityResponse>(
      `${this.apiUrl}/chat/availability`,
    )
  }

  isAuthenticated(): boolean {
    if (this.jwtResponse?.token) {
      return true
    }
  }

  public get jwtResponse(): JwtResponse {
    return this.jwtSubject.value
  }
}

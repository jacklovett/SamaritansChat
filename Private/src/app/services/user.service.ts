import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'

import { environment } from 'src/environments/environment'

import { User } from './../models/user'
import { ApiResponse } from './../models/api.response'
import { UserDetailsRequest } from 'src/app/models/user.details.request'
import { AlertService } from './alert.service'
import { Subject, Observable } from 'rxjs'
import { AuthenticationService } from './authentication.service'
import { Router } from '@angular/router'

@Injectable({ providedIn: 'root' })
export class UserService {
  apiUrl: string = environment.apiUrl

  private isReloadRequiredSubject = new Subject<boolean>()

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private router: Router,
    private authenticationService: AuthenticationService,
  ) {}

  isReloadRequired(): Observable<boolean> {
    return this.isReloadRequiredSubject.asObservable()
  }

  get currentUser() {
    return this.authenticationService.getUserDetailsFromJWT()
  }

  get() {
    return this.http.get<User[]>(`${this.apiUrl}/users`)
  }

  getById(id: number) {
    return this.http.get<User>(`${this.apiUrl}/users/${id}`)
  }

  register(user: User) {
    this.http
      .post<ApiResponse>(`${this.apiUrl}/users/register`, user)
      .subscribe(
        (response) => {
          if (!response) {
            return
          }
          this.isReloadRequiredSubject.next(true)
          this.router.navigate(['users'])
          this.alertService.handleResponse(response)
        },
        (error) => {
          this.alertService.error(error)
        },
      )
  }

  update(user: User) {
    this.http.put<ApiResponse>(`${this.apiUrl}/users/edit`, user).subscribe(
      (response) => {
        if (!response) {
          return
        }

        this.isReloadRequiredSubject.next(true)

        if (this.currentUser.admin) {
          this.router.navigate(['users'])
        }

        this.alertService.handleResponse(response)
      },
      (error) => {
        this.alertService.error(error)
      },
    )
  }

  delete(id: number) {
    this.http
      .delete<ApiResponse>(`${this.apiUrl}/users/delete/${id}`)
      .subscribe(
        (response) => {
          if (!response) {
            return
          }
          this.isReloadRequiredSubject.next(true)
          this.alertService.handleResponse(response)
        },
        (error) => {
          this.alertService.error(error)
        },
      )
  }

  updatePassword(changePasswordRequest: UserDetailsRequest) {
    return this.http.put<ApiResponse>(
      `${this.apiUrl}/users/updatePassword`,
      changePasswordRequest,
    )
  }

  checkPassword(checkPasswordRequest: UserDetailsRequest) {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/users/checkCurrentPassword`,
      checkPasswordRequest,
    )
  }

  isUsernameAvailable(checkUsernameRequest: UserDetailsRequest) {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/users/checkUsernameAvailability`,
      checkUsernameRequest,
    )
  }

  isEmailAvailable(checkEmailRequest: UserDetailsRequest) {
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/users/checkEmailAvailablilty`,
      checkEmailRequest,
    )
  }
}

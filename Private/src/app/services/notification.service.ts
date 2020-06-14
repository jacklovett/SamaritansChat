import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Subject, Observable } from 'rxjs'

import { AlertService } from './alert.service'
import { AuthenticationService } from './authentication.service'

import { environment } from 'src/environments/environment'

import { ApiResponse } from '../models/api.response'
import {
  Notification,
  PartialNotification,
} from 'src/app/components/notifications/notification'
import { ConversationRequest } from '../models/conversation.request'

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private apiUrl: string = environment.apiUrl

  private isReloadRequiredSubject = new Subject<boolean>()

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
  ) {}

  public get currentUser() {
    return this.authenticationService.getUserDetailsFromJWT()
  }

  public isReloadRequired(): Observable<boolean> {
    return this.isReloadRequiredSubject.asObservable()
  }

  get() {
    return this.http.get<Notification[]>(
      `${this.apiUrl}/notifications/${this.currentUser.userId}`,
    )
  }

  async update(notification: PartialNotification) {
    try {
      await this.http
        .put(`${this.apiUrl}/notifications/edit`, notification)
        .toPromise()
      this.isReloadRequiredSubject.next(true)
    } catch (error) {
      console.log(error)
    }
  }

  async delete(id: number) {
    try {
      const response = await this.http
        .delete<ApiResponse>(`${this.apiUrl}/notifications/delete/${id}`)
        .toPromise()

      if (response.success) {
        this.isReloadRequiredSubject.next(true)
      }
    } catch (error) {
      this.alertService.error(error)
    }
  }

  public startConversation(conversationRequest: ConversationRequest) {
    conversationRequest.samaritansUser = this.currentUser.username
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/chat/startConversation`,
      conversationRequest,
    )
  }
}

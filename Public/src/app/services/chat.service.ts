import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Subject } from 'rxjs'

import { ChatMessage } from '../models/chat.message'
import { environment } from './../../environments/environment'

import { AlertService } from './alert.service'
import { AuthenticationService } from './authentication.service'
import { ApiResponse } from '../models/api.response'
import { RxStompService } from '@stomp/ng2-stompjs'

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private apiUrl: string = environment.apiUrl

  samaritansUsername = 'Sam'

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private rxStompService: RxStompService,
    private authenticationService: AuthenticationService,
  ) {}

  public publishMessage = (destination: string, message: ChatMessage) => {
    try {
      this.rxStompService.publish({
        destination: `/app/${destination}`,
        body: JSON.stringify(message),
      })
    } catch (error) {
      this.alertService.error(error)
    }
  }

  get username() {
    return this.authenticationService.getUsername()
  }

  public getConversation() {
    return this.http.get<ChatMessage[]>(
      `${this.apiUrl}/chat/conversation/${this.username}`,
    )
  }

  public isVolunteerActive() {
    return this.http.get<ApiResponse>(
      `${this.apiUrl}/chat/isVolunteerActive/${this.username}`,
    )
  }

  public disconnect() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.username,
      recipient: this.samaritansUsername,
      type: 'LEAVE',
    }

    this.publishMessage('send.disconnect', message)
    localStorage.removeItem('conversationInProgress')
  }
}

import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { RxStompService } from '@stomp/ng2-stompjs'

import { ChatMessage } from '../models/chat.message'

import { AlertService } from './alert.service'
import { AuthenticationService } from './authentication.service'

import { environment } from 'src/environments/environment'

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private apiUrl: string = environment.apiUrl

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private rxStompService: RxStompService,
    private authenticationService: AuthenticationService,
  ) {
    this.connect()
  }

  get currentUsername() {
    return this.authenticationService.getUserDetailsFromJWT()?.username
  }

  publishMessage = (destination: string, message: ChatMessage) => {
    try {
      this.rxStompService.publish({
        destination: `/app/${destination}`,
        body: JSON.stringify(message),
      })
    } catch (error) {
      this.alertService.error(error)
    }
  }

  connect() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      type: 'JOIN',
    }

    this.publishMessage('send.addActiveUser', message)

    if (!JSON.parse(localStorage.getItem('isChatConnected'))) {
      localStorage.setItem('isChatConnected', JSON.stringify(true))
    }
  }

  disconnect() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      type: 'LEAVE',
    }

    this.publishMessage('send.disconnect', message)
    this.clearLocalStorage()
  }

  fetchChatMessages(username: string) {
    return this.http.get<ChatMessage[]>(
      `${this.apiUrl}/chat/messages/${username}`,
    )
  }

  fetchChatUsers() {
    return this.http.get<string[]>(
      `${this.apiUrl}/chat/users/${this.currentUsername}`,
    )
  }

  updateUnreadMessages(username: string) {
    this.http
      .put(`${this.apiUrl}/chat/updateUnreadMessages`, username)
      .subscribe(
        () => {},
        (error) => {
          console.log(
            `Unable to update unread messages for user: ${username} - ${error}`,
          )
        },
      )
  }

  clearLocalStorage() {
    localStorage.removeItem('activeChat')
    localStorage.removeItem('isChatConnected')
  }
}

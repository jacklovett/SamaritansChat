import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { RxStompService } from '@stomp/ng2-stompjs'

import { ChatUser } from '../models/chat.user'
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
    return this.authenticationService.getUserDetailsFromJWT().username
  }

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

  public connect() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      type: 'JOIN',
    }

    this.publishMessage('send.addActiveUser', message)

    if (!JSON.parse(localStorage.getItem('isChatConnected'))) {
      localStorage.setItem('isChatConnected', JSON.stringify(true))
    }
  }

  public disconnect() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      type: 'LEAVE',
    }

    this.publishMessage('send.disconnect', message)
    this.clearLocalStorage()
  }

  public getConversationByUsername(username: string) {
    return this.http.get<ChatMessage[]>(
      `${this.apiUrl}/chat/conversation/${username}`,
    )
  }

  public fetchChatUsers() {
    return this.http.get<ChatUser[]>(
      `${this.apiUrl}/chat/users/${this.currentUsername}`,
    )
  }

  public updateUnreadMessages(username: string) {
    this.http
      .put(`${this.apiUrl}/chat/updateUnreadMessages`, username)
      .subscribe((error) => {
        console.log(
          `An error occured while updating unread chat messages: ${error}`,
        )
      })
  }

  clearLocalStorage() {
    localStorage.removeItem('activeChat')
    localStorage.removeItem('isChatConnected')
  }
}

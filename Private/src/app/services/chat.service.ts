import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Subject, Observable } from 'rxjs'

import { Client, Message, over } from 'stompjs'
import * as SockJS from 'sockjs-client'

import { AlertService } from './alert.service'
import { environment } from 'src/environments/environment'

import { ChatUser } from '../models/chat.user'
import { ChatMessage } from '../models/chat.message'
import { AuthenticationService } from './authentication.service'

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private chatUrl: string = environment.chatUrl
  private apiUrl: string = environment.apiUrl

  private activeUsersSubject = new Subject<ChatUser>()
  private disconnectedUsersSubject = new Subject<string>()
  private chatMessagesSubject = new Subject<ChatMessage>()

  stompClient: Client

  activeUsers: ChatUser[] = []
  messages: ChatMessage[] = []

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
  ) {}

  public connect() {
    const socket = new SockJS(this.chatUrl)
    this.stompClient = over(socket)
    const _this = this
    _this.stompClient.connect(
      {},
      () => {
        this.stompClient.subscribe(
          `/topic/${this.currentUsername}`,
          (message: Message) => {
            _this.onMessageRecieved(JSON.parse(message.body))
          },
        )

        this.stompClient.subscribe(
          `/topic/users.${this.currentUsername}`,
          (message: Message) => {
            _this.handleActiveUserResponse(message)
          },
        )

        this.connectToChat()
        this.setConnected(true)
      },
      (error: string) => {
        this.alertService.error(
          `Unable to connect to WebSocket server: ${error}`,
        )
      },
    )
  }

  get currentUsername() {
    return this.authenticationService.getUserDetailsFromJWT().username
  }

  public send(message: ChatMessage) {
    try {
      this.stompClient.send('/app/send.message', {}, JSON.stringify(message))
    } catch (error) {
      this.alertService.error(error)
    }
  }

  public disconnect() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      type: 'LEAVE',
    }

    try {
      this.stompClient.send('/app/send.disconnect', {}, JSON.stringify(message))
      this.stompClient.disconnect(() => {
        this.setConnected(false)
      })
    } catch (error) {
      this.alertService.error(error)
    }

    this.clearLocalStorage()
  }

  public getChatUsers(): Observable<ChatUser> {
    return this.activeUsersSubject.asObservable()
  }

  public getDisconnectedChatUsers(): Observable<string> {
    return this.disconnectedUsersSubject.asObservable()
  }

  public getChatMessages(): Observable<ChatMessage> {
    return this.chatMessagesSubject.asObservable()
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

  public async connectToChat() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      type: 'JOIN',
    }

    try {
      this.stompClient.send(
        '/app/send.addActiveUser',
        {},
        JSON.stringify(message),
      )
    } catch (error) {
      this.alertService.error(error)
    }
  }

  private handleActiveUserResponse(message: Message) {
    const users = JSON.parse(message.body)
    if (!users) {
      console.log('No active users found')
      return
    }

    users.forEach((user: ChatUser) => {
      this.activeUsersSubject.next(user)
    })
  }

  private onMessageRecieved(message: ChatMessage) {
    const { type, sender } = message
    if (type === 'CHAT') {
      this.chatMessagesSubject.next(message)
      // need to handle case when another samaritan volunteer logs in
    }

    if (type === 'JOIN' && sender !== this.currentUsername) {
      const newChatUser: ChatUser = <ChatUser>{
        username: sender,
        unreadMessageCount: 0,
      }
      this.activeUsersSubject.next(newChatUser)
    }

    if (type === 'LEAVE') {
      this.disconnectedUsersSubject.next(sender)
    }
  }

  private setConnected(connected: boolean) {
    return connected ? console.log('Connected!') : console.log('Disconnected!')
  }

  clearLocalStorage() {
    localStorage.removeItem('activeChat')
  }
}

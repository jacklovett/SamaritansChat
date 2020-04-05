import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject, Observable } from 'rxjs';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

import { AlertService } from './alert.service';
import { environment } from 'src/environments/environment';

import { ChatUser } from '../models/chat.user';
import { ChatMessage } from '../models/chat.message';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private chatUrl: string = environment.chatUrl;
  private apiUrl: string = environment.apiUrl;

  private activeUsersSubject = new Subject<any>();
  private disconnectedUsersSubject = new Subject<any>();
  private chatMessagesSubject = new Subject<any>();

  stompClient: any;

  activeUsers: ChatUser[] = [];
  messages: ChatMessage[] = [];

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
  ) {}

  public connect() {
    const socket = new SockJS(this.chatUrl);
    this.stompClient = Stomp.over(socket);
    const _this = this;
    _this.stompClient.connect(
      {},
      () => {
        this.stompClient.subscribe('/chat/' + this.currentUsername, function(
          msg: any,
        ) {
          _this.onMessageRecieved(JSON.parse(msg.body));
        });

        this.stompClient.subscribe(
          '/chat/users/' + this.currentUsername,
          function(msg: any) {
            _this.handleActiveUserResponse(msg);
          },
        );

        this.connectToChat();
        this.setConnected(true);
      },
      (error: string) => {
        this.alertService.error(
          `Unable to connect to WebSocket server: ${error}`,
        );
      },
    );
  }

  get currentUsername() {
    return this.authenticationService.getUserDetailsFromJWT().username;
  }

  public send(message: ChatMessage) {
    try {
      this.stompClient.send('/app/send/message', {}, JSON.stringify(message));
    } catch (error) {
      this.alertService.error(error);
    }
  }

  public disconnect() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      type: 'LEAVE',
    };

    if (this.stompClient) {
      try {
        this.stompClient.send(
          '/app/send/disconnect',
          {},
          JSON.stringify(message),
        );
        this.stompClient.disconnect();
        this.setConnected(false);
      } catch (error) {
        this.alertService.error(error);
      }
    }

    this.clearLocalStorage();
  }

  public getChatUsers(): Observable<any> {
    return this.activeUsersSubject.asObservable();
  }

  public getDisconnectedChatUsers(): Observable<any> {
    return this.disconnectedUsersSubject.asObservable();
  }

  public getChatMessages(): Observable<ChatMessage[]> {
    return this.chatMessagesSubject.asObservable();
  }

  public getConversationByUsername(username: string) {
    return this.http.get<ChatMessage[]>(
      `${this.apiUrl}/chat/conversation/${username}`,
    );
  }

  public fetchChatUsers() {
    return this.http.get<ChatUser[]>(
      `${this.apiUrl}/chat/users/${this.currentUsername}`,
    );
  }

  public updateUnreadMessages(username: string) {
    this.http
      .put(`${this.apiUrl}/chat/updateUnreadMessages`, username)
      .subscribe(
        result => {},
        error => {
          console.log(
            `An error occured while updating unread chat messages: ${error}`,
          );
        },
      );
  }

  public async connectToChat() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.currentUsername,
      type: 'JOIN',
    };

    try {
      this.stompClient.send(
        '/app/send/addActiveUser',
        {},
        JSON.stringify(message),
      );
    } catch (error) {
      this.alertService.error(error);
    }
  }

  private handleActiveUserResponse(message: any) {
    const users = JSON.parse(message.body);
    if (!users) {
      console.log('No active users found');
      return;
    }

    users.forEach((u: any) => {
      this.activeUsersSubject.next(u);
    });
  }

  private onMessageRecieved(message: any) {
    if (message.type === 'CHAT') {
      this.chatMessagesSubject.next(message);
      // need to handle case when another gibs sam volunteer logs in
    }

    if (message.type === 'JOIN' && message.sender !== this.currentUsername) {
      const newChatUser: ChatUser = <ChatUser>{
        username: message.sender,
        unreadMessageCount: 0,
      };
      this.activeUsersSubject.next(newChatUser);
    }

    if (message.type === 'LEAVE') {
      this.disconnectedUsersSubject.next(message.sender);
    }
  }

  private setConnected(connected: boolean) {
    return connected ? console.log('Connected!') : console.log('Disconnected!');
  }

  clearLocalStorage() {
    localStorage.removeItem('activeChat');
  }
}

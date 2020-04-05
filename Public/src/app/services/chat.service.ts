import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject, Observable } from 'rxjs';

import { ChatMessage } from '../models/chat.message';
import { environment } from './../../environments/environment';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

import { AlertService } from './alert.service';
import { AuthenticationService } from './authentication.service';
import { ApiResponse } from '../models/api.response';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private chatUrl: string = environment.chatUrl;
  private apiUrl: string = environment.apiUrl;

  gibSamsUsername = 'Sam';

  private chatMessagesSubject = new Subject<any>();
  private activeVolunteerSubject = new Subject<any>();
  private disconnectedVolunteerSubject = new Subject<any>();

  stompClient: any;

  messages: ChatMessage[] = [];

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
  ) {}

  public async connect() {
    const socket = new SockJS(this.chatUrl);
    this.stompClient = Stomp.over(socket);
    const _this = this;
    _this.stompClient.connect(
      {},
      (fun: any) => {
        this.stompClient.subscribe('/chat/' + this.username, function(
          msg: any,
        ) {
          _this.handleMessage(JSON.parse(msg.body));
        });

        const message: ChatMessage = <ChatMessage>{
          sender: this.username,
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

        this.setConnected(true);
      },
      (error: string) => {
        this.alertService.error(
          'Unable to connect to WebSocket server: ' + error,
        );
      },
    );
  }

  get username() {
    return this.authenticationService.getUsername();
  }

  public getVolunteer(): Observable<any> {
    return this.activeVolunteerSubject.asObservable();
  }

  public getDisconnectedVolunteer(): Observable<any> {
    return this.disconnectedVolunteerSubject.asObservable();
  }

  public getChatMessages(): Observable<ChatMessage[]> {
    return this.chatMessagesSubject.asObservable();
  }

  public getConversation() {
    return this.http.get<ChatMessage[]>(
      `${this.apiUrl}/chat/conversation/${this.username}`,
    );
  }

  public isVolunteerActive() {
    return this.http.get<ApiResponse>(
      `${this.apiUrl}/chat/isVolunteerActive/${this.username}`,
    );
  }

  public disconnect() {
    const message: ChatMessage = <ChatMessage>{
      sender: this.username,
      recipient: this.gibSamsUsername,
      type: 'LEAVE',
    };

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
    localStorage.removeItem('conversationInProgress');
  }

  public send(message: ChatMessage) {
    try {
      this.stompClient.send('/app/send/message', {}, JSON.stringify(message));
    } catch (error) {
      this.alertService.error(error);
    }
  }

  private handleMessage(message: any) {
    if (
      message.type === 'CHAT' &&
      (message.sender === this.username || message.recipient === this.username)
    ) {
      this.chatMessagesSubject.next(message);
    }

    if (message.type === 'JOIN' && message.sender === this.gibSamsUsername) {
      this.alertService.success('Sam Connected!');
      this.activeVolunteerSubject.next(message.sender);
    }

    if (message.type === 'LEAVE' && message.sender === this.gibSamsUsername) {
      this.alertService.error('Sam Disconnected!');
      this.disconnectedVolunteerSubject.next(message.sender);
    }
  }

  private setConnected(connected: boolean) {
    const value = connected ? 'Connected!' : 'Disconnected!';
    console.log(value);
  }
}

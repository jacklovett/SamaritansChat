import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject, Observable } from 'rxjs';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

import { AlertService } from './alert.service';
import { AuthenticationService } from './authentication.service';

import { environment } from 'src/environments/environment';

import { ApiResponse } from '../models/api.response';
import { Notification, PartialNotification } from 'src/app/models/notification';
import { ConversationRequest } from '../models/conversation.request';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private chatUrl: string = environment.chatUrl;
  private apiUrl: string = environment.apiUrl;

  stompClient: any;

  private notificationsSubject = new Subject<Notification>();
  private isReloadRequiredSubject = new Subject<boolean>();

  constructor(
    private http: HttpClient,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
  ) {}

  public get currentUser() {
    return this.authenticationService.getUserDetailsFromJWT();
  }

  public connect() {
    const socket = new SockJS(this.chatUrl);
    this.stompClient = Stomp.over(socket);
    const _this = this;
    _this.stompClient.connect(
      {},
      () => {
        this.stompClient.subscribe('/chat/notifications', function(msg: any) {
          _this.handleNotification(JSON.parse(msg.body));
        });

        this.stompClient.subscribe(
          '/chat/notifications/' + this.currentUser.username,
          function(msg: any) {
            _this.handleNotification(JSON.parse(msg.body));
          },
        );

        this.setConnected(true);
      },
      (error: string) => {
        this.alertService.error(
          `Unable to connect to WebSocket server: ${error}`,
        );
      },
    );
  }

  public getNotifications(): Observable<Notification> {
    return this.notificationsSubject.asObservable();
  }

  public isReloadRequired(): Observable<boolean> {
    return this.isReloadRequiredSubject.asObservable();
  }

  private handleNotification(notification: Notification) {
    this.notificationsSubject.next(notification);
  }

  get() {
    return this.http.get<Notification[]>(
      `${this.apiUrl}/notifications/${this.currentUser.userId}`,
    );
  }

  async update(notification: PartialNotification) {
    try {
      await this.http
        .put<any>(`${this.apiUrl}/notifications/edit`, notification)
        .toPromise();
      this.isReloadRequiredSubject.next(true);
    } catch (error) {
      console.log(error);
    }
  }

  async delete(id: number) {
    try {
      const response = await this.http
        .delete<any>(`${this.apiUrl}/notifications/delete/${id}`)
        .toPromise();
      if (response.success) {
        this.isReloadRequiredSubject.next(true);
      }
    } catch (error) {
      this.alertService.error(error);
    }
  }

  public startConversation(conversationRequest: ConversationRequest) {
    conversationRequest.gibSamsUser = this.currentUser.username;
    return this.http.post<ApiResponse>(
      `${this.apiUrl}/chat/startConversation`,
      conversationRequest,
    );
  }

  private setConnected(connected: boolean) {
    return connected ? console.log('Connected!') : console.log('Disconnected!');
  }
}

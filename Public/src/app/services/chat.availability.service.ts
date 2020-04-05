import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

import { AlertService } from './alert.service';
import { environment } from 'src/environments/environment';
import { ChatAvailabilityResponse } from '../models/chat.availability.response';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ChatAvailabilityService {
  private stompClient: any;

  private apiUrl: string = environment.apiUrl;
  private chatUrl: string = environment.chatUrl;

  private chatAvailableSubject = new Subject<any>();

  constructor(private http: HttpClient, private alertService: AlertService) {}

  public async connect() {
    const socket = new SockJS(this.chatUrl);
    this.stompClient = Stomp.over(socket);
    const _this = this;
    _this.stompClient.connect(
      {},
      () => {
        this.stompClient.subscribe('/chat/availability', function(
          response: any,
        ) {
          _this.chatAvailableSubject.next(JSON.parse(response.body));
        });
      },
      (error: string) => {
        this.alertService.error(
          `Unable to connect to WebSocket server: ${error}`,
        );
      },
    );
  }

  public getChatAvailability(): Observable<ChatAvailabilityResponse> {
    return this.chatAvailableSubject.asObservable();
  }

  public isChatAvailable() {
    return this.http.get<ChatAvailabilityResponse>(
      `${this.apiUrl}/chat/availability`,
    );
  }
}

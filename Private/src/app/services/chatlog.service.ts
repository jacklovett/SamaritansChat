import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { ApiResponse } from '../models/api.response';
import { ChatLog } from '../models/chat.log';
import { Transcript } from '../models/transcript';

import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ChatLogService {
  apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  get() {
    return this.http.get<ChatLog[]>(`${this.apiUrl}/chatlogs`);
  }

  save(chatLog: ChatLog) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/chatlogs/save`, chatLog);
  }

  getTranscriptById(id: number) {
    return this.http.get<Transcript>(
      `${this.apiUrl}/chatlogs/transcript/${id}`,
    );
  }
}

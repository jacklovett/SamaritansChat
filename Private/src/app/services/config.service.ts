import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { ChatConfig } from '../models/chat.config';
import { ApiResponse } from '../models/api.response';

import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  get() {
    return this.http.get<ChatConfig>(`${this.apiUrl}/config`);
  }

  update(chatConfig: ChatConfig) {
    return this.http.put<ApiResponse>(`${this.apiUrl}/config/edit`, chatConfig);
  }
}

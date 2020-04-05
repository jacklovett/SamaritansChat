import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { environment } from './../../environments/environment';

import { User } from './../models/user';
import { ApiResponse } from './../models/api.response';


@Injectable({ providedIn: 'root' })
export class UserService {

  apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) { }

  register(user: User) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/auth/chat/register`, user);
  }

  generateUsername(): any {
    return this.http.get<ApiResponse>(`${this.apiUrl}/auth/chat/generateUsername`);
  }
}

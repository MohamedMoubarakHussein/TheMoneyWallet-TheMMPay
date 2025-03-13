import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth/signup';

  constructor(private http: HttpClient) {}

  signup(payload: any) {
    return this.http.post(this.apiUrl, payload);
  }
}
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth/signup';

  constructor(private http: HttpClient) {}

  signup(payload: any) {
    return this.http.post(this.apiUrl, payload,
      {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
        observe: 'response', // Get the full response
        responseType: 'text' // Treat response as text to avoid JSON parsing errors
      }
    );
  }

  
  

}
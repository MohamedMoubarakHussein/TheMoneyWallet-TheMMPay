import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/';

  constructor(private http: HttpClient) {}

  signup(payload: any) {
    return this.http.post(this.apiUrl+'auth/signup', payload,
      {
        withCredentials: true,
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
        observe: 'response',
        responseType : 'text'
      }
    );
  }

  signin(email: string, password: string) {
    return this.http.post(this.apiUrl+'auth/signin', 
      { email, password },
      { 
        withCredentials: true, 
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
         observe: 'response',
         responseType : 'text'
      }
    );
  }
  

}
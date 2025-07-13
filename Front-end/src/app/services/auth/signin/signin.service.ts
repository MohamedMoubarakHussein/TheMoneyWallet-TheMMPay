import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { switchMap, map, catchError, retry } from 'rxjs/operators';
import { User, UnifiedResponse } from '../../../entity/UnifiedResponse';
import { TokenService } from '../token/token.service';


@Injectable({
  providedIn: 'root'
})
export class SigninService {
  private readonly apiUrl = 'http://localhost:8080/';
  private readonly dashboardUrl = 'http://localhost:8095/dashboard/user';

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
  ) {}

  signin(email: string, password: string): Observable<User> {
    return this.http.post<UnifiedResponse>(
      `${this.apiUrl}auth/signin`, 
      { email, password }, 
      {
        withCredentials: true,
        headers: new HttpHeaders({ 'Content-Type': 'application/json' })
      }
    ).pipe(
     
      switchMap(response => {
        const token = response.data?.['auth']?.['token'];
        if (!token) {
          return throwError(() => new Error('Authentication token missing from server response'));
        }
        
        return this.extractUserFromResponse(token).pipe(
          map(user => {
            // Update session with user data and token
            // Token expires in 1 hour (3600 seconds)
            //TODO  how to make it extract the expores from the token itself
            this.tokenService.updateSession(user, token, 3600);
            return user;
          })
        );
      }),
      
      retry(1),
      catchError(err => {
        console.error('Signin failed:', err);
        return throwError(() => new Error('Signin failed: ' + err.message));
      })
    );
  }

 
  private extractUserFromResponse(token: string): Observable<User> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    
    return this.http.get<UnifiedResponse>(this.dashboardUrl, { headers }).pipe(
      map((unifiedResponse: UnifiedResponse) => {
        const user = JSON.parse(unifiedResponse.data['DATA']['dashboard']);
        return user;
      }),
      catchError(error => {
        let message = 'Failed to retrieve user profile';
                try {
          message = JSON.parse(error.error.data['ERROR']);
        } catch (_) {
        }
        
        console.error('Profile extraction failed:', error);
        return throwError(() => new Error(message));
      })
    );
  }
}
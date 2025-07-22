import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import { User, SignupData, UnifiedResponse } from '../../../entity/UnifiedResponse';
import { TokenService } from '../token/token.service';


@Injectable({
  providedIn: 'root'
})
export class SignupService {
  private readonly apiUrl = 'http://localhost:8080/';
  private readonly dashboardUrl = 'http://localhost:8095/dashboard/user';

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
  ) {}

  signup(signupData: SignupData): Observable<User> {
    return this.http.post<UnifiedResponse>(
      `${this.apiUrl}auth/signup`, 
      signupData, 
      {
        withCredentials: true, 
        observe: 'response', 
        headers: new HttpHeaders({ 'Content-Type': 'application/json' })
      }
    ).pipe(
      switchMap(response => {
        const body = response.body;
    
        if (!body || body.haveError) {
          return throwError(() => new Error('Signup failed - server returned error'));
        }
        const token = response.headers.get('Authorization');
        if (!token) {
          return throwError(() => new Error('Authorization token missing from signup response'));
        }
        
        return this.extractUserFromResponse(token).pipe(
          map(user => {
            // Update session with user data and token
            // Set token expiration to 1 hour (3600 seconds)
            //TODO explained in signin 
            this.tokenService.updateSession(user, token, 3600);
            return user;
          })
        );
      }),
      catchError(err => {
        console.error('Signup failed:', err);
        return throwError(() => new Error('Signup failed: ' + err.message));
      })
    );
  }

//TODO this method is repated in signup and signin how to make it once
  private extractUserFromResponse(token: string): Observable<User> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    
    return this.http.get<UnifiedResponse>(this.dashboardUrl, { headers }).pipe(
      map((unifiedResponse: UnifiedResponse) => {
        
        const user = JSON.parse(unifiedResponse.data['DATA']['dashboard']);
        console.log("poco   "+JSON.stringify(user));
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


  // STEP 1: New method to process OAuth2 token received from callback
  processOAuth2Token(token: string): Observable<User> {
    return this.extractUserFromResponse(token).pipe(
      map(user => {
        // Set token expiration to 1 hour (3600 seconds) - same as regular signup
        this.tokenService.updateSession(user, token, 3600);
        
        // Add provider information to user object for identification
        //user.provider = 'google';
        
        return user;
      }),
      catchError(err => {
        console.error('OAuth2 token processing failed:', err);
        return throwError(() => new Error('OAuth2 authentication failed: ' + err.message));
      })
    );
  }

  
}
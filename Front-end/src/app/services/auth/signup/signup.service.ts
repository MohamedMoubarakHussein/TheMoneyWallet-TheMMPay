import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from 'express';
import { BehaviorSubject, Observable, switchMap, throwError, map, catchError } from 'rxjs';
import { User, SignupData, UnifiedResponse } from '../../../entity/UnifiedResponse';

@Injectable({
  providedIn: 'root'
})
export class SignupService {
  private apiUrl = 'http://localhost:8080/';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {

  }




  signup(signupData: SignupData): Observable<User> {
    return this.http.post<UnifiedResponse>(`${this.apiUrl}auth/signup`, signupData, {
      withCredentials: true,
      observe: 'response',
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }).pipe(
    switchMap(response => {
      const body = response.body;
      if (!body || body.haveError) {
      return throwError(() => new Error('Signup failed'));
      }

      const token = response.headers.get('Authorization');
      if (!token) {
      return throwError(() => new Error('Token missing'));
      }

      return this.extractUserFromResponse(token).pipe(
      map(user => {
      // TODO edit the exipration
      this.updateSession(user, token , 606060606);
      return user;
          })
        );
      })
    );
  }


  private extractUserFromResponse(token: string): Observable<any> {

    const headers = new HttpHeaders({
     'Authorization': `Bearer ${token}`,
    });
    return this.http.get<UnifiedResponse>('http://localhost:8095/dashboard/user', { headers }).pipe(
    map((unifiedResponse: UnifiedResponse) => {
    const user = JSON.parse(unifiedResponse.data['DATA']['dashboard']);
    this.currentUserSubject.next(user); 
    return user;
    }),
    catchError(error => {
    let message = 'Server getting the profile error';
    try {
    message = JSON.parse(error.error.data['ERROR']);
    } catch (_) {}
    return throwError(() => new Error(message));
    })
    );
  }

  updateSession(user: User, token: string , tokenExpiretion: number) {
  localStorage.setItem('accessToken', token);
  this.currentUserSubject.next(user);
  this.isAuthenticatedSubject.next(true);
  // this.scheduleTokenRefresh(tokenExpiretion);
  }
}

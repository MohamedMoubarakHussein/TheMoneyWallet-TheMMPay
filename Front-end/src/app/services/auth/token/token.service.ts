import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { timer, Observable, switchMap, throwError, tap, finalize, catchError } from 'rxjs';
import { UnifiedResponse, User } from '../../../entity/UnifiedResponse';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  private isRefreshing = false;
  constructor() { }


   private scheduleTokenRefresh(expiresIn?: number) {
      const refreshTime = (expiresIn ? expiresIn - 300 : 3300) * 1000;
      timer(refreshTime).subscribe(() => {
        if (this.isAuthenticated()) this.attemptTokenRefresh().subscribe();
      });
    }
  
    isAuthenticated(): boolean {
      return !!localStorage.getItem('accessToken') && !!this.currentUserSubject.value;
    }
  
  
    private attemptTokenRefresh(): Observable<any> {
      if (this.isRefreshing) {
        return this.refreshTokenSubject.pipe(
          switchMap(newToken => newToken ? this.validateToken(newToken) : throwError(() => new Error('Token refresh failed')))
        );
      }
  
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);
  
      return this.http.post<UnifiedResponse>(`${this.apiUrl}auth/refresh`, {}, {
        withCredentials: true,
        headers: new HttpHeaders({ 'Content-Type': 'application/json' })
      }).pipe(
        tap(response => {
          const token = response.data['auth']?.['token'];
          if (!token) throw new Error('No token after refresh');
          const user = this.extractUserFromResponse(token);
         // this.updateSession(user, token);
          this.refreshTokenSubject.next(token);
        }),
        finalize(() => { this.isRefreshing = false; }),
        catchError(err => {
          this.logout();
          return throwError(() => err);
        })
      );
    }
  
    private checkExistingToken() {
      const token = localStorage.getItem('jwt_token');
      if (token) {
        this.validateToken(token).pipe(
          catchError(() => this.attemptTokenRefresh())
        ).subscribe();
      }
    }
  
    private validateToken(token: string): Observable<any> {
      return this.http.get<UnifiedResponse>(`${this.apiUrl}auth/validate`, {
        headers: new HttpHeaders({
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }),
        withCredentials: true
      }).pipe(
        tap(response => {
         // const user = this.extractUserFromResponse();
         // this.updateSession(user, token);
        })
      );
    }

      updateSession(user: User, token: string , tokenExpiretion: number) {
    localStorage.setItem('accessToken', token);
    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
    this.scheduleTokenRefresh(tokenExpiretion);
  }
  
}

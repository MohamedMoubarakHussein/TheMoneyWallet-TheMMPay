import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError, timer } from 'rxjs';
import { switchMap, tap, finalize, catchError } from 'rxjs/operators';
import { User, UnifiedResponse } from '../../../entity/UnifiedResponse';
import { AuthStateService } from '../state/AuthState.service';


@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly apiUrl = 'http://localhost:8080/';
  
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);
  private isRefreshing = false;

  private refreshTimer: any = null;

  constructor(
    private http: HttpClient,
    private authStateService: AuthStateService
  ) {}


  updateSession(user: User, token: string, tokenExpirationSeconds: number): void {
   
    localStorage.setItem('accessToken', token);
    
  
    this.authStateService.setAuthenticatedUser(user);
    
   
    this.scheduleTokenRefresh(tokenExpirationSeconds);
  }

  
  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

 
  clearToken(): void {
    localStorage.removeItem('accessToken');
  }

  
  isAuthenticated(): boolean {
    return !!this.getToken() && this.authStateService.hasAuthenticatedUser();
  }

 
  validateToken(token: string): Observable<any> {
    return this.http.get<UnifiedResponse>(`${this.apiUrl}auth/validate`, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }),
      withCredentials: true
    }).pipe(
      tap(response => {
       
        console.log('Token validated successfully');
      }),
      catchError(error => {
        console.error('Token validation failed:', error);
        return throwError(() => error);
      })
    );
  }


  private scheduleTokenRefresh(expiresInSeconds: number): void {
  
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
    }
    
    
    const refreshTimeMs = (expiresInSeconds - 300) * 1000;
    
    if (refreshTimeMs > 0) {
      this.refreshTimer = timer(refreshTimeMs).subscribe(() => {
        if (this.isAuthenticated()) {
          this.attemptTokenRefresh().subscribe({
            error: () => {
              console.error('Automatic token refresh failed');
            }
          });
        }
      });
    }
  }

 
  attemptTokenRefresh(): Observable<any> {

    if (this.isRefreshing) {
      return this.refreshTokenSubject.pipe(
        switchMap(newToken => 
          newToken ? 
            this.validateToken(newToken) : 
            throwError(() => new Error('Token refresh failed'))
        )
      );
    }

    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);

    return this.http.post<UnifiedResponse>(`${this.apiUrl}auth/refresh`, {}, {
      withCredentials: true, // Include cookies for session-based refresh
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }).pipe(
      tap(response => {
        const token = response.data['auth']?.['token'];
        if (!token) {
          throw new Error('No token received from refresh endpoint');
        }
        
        localStorage.setItem('accessToken', token);
        
        this.refreshTokenSubject.next(token);
      }),
      finalize(() => {
        this.isRefreshing = false;
      }),
      catchError(err => {
        console.error('Token refresh failed:', err);
        return throwError(() => err);
      })
    );
  }


  cancelTokenRefresh(): void {
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
      this.refreshTimer = null;
    }
    
    this.isRefreshing = false;
    this.refreshTokenSubject.next(null);
  }

  
  initializeTokenValidation(): void {
    const token = this.getToken();
    if (token) {
      this.validateToken(token).pipe(
        catchError(() => {
          return this.attemptTokenRefresh();
        })
      ).subscribe({
        next: () => {
          this.authStateService.setAuthenticationStatus(true);
        },
        error: () => {
          this.clearToken();
          this.authStateService.setAuthenticationStatus(false);
        }
      });
    }
  }
}
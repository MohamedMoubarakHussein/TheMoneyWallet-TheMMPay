import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError, timer } from 'rxjs';
import { switchMap, tap, finalize, catchError, map } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { User, UnifiedResponse } from '../../../entity/UnifiedResponse';
import { AuthStateService } from '../state/AuthState.service';


@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly apiUrl = 'http://localhost:8080/';
  
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);
  private isRefreshing = false;

  private refreshTimer: ReturnType<typeof setTimeout> | null = null;
  private readonly dashboardUrl = 'http://localhost:8095/dashboard/user';

  constructor(
    private http: HttpClient,
    private authStateService: AuthStateService,
    @Inject(PLATFORM_ID) private platformId: object
  ) {}

  private extractUserFromToken(token: string): Observable<User> {
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
        let message = 'Failed to retrieve user profile from token';
        try {
          message = JSON.parse(error.error.data['ERROR']);
        } catch (_) {
        }
        console.error('Profile extraction from token failed:', error);
        return throwError(() => new Error(message));
      })
    );
  }

  updateSession(user: User, token: string, tokenExpirationSeconds: number): void {
    // Only access localStorage in browser environment
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('accessToken', token);
    }
    
    this.authStateService.setAuthenticatedUser(user);
    
    this.scheduleTokenRefresh(tokenExpirationSeconds);
  }

  
  getToken(): string | null {
    // Only access localStorage in browser environment
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }
    return localStorage.getItem('accessToken');
  }

 
  clearToken(): void {
    // Only access localStorage in browser environment
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('accessToken');
    }
  }

  
  isAuthenticated(): boolean {
    return !!this.getToken() && this.authStateService.hasAuthenticatedUser();
  }

 
  validateToken(token: string): Observable<User> {
    return this.http.get<UnifiedResponse>(`${this.apiUrl}auth/validate`, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }),
      withCredentials: true
    }).pipe(
      switchMap(response => {
        console.log('Token validated successfully', response);
        return this.extractUserFromToken(token);
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

 
  attemptTokenRefresh(): Observable<User> {

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
        
                // Only access localStorage in browser environment
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('accessToken', token);
        }
        
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
    console.log('TokenService: Initializing token validation');
    const token = this.getToken();
    if (token) {
      console.log('TokenService: Found token, validating...');
      this.validateToken(token).pipe(
        catchError(() => {
          console.log('TokenService: Token validation failed, attempting refresh...');
          return this.attemptTokenRefresh();
        })
      ).subscribe({
        next: (user: User) => {
          console.log('TokenService: Token validated/refreshed successfully, setting auth status true and user', user);
          this.authStateService.setAuthenticatedUser(user);
        },
        error: () => {
          console.log('TokenService: Token validation/refresh failed, clearing token and setting auth status false');
          this.clearToken();
          this.authStateService.setAuthenticationStatus(false);
        }
      });
    } else {
      console.log('TokenService: No token found, setting auth status false');
      this.authStateService.setAuthenticationStatus(false);
    }
  }
}
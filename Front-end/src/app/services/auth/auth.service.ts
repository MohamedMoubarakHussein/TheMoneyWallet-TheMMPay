import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

declare var google: any;

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/';
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private isGoogleInitialized = false;

  constructor(private http: HttpClient) {
    this.loadGoogleScript();
    this.checkExistingToken();
  }

  // Load Google OAuth script
  private loadGoogleScript() {
    if (typeof google !== 'undefined') {
      this.isGoogleInitialized = true;
      return;
    }
    
    const script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = () => {
      this.isGoogleInitialized = true;
      this.initializeGoogleSignIn();
    };
    document.head.appendChild(script);
  }

  // Check for existing JWT token on service initialization
  private checkExistingToken() {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      this.validateToken(token).subscribe({
        next: (user) => this.currentUserSubject.next(user),
        error: () => this.logout()
      });
    }
  }

  // Validate existing JWT token
  private validateToken(token: string): Observable<any> {
    return this.http.get(this.apiUrl + 'auth/validate', {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      })
    });
  }

  // Initialize Google Sign-In
  initializeGoogleSignIn() {
    if (!this.isGoogleInitialized || typeof google === 'undefined') {
      setTimeout(() => this.initializeGoogleSignIn(), 100);
      return;
    }

    google.accounts.id.initialize({
      client_id: 'YOUR_GOOGLE_CLIENT_ID', // Replace with your actual Google Client ID
      callback: (response: any) => this.handleGoogleResponse(response),
      auto_select: false,
      cancel_on_tap_outside: true
    });
  }

  // Handle Google OAuth response
  private handleGoogleResponse(response: any) {
    const googleToken = response.credential;
    
    this.http.post(this.apiUrl + 'auth/google', 
      { token: googleToken },
      {
        withCredentials: true,
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
        observe: 'response'
      }
    ).subscribe({
      next: (result: any) => {
        const token = result.body?.token || result.body?.accessToken;
        const user = result.body?.user;
        
        if (token) {
          localStorage.setItem('jwt_token', token);
          this.currentUserSubject.next(user);
        }
      },
      error: (error) => {
        console.error('Google authentication failed', error);
      }
    });
  }

  // Traditional signup
  signup(payload: any) {
    console.log('Payload:', payload);
    
    return this.http.post(this.apiUrl + 'auth/signup', payload, {
      withCredentials: true,
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      observe: 'response'
    }).pipe(
      tap((response: any) => {
        const token = response.body?.token || response.body?.accessToken;
        const user = response.body?.user;
        
        if (token) {
          localStorage.setItem('jwt_token', token);
          this.currentUserSubject.next(user);
        }
      })
    );
  }

  // Traditional signin
  signin(email: string, password: string) {
    return this.http.post(this.apiUrl + 'auth/signin',
      { email, password },
      {
        withCredentials: true,
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
        observe: 'response'
      }
    ).pipe(
      tap((response: any) => {
        const token = response.body?.token || response.body?.accessToken;
        const user = response.body?.user;
        
        if (token) {
          localStorage.setItem('jwt_token', token);
          this.currentUserSubject.next(user);
        }
      })
    );
  }

  // Google Sign-In method
  signInWithGoogle() {
    if (!this.isGoogleInitialized) {
      console.error('Google Sign-In not initialized yet');
      return;
    }
    
    google.accounts.id.prompt((notification: any) => {
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        // Fallback to popup if prompt is not displayed
        google.accounts.id.renderButton(
          document.getElementById('google-signin-button'),
          { 
            theme: 'outline', 
            size: 'large',
            width: '100%'
          }
        );
      }
    });
  }

  // Alternative Google Sign-In using popup
  signInWithGooglePopup() {
    if (!this.isGoogleInitialized) {
      console.error('Google Sign-In not initialized yet');
      return;
    }

    // Create a temporary button element for Google to render
    const tempDiv = document.createElement('div');
    tempDiv.style.display = 'none';
    document.body.appendChild(tempDiv);

    google.accounts.id.renderButton(tempDiv, {
      theme: 'filled_blue',
      size: 'large',
      type: 'standard'
    });

    // Simulate click on the Google button
    setTimeout(() => {
      const googleBtn = tempDiv.querySelector('div[role="button"]') as HTMLElement;
      if (googleBtn) {
        googleBtn.click();
      }
      document.body.removeChild(tempDiv);
    }, 100);
  }

  // Get current user
  getCurrentUser(): Observable<any> {
    return this.currentUser$;
  }

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return !!localStorage.getItem('jwt_token') && !!this.currentUserSubject.value;
  }

  // Get JWT token
  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  // Logout
  logout() {
    localStorage.removeItem('jwt_token');
    this.currentUserSubject.next(null);
    
    // Sign out from Google
    if (this.isGoogleInitialized && typeof google !== 'undefined') {
      google.accounts.id.disableAutoSelect();
    }
  }

  // Refresh token (if your backend supports it)
  refreshToken(): Observable<any> {
    return this.http.post(this.apiUrl + 'auth/refresh', {}, {
      withCredentials: true,
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      observe: 'response'
    }).pipe(
      tap((response: any) => {
        const token = response.body?.token || response.body?.accessToken;
        if (token) {
          localStorage.setItem('jwt_token', token);
        }
      })
    );
  }
}
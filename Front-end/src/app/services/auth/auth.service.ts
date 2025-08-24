import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User, SignupData } from '../../entity/UnifiedResponse';
import { SigninService } from './signin/signin.service';
import { SignupService } from './signup/signup.service';
import { LogoutService } from './logout/logout.service';
import { TokenService } from './token/token.service';
import { VerificationService } from './verification/verification.service';
import { AuthStateService } from './state/AuthState.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private signinService: SigninService,
    private signupService: SignupService,
    private logoutService: LogoutService,
    private tokenService: TokenService,
    private verificationService: VerificationService,
    private authStateService: AuthStateService
  ) {
    this.initializeAuthSystem();
  }

  /**
   * Signs in a user.
   * @param email The user's email.
   * @param password The user's password.
   * @returns An observable of the user.
   */
  signin(email: string, password: string): Observable<User> {
    return this.signinService.signin(email, password).pipe(
      catchError(() => {
        // Handle error
        return throwError(() => new Error('Signin failed'));
      })
    );
  }

  /**
   * Signs up a new user.
   * @param signupData The user's signup data.
   * @returns An observable of the user.
   */
  signup(signupData: SignupData): Observable<User> {
    return this.signupService.signup(signupData).pipe(
      catchError(() => {
        // Handle error
        return throwError(() => new Error('Signup failed'));
      })
    );
  }

  processOAuth2Token(token: string): Observable<User> {
    return this.signupService.processOAuth2Token(token);
  }

  logout(): void {
    this.logoutService.logout();
  }

  get currentUser$(): Observable<User | null> {
    return this.authStateService.currentUser$;
  }

  get isAuthenticated$(): Observable<boolean> {
    return this.authStateService.isAuthenticated$;
  }

  isAuthenticated(): boolean {
    return this.tokenService.isAuthenticated();
  }

  getCurrentUser(): User | null {
    return this.authStateService.getCurrentUser();
  }

  getToken(): string | null {
    return this.tokenService.getToken();
  }

  verifyCode(code: string, token: string): Observable<unknown> {
    return this.verificationService.verifyCode(code, token);
  }

  resendVerificationCode(email: string): Observable<unknown> {
    return this.verificationService.resendVerificationCode(email);
  }

  refreshToken(): Observable<User> {
    return this.tokenService.attemptTokenRefresh();
  }

  private initializeAuthSystem(): void {
    console.log('AuthService: Initializing auth system');
    this.tokenService.initializeTokenValidation();
  }
}

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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

  
  signin(email: string, password: string): Observable<User> {
    return this.signinService.signin(email, password);
  }

  
  signup(signupData: SignupData): Observable<User> {
    return this.signupService.signup(signupData);
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
//TODO
  /*
  hasRole(role: string): boolean {
    return this.authStateService.hasRole(role);
  }


  hasAnyRole(roles: string[]): boolean {
    return this.authStateService.hasAnyRole(roles);
  }


  hasAllRoles(roles: string[]): boolean {
    return this.authStateService.hasAllRoles(roles);
  }*/


  getToken(): string | null {
    return this.tokenService.getToken();
  }

 
  validateCurrentToken(): Observable<any> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No token available for validation');
    }
    return this.tokenService.validateToken(token);
  }

  
  refreshToken(): Observable<any> {
    return this.tokenService.attemptTokenRefresh();
  }

  
  verifyCode(code: string, token: string): Observable<any> {
    return this.verificationService.verifyCode(code, token);
  }

  
  resendVerificationCode(email: string): Observable<any> {
    return this.verificationService.resendVerificationCode(email);
  }

  
  validateVerificationCodeFormat(code: string): boolean {
    return this.verificationService.validateCodeFormat(code);
  }

  
  formatVerificationCode(code: string): string {
    return this.verificationService.formatCodeForDisplay(code);
  }

  
  private initializeAuthSystem(): void {
    this.tokenService.initializeTokenValidation();
  }

  
  reinitializeAuthSystem(): void {
    this.initializeAuthSystem();
  }

 
  updateUserProfile(user: User): void {
    this.authStateService.updateCurrentUser(user);
  }

  
  getUserProperty<K extends keyof User>(property: K): User[K] | undefined {
    return this.authStateService.getUserProperty(property);
  }

 
  isSystemInitialized(): boolean {
    return !!(
      this.signinService &&
      this.signupService &&
      this.logoutService &&
      this.tokenService &&
      this.verificationService &&
      this.authStateService
    );
  }
}
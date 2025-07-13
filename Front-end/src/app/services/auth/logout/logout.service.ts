import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from '../token/token.service';
import { AuthStateService } from '../state/AuthState.service';

@Injectable({
  providedIn: 'root'
})
export class LogoutService {
  constructor(
    private router: Router,
    private tokenService: TokenService,
    private authStateService: AuthStateService
  ) {}
  logout(): void {
    this.tokenService.clearToken();
    this.authStateService.resetAuthenticationState();
    this.tokenService.cancelTokenRefresh();
    this.router.navigate(['/signin']);
  }
}
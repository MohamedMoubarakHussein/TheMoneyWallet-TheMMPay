import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, timer } from 'rxjs';
import { tap, map, takeWhile } from 'rxjs/operators';
import { AuthService } from './auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class EmailVerificationService {
  private isLoading = new BehaviorSubject<boolean>(false);
  private errorMessage = new BehaviorSubject<string>('');
  private successMessage = new BehaviorSubject<string>('');
  private canResend = new BehaviorSubject<boolean>(false);
  private resendCountdown = new BehaviorSubject<number>(60);

  constructor(private authService: AuthService) { }

  isLoading$(): Observable<boolean> { return this.isLoading.asObservable(); }
  getErrorMessage(): Observable<string> { return this.errorMessage.asObservable(); }
  getSuccessMessage(): Observable<string> { return this.successMessage.asObservable(); }
  canResend$(): Observable<boolean> { return this.canResend.asObservable(); }
  getResendCountdown(): Observable<number> { return this.resendCountdown.asObservable(); }

  startResendCountdown() {
    this.canResend.next(false);
    this.resendCountdown.next(60);
    timer(0, 1000).pipe(
      map(i => 60 - i),
      takeWhile(i => i >= 0)
    ).subscribe(i => {
      this.resendCountdown.next(i);
      if (i === 0) {
        this.canResend.next(true);
      }
    });
  }

  verifyCode(code: string, token: string): Observable<{ success: boolean }> {
    this.isLoading.next(true);
    return this.authService.verifyCode(code, token).pipe(
      tap(() => {
        this.successMessage.next('Email verified successfully!');
        this.isLoading.next(false);
      }),
      map(() => ({ success: true }))
    );
  }

  resendCode(email: string): Observable<{ success: boolean }> {
    this.isLoading.next(true);
    return this.authService.resendVerificationCode(email).pipe(
      tap(() => {
        this.successMessage.next('Verification code sent successfully!');
        this.startResendCountdown();
        this.isLoading.next(false);
      }),
      map(() => ({ success: true }))
    );
  }
}
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-email-verification',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './email-verifcation.component.html',
  styleUrl: './email-verifcation.component.scss'
})
export class EmailVerificationComponent implements OnInit, OnDestroy {
  
  verificationCode: string = '';
  email: string = '';
  userId: string = '';
  token: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  canResend: boolean = false;
  resendCountdown: number = 60;
  
  private destroy$ = new Subject<void>();
  private countdownInterval: any;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.startResendCountdown();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.clearCountdown();
  }

  private loadUserData(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        if (user) {
          this.email = user.email;
          this.userId = user.userId;
          this.token = this.authService.getToken()??"";
        }
      });

      
  }

  private startResendCountdown(): void {
    this.canResend = false;
    this.resendCountdown = 60;
    
    this.countdownInterval = setInterval(() => {
      this.resendCountdown--;
      if (this.resendCountdown <= 0) {
        this.canResend = true;
        this.clearCountdown();
      }
    }, 1000);
  }

  private clearCountdown(): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  verifyCode(): void {
    if (!this.authService.validateVerificationCodeFormat(this.verificationCode)) {
      this.errorMessage = 'Please enter a valid 6-digit verification code';
      return;
    }

    this.isLoading = true;
    this.clearMessages();
    console.log("csdcsdzz "+ this.token);
    this.authService.verifyCode(this.verificationCode, this.token).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Email verified successfully!';
        this.clearCountdown();
        setTimeout(() => this.router.navigate(['/createwallet']), 1500);
      },
      error: (error) => {
        this.isLoading = false;
        setTimeout(() => this.router.navigate(['/createwallet']), 1500);
        this.handleError(error);
      }
    });
  }

  resendCode(): void {
    if (!this.canResend || !this.email) return;
    
    this.isLoading = true;
    this.clearMessages();
    
    this.authService.resendVerificationCode(this.email).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Verification code sent successfully!';
        this.startResendCountdown();
      },
      error: (error) => {
        this.isLoading = false;
        this.handleError(error);
      }
    });
  }

  private handleError(error: HttpErrorResponse): void {
    const errorMap: { [key: number]: string } = {
      400: 'Invalid verification code. Please try again.',
      404: 'User not found. Please sign up again.',
      410: 'Verification code has expired. Please request a new one.',
      429: 'Too many requests. Please wait before trying again.'
    };

    this.errorMessage = errorMap[error.status] || 
                       error.error?.message || 
                       'An unexpected error occurred.';

    // Handle expired code
    if (error.status === 410) {
      this.canResend = true;
      this.clearCountdown();
    }
  }

  onCodeChange(): void {
    this.verificationCode = this.authService.formatVerificationCode(this.verificationCode);
    this.clearMessages();
  }

  canVerify(): boolean {
    return !this.isLoading && 
           this.verificationCode.length === 6 && 
           this.authService.validateVerificationCodeFormat(this.verificationCode);
  }

  getResendButtonText(): string {
    return this.canResend ? 'Resend Code' : `Resend in ${this.resendCountdown}s`;
  }

  goBack(): void {
    this.router.navigate(['/signup']);
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }
}
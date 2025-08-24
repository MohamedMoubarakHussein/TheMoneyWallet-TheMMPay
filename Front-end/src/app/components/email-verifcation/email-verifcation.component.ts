import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EmailVerificationService } from '../../services/email-verification.service';
import { AuthService } from '../../services/auth/auth.service';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-email-verification',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './email-verifcation.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class EmailVerificationComponent implements OnInit, OnDestroy {
  isLoading$: Observable<boolean>;
  errorMessage$: Observable<string>;
  successMessage$: Observable<string>;
  canResend$: Observable<boolean>;
  resendCountdown$: Observable<number>;

  verificationCode = '';
  email = '';
  token = '';

  private destroy$ = new Subject<void>();

  constructor(
    private emailVerificationService: EmailVerificationService,
    private authService: AuthService,
    private router: Router
  ) {
    this.isLoading$ = this.emailVerificationService.isLoading$();
    this.errorMessage$ = this.emailVerificationService.getErrorMessage();
    this.successMessage$ = this.emailVerificationService.getSuccessMessage();
    this.canResend$ = this.emailVerificationService.canResend$();
    this.resendCountdown$ = this.emailVerificationService.getResendCountdown();
  }

  ngOnInit(): void {
    this.authService.currentUser$.pipe(takeUntil(this.destroy$)).subscribe(user => {
      if (user) {
        this.email = user.email;
        this.token = this.authService.getToken() ?? "";
      }
    });
    this.emailVerificationService.startResendCountdown();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  verifyCode(): void {
    this.emailVerificationService.verifyCode(this.verificationCode, this.token).subscribe(result => {
      if (result.success) {
        setTimeout(() => this.router.navigate(['/createwallet']), 1500);
      }
    });
  }

  resendCode(): void {
    this.emailVerificationService.resendCode(this.email).subscribe();
  }

  goBack(): void {
    this.router.navigate(['/signup']);
  }
}

import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SignupService } from '../../services/auth/signup/signup.service';
import { VerificationService } from '../../services/auth/verification/verification.service';
import { HttpErrorResponse } from '@angular/common/http';
@Component({
  selector: 'app-email-verifcation',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './email-verifcation.component.html',
  styleUrl: './email-verifcation.component.scss'
})
export class EmailVerifcationComponent implements OnInit, OnDestroy {
  
  verificationCode: string = '';
  email: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  canResend: boolean = false;
  resendCountdown: number = 60;
  serverErrorMessage = '';
  
  private destroy$ = new Subject<void>();
  private countdownInterval: any;

  constructor(
    private signupService: SignupService,
    private router: Router,
    private verificationService:VerificationService
  ) {}

  ngOnInit(): void {
    this.loadUserEmail();
    this.startResendCountdown();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  private loadUserEmail(): void {
    this.signupService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        if (user) {
          this.email = user.email;
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
        clearInterval(this.countdownInterval);
      }
    }, 1000);
  }

 

  verifyCode(): void {
    if (!this.verificationCode || this.verificationCode.length !== 6) {
      this.errorMessage = 'Please enter a valid 6-digit verification code';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    
    // Simulate API call
    //    this.successMessage = 'Email verified successfully!';
    //    this.router.navigate(['/createwallet']);
    //    this.errorMessage = 'Invalid verification code. Please try again.';
 
    //

    this.verificationService.verifyCode("", "").subscribe({
      next: () => {
        this.handleSuccess();
      },
      error: (error) => this.handleError(error)
    });
  }

  resendCode(): void {
    if (!this.canResend) return;
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    setTimeout(() => {
      this.successMessage = 'Verification code sent successfully!';
      this.startResendCountdown();
      this.isLoading = false;
    }, 1000);

     this.verificationService.resendVerificationCode("").subscribe({
      next: () => {
        this.handleSuccess();
      },
      error: (error) => this.handleError(error)
    });


  }

  private handleSuccess(): void {
    this.router.navigate(['/verification']);
  }

   private handleError(error: HttpErrorResponse): void {
  
  
      try {
        const response =  error.error ;
        
        if (error.status === 400) {
          //this.handleValidationErrors(response.data['ERROR']);
        }  else {
          this.serverErrorMessage = response?.message || 'Unexpected error occurred.';
        }
      } catch (parseError) {
        console.error("Parsing Error: ", parseError);
        this.serverErrorMessage = 'Unexpected error occurred.';
      }
    }

  goBack(): void {
    this.router.navigate(['/signup']);
  }
}
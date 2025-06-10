import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControlOptions, FormGroup, FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Subject, debounceTime, takeUntil } from 'rxjs';

import { AuthService } from '../../services/auth/auth.service'; 
import { AuthValidators } from '../../utilities/validation.utils'; 
import { HeaderComponent } from '../header/header.component';
import { ComingSoonComponent } from '../coming-soon/coming-soon.component';
import { UserService } from '../../services/userService/user-service.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HeaderComponent, ComingSoonComponent, ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit, OnDestroy {
  
  showComingSoon = false;
  isSubmitting = false;
  isGoogleLoading = false;
  serverErrorMessage = '';
  signupForm!: FormGroup;
  
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {
    this.initializeForm();
    this.setupDebouncedValidation();
  }

  ngOnInit(): void {
    // Initialize Google Sign-In when component loads
    setTimeout(() => {
      this.authService.initializeGoogleSignIn();
    }, 1000);

    // Subscribe to authentication state changes
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        if (user) {
          this.handleAuthSuccess(user);
        }
      });
  }

  private initializeForm(): void {
    const formOptions: AbstractControlOptions = {
      validators: AuthValidators.passwordMatch 
    };

    this.signupForm = this.fb.group({
      userName: ['', [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(16)
      ]],
      firstName: ['', [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(16)
      ]],
      lastName: ['', [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(16)
      ]],
      email: ['', [
        Validators.required,
        Validators.email
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(32),
      ]],
      confirmPassword: ['', Validators.required],
      terms: [false, Validators.requiredTrue],
    }, formOptions);
  }

  private setupDebouncedValidation(): void {
    const controls = ['userName', 'firstName', 'lastName', 'email', 'password', 'confirmPassword'];
    
    controls.forEach(controlName => {
      this.signupForm.get(controlName)?.valueChanges
        .pipe(
          debounceTime(500),
          takeUntil(this.destroy$)
        )
        .subscribe(() => {
          this.signupForm.get(controlName)?.markAsTouched();
        });
    });
  }

  // Traditional form signup
  onSubmit(): void {
    if (this.signupForm.invalid) return;

    this.isSubmitting = true;
    this.serverErrorMessage = '';
    const { confirmPassword, terms, ...signupData } = this.signupForm.value;
    console.log('Signup data:', signupData);

    this.authService.signup(signupData)
      .subscribe({
        next: (response) => {
          this.isSubmitting = false;
          this.handleSuccess();
        },
        error: (error) => this.handleError(error)
      });
  }

  // Google OAuth signup/signin
  btnGoogle = () => {
    if (this.isGoogleLoading) return;
    
    this.isGoogleLoading = true;
    this.serverErrorMessage = '';
    
    try {
      this.authService.signInWithGoogle();
    } catch (error) {
      console.error('Google Sign-In failed:', error);
      this.isGoogleLoading = false;
      this.serverErrorMessage = 'Google Sign-In failed. Please try again.';
    }
    
    // Reset loading state after a timeout in case of no response
    setTimeout(() => {
      this.isGoogleLoading = false;
    }, 10000);
  };

  // Alternative Google popup method
  btnGooglePopup = () => {
    if (this.isGoogleLoading) return;
    
    this.isGoogleLoading = true;
    this.serverErrorMessage = '';
    
    try {
      this.authService.signInWithGooglePopup();
    } catch (error) {
      console.error('Google Sign-In Popup failed:', error);
      this.isGoogleLoading = false;
      this.serverErrorMessage = 'Google Sign-In failed. Please try again.';
    }
    
    setTimeout(() => {
      this.isGoogleLoading = false;
    }, 10000);
  };

  private handleSuccess(): void {
    // Handle traditional signup success
    this.router.navigate(['/dashboard']);
  }

  private handleAuthSuccess(user: any): void {
    // Handle both traditional and Google auth success
    this.isSubmitting = false;
    this.isGoogleLoading = false;
    
    console.log('Authentication successful:', user);
    
    // Store user data
    this.userService.setCurrentUser(user);
    
    // Navigate to dashboard
    this.router.navigate(['/dashboard'], {
      state: { 
        user: user,
        authMethod: user.provider || 'email' // Track auth method
      }
    });
  }

  private handleError(error: HttpErrorResponse): void {
    this.isSubmitting = false;
    this.isGoogleLoading = false;
    
    console.log("Error headers:", error.headers);
    console.log("Error type:", typeof error.error);
    console.log("Error object:", error.error);
    console.log("Error message:", error.message);
    
    try {
      const response = typeof error.error === 'string' ? JSON.parse(error.error) : error.error;
      console.log("Parsed response:", response);
      
      if (error.status === 400) {
        this.handleValidationErrors(response["errors"] || response);
      } else if (error.status === 409) {
        // Handle conflict (user already exists)
        this.serverErrorMessage = 'An account with this email already exists. Please sign in instead.';
      } else {
        this.serverErrorMessage = response?.message || error.error?.message || 'An unexpected error occurred.';
      }
    } catch (parseError) {
      console.error("Error parsing response:", parseError);
      this.serverErrorMessage = 'An unexpected error occurred.';
    }
  }

  private handleValidationErrors(errors: Record<string, string[]> | any): void {
    if (typeof errors === 'object' && errors !== null) {
      Object.keys(errors).forEach(field => {
        const errorMessages = Array.isArray(errors[field]) ? errors[field] : [errors[field]];
        if (errorMessages.length > 0) {
          const control = this.signupForm.get(field);
          control?.setErrors({ serverError: errorMessages[0] });
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  showError(controlName: string): boolean {
    const control = this.signupForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  getErrorMessage(controlName: string): string {
    const control = this.signupForm.get(controlName);
    if (control?.errors) {
      if (control.errors['serverError']) {
        return control.errors['serverError'];
      }
      if (control.errors['required']) {
        return `${controlName} is required`;
      }
      if (control.errors['minlength']) {
        return `${controlName} must be at least ${control.errors['minlength'].requiredLength} characters`;
      }
      if (control.errors['maxlength']) {
        return `${controlName} must not exceed ${control.errors['maxlength'].requiredLength} characters`;
      }
      if (control.errors['email']) {
        return 'Please enter a valid email address';
      }
    }
    return '';
  }

  // Keep Apple button as coming soon for now
  btnApple = () => this.toggleComingSoon();
  toggleComingSoon = () => this.showComingSoon = !this.showComingSoon;

  // Helper method to check if user is already authenticated
  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  // Method to clear server errors when user starts typing
  clearServerError(): void {
    this.serverErrorMessage = '';
  }
}
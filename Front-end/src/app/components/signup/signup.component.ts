import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, AbstractControlOptions, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject, debounceTime, takeUntil } from 'rxjs';

import { AuthService } from '../../services/auth/auth.service';
import { AuthValidators } from '../../utilities/validation.utils';
import { HeaderComponent } from '../header/header.component';
import { ComingSoonComponent } from '../coming-soon/coming-soon.component';
import { UserService } from '../../services/userService/user-service.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    RouterModule, 
    ReactiveFormsModule, 
    HeaderComponent, 
    ComingSoonComponent
  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit, OnDestroy {
  //TODO EDITING AND ADDING THE GOOGLE OAUTH2
  showComingSoon = false;
  isSubmitting = false;
  serverErrorMessage = '';
  signupForm!: FormGroup;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    // Replaced multiple service injections with single AuthService
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {
    this.initializeForm();
    this.setupDebouncedValidation();
  }

  ngOnInit(): void {
    // Updated to use AuthService's currentUser$ observable
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe((user: any) => {
        if (user) this.handleAuthSuccess(user);
      });
      // STEP 1: Listen for OAuth2 callback parameters
    this.handleOAuth2Callback();
  }

  private initializeForm(): void {
    const formOptions: AbstractControlOptions = { validators: AuthValidators.passwordMatch };

    this.signupForm = this.fb.group({
      userName: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(16)]],
      firstName: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(16)]],
      lastName: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(16)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]],
      confirmPassword: ['', Validators.required],
      terms: [false, Validators.requiredTrue],
    }, formOptions);
  }

  private setupDebouncedValidation(): void {
    const controls = ['userName', 'firstName', 'lastName', 'email', 'password', 'confirmPassword'];

    controls.forEach(controlName => {
      this.signupForm.get(controlName)?.valueChanges
        .pipe(debounceTime(500), takeUntil(this.destroy$))
        .subscribe(() => {
          this.signupForm.get(controlName)?.markAsTouched();
        });
    });
  }

  onSubmit(): void {
    if (this.signupForm.invalid) return;

    this.isSubmitting = true;
    this.serverErrorMessage = '';
    const { confirmPassword, terms, ...signupData } = this.signupForm.value;

    // Updated to use AuthService's signup method
    this.authService.signup(signupData).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.handleSuccess();
      },
      error: (error) => this.handleError(error)
    });
  }

  private handleSuccess(): void {
    this.router.navigate(['/verification']);
  }

  private handleAuthSuccess(user: any): void {
    this.isSubmitting = false;
    console.log('Authentication successful:', user);
    this.userService?.setCurrentUser?.(user);
    this.router.navigate(['/dashboard'], { state: { user, authMethod: user.provider || 'email' } });
  }

  private handleError(error: HttpErrorResponse): void {
    this.isSubmitting = false;

    try {
      const response = error.error;
      
      if (error.status === 400) {
        this.handleValidationErrors(response.data['ERROR']);
      } else {
        this.serverErrorMessage = response?.message || 'Unexpected error occurred.';
      }
    } catch (parseError) {
      console.error("Parsing Error: ", parseError);
      this.serverErrorMessage = 'Unexpected error occurred.';
    }
  }

  private handleValidationErrors(errors: Record<string, string[] | string>): void {
    if (!errors) return;
    Object.keys(errors).forEach(field => {
      const errorMessages = Array.isArray(errors[field]) ? errors[field] : [errors[field]];
      const control = this.signupForm.get(field);
      if (control && errorMessages.length > 0) {
        control.setErrors({ serverError: errorMessages[0] });
      }
    });
  }

  showError(controlName: string): boolean {
    const control = this.signupForm.get(controlName);
    return control?.invalid && (control.dirty || control.touched) || false;
  }

  getErrorMessage(controlName: string): string {
    const control = this.signupForm.get(controlName);
    if (!control?.errors) return '';

    if (control.errors['serverError']) return control.errors['serverError'];
    if (control.errors['required']) return `${controlName} is required`;
    if (control.errors['minlength']) return `${controlName} must be at least ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `${controlName} must not exceed ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['email']) return 'Please enter a valid email address';

    return '';
  }
  
  btnApple = () => this.toggleComingSoon();
  toggleComingSoon = () => this.showComingSoon = !this.showComingSoon;
  
  // Updated to use AuthService's isAuthenticated method
  isAuthenticated(): boolean { 
    return this.authService.isAuthenticated(); 
  }
  
  clearServerError(): void { 
    this.serverErrorMessage = ''; 
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }



    // STEP 2: Handle OAuth2 callback from URL parameters
  private handleOAuth2Callback(): void {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    
    if (token) {
      // Clear the URL parameters to clean up the browser history
      window.history.replaceState({}, document.title, window.location.pathname);
      
      // Process the OAuth2 token
      this.processOAuth2Token(token);
    }
  }

  // STEP 3: Process OAuth2 token received from backend
  public processOAuth2Token(token: string): void {
    this.isSubmitting = true;
    this.authService.processOAuth2Token(token).subscribe({
      next: (user) => {
        this.handleAuthSuccess(user);
      },
      error: (error) => this.handleError(error)
    });
  }

  // STEP 4: Updated Google button to redirect to OAuth2 endpoint
  btnGoogle = () => {
    this.isSubmitting = true;
    // First-time generation (store in localStorage or secure storage)
const device_id = localStorage.getItem("device_id") || crypto.randomUUID();
localStorage.setItem("device_id", device_id);

const device_name = `${navigator.platform} ${navigator.userAgent}`;

// Send these during OAuth flow
const params = new URLSearchParams({
  device_id: device_id,
  device_name: device_name,
});

window.location.href = `http://192.168.1.9:8099/login/oauth2/code/google?${params}`;
  };
}
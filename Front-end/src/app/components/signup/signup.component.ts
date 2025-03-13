import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControlOptions, FormGroup, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FormBuilder, Validators, AbstractControl ,  ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Subject, debounceTime, takeUntil } from 'rxjs';
import { provideHttpClient } from '@angular/common/http';

import { AuthService } from '../../services/signUp/sign-up.service'; 
import { AuthValidators } from '../../utilities/validation.utils'; 
import { HeaderComponent } from '../header/header.component';
import { ComingSoonComponent } from '../coming-soon/coming-soon.component';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule , HeaderComponent, ComingSoonComponent,ReactiveFormsModule,],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent  implements OnDestroy  {
  
  showComingSoon = false;
  isSubmitting = false;
  serverErrorMessage = '';
  

  signupForm!: FormGroup;
  
 
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private authService: AuthService 
  ) {
    this.initializeForm();
    this.setupDebouncedValidation();
  }

  /**
   * Initialize reactive form with validations
   * @doc This method sets up the form structure and validation rules
   */
  private initializeForm(): void {
    const formOptions: AbstractControlOptions = {
      validators: AuthValidators.passwordMatch // Using static validator
    };

    this.signupForm = this.fb.group({
    
    
      username: ['', [
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
      terms: [false, Validators.requiredTrue] ,
   
    }, formOptions);
  }

  /**
   * Setup debounced validation for form controls
   * @doc Adds 500ms debounce to validation triggers
   */
  private setupDebouncedValidation(): void {
    const controls = ['username', 'firstName', 'lastName', 'email', 'password', 'confirmPassword'];
    
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

  /**
   * Handle form submission
   * @doc Manages form submission flow with loading state and error handling
   */
  onSubmit(): void {
    if (this.signupForm.invalid) return;

    this.isSubmitting = true;
    this.serverErrorMessage = '';

    this.authService.signup(this.signupForm.value)
      .subscribe({
        next: () => this.handleSuccess(),
        error: (error) => this.handleError(error)
      });
  }

  /**
   * Handle successful registration
   * @doc Manages post-success logic (redirects, notifications, etc.)
   */
  private handleSuccess(): void {
    this.isSubmitting = false;
    // Add success handling logic
  }

  /**
   * Handle API errors
   * @doc Processes server errors and updates form validation
   */
  private handleError(error: HttpErrorResponse): void {
    this.isSubmitting = false;
    
    if (error.status === 400 && error.error?.errors) {
      this.handleValidationErrors(error.error.errors);
    } else {
      this.serverErrorMessage = error.error?.message || 'An unexpected error occurred.';
    }
  }

  /**
   * Process validation errors from server
   * @doc Applies server-side validation errors to form controls
   */
  private handleValidationErrors(errors: Record<string, string[]>): void {
    Object.keys(errors).forEach(field => {
      const control = this.signupForm.get(field);
      control?.setErrors({ serverError: errors[field][0] });
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
 * Checks if a form control should show validation errors
 * @param controlName - Name of the form control to check
 * @returns Boolean indicating whether to show errors
 * @doc This helper method determines when to display validation messages
 *      based on control state (dirty/touched) and validity
 */
showError(controlName: string): boolean {
  const control = this.signupForm.get(controlName);
  return !!control && control.invalid && (control.dirty || control.touched);
}
  // UI Handlers
  btnGoogle = () => this.toggleComingSoon();
  btnApple = () => this.toggleComingSoon();
  toggleComingSoon = () => this.showComingSoon = !this.showComingSoon;
}




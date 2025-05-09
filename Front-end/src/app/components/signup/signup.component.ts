import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControlOptions, FormGroup, FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, Validators, AbstractControl ,  ReactiveFormsModule } from '@angular/forms';
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
    private authService: AuthService ,
    private userService: UserService,
    private router: Router
  ) {
    this.initializeForm();
    this.setupDebouncedValidation();
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
      terms: [false, Validators.requiredTrue] ,
   
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

  
  private handleSuccess(): void {
    this.isSubmitting = false;
    this.router.navigate(['/dashboard']);
  /*
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        // Store user data in memory (not localStorage)
        this.userService.setCurrentUser(user);
       
        // Navigate to dashboard with state
        this.router.navigate(['/dashboard'], {
          state: { 
            user: user
          }
        });
      },
      error: (error) => {
      
        this.serverErrorMessage ='An unexpected error occurred.';
      }
    });*/
  }


  private handleError(error: HttpErrorResponse): void {
    this.isSubmitting = false;
    
    console.log("1  "+ error.headers);
    console.log(typeof error.error);
    console.log( error.error);
    console.log(typeof error.error);
    console.log( error.message);
    const response = JSON.parse(error.error);
    console.log("is it a vaild json" );
    if (error.status === 400 ) {
      this.handleValidationErrors(response["errors"]);
    } else {
      this.serverErrorMessage = error.error?.message || 'An unexpected error occurred.';
    }
  }

  
  private handleValidationErrors(errors: Record<string, string[]>): void {
    Object.keys(errors).forEach(field => {
      if(errors[field].length > 0){
      const control = this.signupForm.get(field);
      control?.setErrors({ serverError: errors[field][0] });
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  
showError(controlName: string): boolean {
  const control = this.signupForm.get(controlName);
  return !!control && control.invalid && (control.dirty || control.touched);
}

  btnGoogle = () => this.toggleComingSoon();
  btnApple = () => this.toggleComingSoon();
  toggleComingSoon = () => this.showComingSoon = !this.showComingSoon;
}




import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../header/header.component';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { ComingSoonComponent } from '../coming-soon/coming-soon.component';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, FormsModule, ComingSoonComponent, HeaderComponent, ReactiveFormsModule],
  templateUrl: './signin.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
      ]),
    ]),
  ],
})
export class SigninComponent {
  showComingSoon = false;
  submitted = false;
  serverError = '';
  isLoading = false;

  signinForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
    rememberMe: new FormControl(false)
  });

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.submitted = true;
    this.serverError = '';

    if (this.signinForm.invalid) {
      return;
    }

    this.isLoading = true;
    const { email, password } = this.signinForm.value;

    this.authService.signin(email ?? '', password ?? '').subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (error: HttpErrorResponse) => {
        this.serverError = error.message || 'An unexpected error occurred.';
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  clearServerError() {
    if (this.serverError) {
      this.serverError = '';
    }
  }

  btnSoon = () => this.toggleComingSoon();
  toggleComingSoon = () => this.showComingSoon = !this.showComingSoon;
}
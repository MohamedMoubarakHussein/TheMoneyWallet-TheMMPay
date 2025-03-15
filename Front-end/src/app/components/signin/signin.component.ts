import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../header/header.component';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { UserService } from '../../services/userService/user-service.service';
import { ComingSoonComponent } from '../coming-soon/coming-soon.component';


@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, FormsModule , ComingSoonComponent,HeaderComponent,ReactiveFormsModule],
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.scss']
})

export class SigninComponent {
  showComingSoon = false;
  submitted = false;
  serverError = '';

  signinForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
    rememberMe: new FormControl(false)
  });


   constructor( private authService: AuthService,
    private router: Router,
    private userService : UserService) {}

  onSubmit() {
    this.submitted = true;
    this.serverError = '';

    if (this.signinForm.invalid) {
      return;
    }
    const { email, password, rememberMe } = this.signinForm.value;

    this.authService.signin(
      email ?? '',
      password ?? ''
    ).subscribe({
      next: () => this.handleSuccess(),
      error: (error) => this.handleError(error)

        
    });
  }



  private handleSuccess(): void {
    this.router.navigate(['/dashboard']);
   /* this.userService.getCurrentUser().subscribe({
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
      
        this.serverError ='An unexpected error occurred.';
      }
    });*/
  }


  private handleError(err: any) {
    console.log(err.message);
    if (err.status === 400) {
      this.serverError = 'Invalid email or password';
    } else if (err.status === 0) {
      this.serverError = 'Unable to connect to the server. Please check your internet connection.';
    } else {
      this.serverError = 'An unexpected error occurred. Please try again later.';
    }
    
    this.signinForm.get('password')?.reset();
  }


  clearServerError() {
    if (this.serverError) {
      this.serverError = '';
    }
  }
  btnSoon = () => this.toggleComingSoon();
  toggleComingSoon = () => this.showComingSoon = !this.showComingSoon;

}







 


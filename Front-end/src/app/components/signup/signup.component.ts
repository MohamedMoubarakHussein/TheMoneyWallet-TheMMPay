import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { ComingSoonComponent } from '../coming-soon/coming-soon.component';


@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule , HeaderComponent, ComingSoonComponent],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  onSubmit() {
    // Handle form submission
    console.log('Form submitted');
  }

  btnGoogle(){
    this.toggleComingSoon();
  }

  btnApple(){
    this.toggleComingSoon();
  }

  showComingSoon = true; // Flag to control visibility

  // Toggle the ComingSoonComponent
  toggleComingSoon() {
    this.showComingSoon = !this.showComingSoon;
  }
}
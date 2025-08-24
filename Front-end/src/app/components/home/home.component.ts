import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { Router } from '@angular/router';
import { FooterComponent } from '../footer/footer.component';
import { trigger, style, transition, animate } from '@angular/animations';
import { HomeService } from '../../services/home.service';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './home.component.html',
  animations: [
    trigger('fadeInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-20px)' }),
        animate('1000ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('1000ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('zoomIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.8)' }),
        animate('800ms ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),
    trigger('slideInFromRight', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(100%)' }),
        animate('1000ms ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ])
    ])
  ]
})
export class HomeComponent implements AfterViewInit {
  constructor(private router: Router, private homeService: HomeService, private authService: AuthService) {}

  ngAfterViewInit() {
    console.log('HomeComponent: ngAfterViewInit called');
    this.homeService.animateCounters();
  }

  goToSingUp() {
    this.router.navigate(['/signup']);
  }

  loginAsTestUser() {
    this.authService.loginAsTestUser();
  }
}
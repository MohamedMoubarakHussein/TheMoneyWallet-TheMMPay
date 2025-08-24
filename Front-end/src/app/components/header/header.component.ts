import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { trigger, style, transition, animate } from '@angular/animations';
import { AuthService } from '../../services/auth/auth.service';
import { Observable } from 'rxjs';
import { User } from '../../entity/UnifiedResponse';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  animations: [
    trigger('fadeInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class HeaderComponent {
  isMenuOpen = false;
  currentRoute = '';
  currentUser$: Observable<User | null>;
  isAuthenticated$: Observable<boolean>;

  constructor(private router: Router, private authService: AuthService) {
    this.router.events.subscribe(() => {
      this.currentRoute = this.router.url;
    });
    this.currentUser$ = this.authService.currentUser$;
    this.isAuthenticated$ = this.authService.isAuthenticated$;

    this.currentUser$.subscribe(user => console.log('HeaderComponent: currentUser$', user));
    this.isAuthenticated$.subscribe(status => console.log('HeaderComponent: isAuthenticated$', status));
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  goTo(path: string) {
    this.router.navigate([path]);
  }

  logout() {
    this.authService.logout();
  }
}
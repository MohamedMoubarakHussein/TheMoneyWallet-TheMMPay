import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { MobileNavigationService } from '../../services/mobile-navigation.service';
import { AuthService } from '../../services/auth/auth.service';
import { User } from '../../entity/UnifiedResponse';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { RippleDirective } from '../../directives/ripple/ripple.directive';
import { HoverAnimationDirective } from '../../directives/hover-animation/hover-animation.directive';

@Component({
  selector: 'app-mobile-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule, RippleDirective, HoverAnimationDirective],
  templateUrl: './mobile-navigation.component.html',
  animations: [
    trigger('overlayAnimation', [
      state('void', style({ opacity: 0 })),
      transition(':enter, :leave', [animate('300ms ease-in-out')])
    ]),
    trigger('menuAnimation', [
      state('void', style({ transform: 'translateX(100%)' })),
      transition(':enter, :leave', [animate('300ms ease-in-out')])
    ]),
    trigger('userMenuAnimation', [
      transition(':enter', [
        style({ height: 0, opacity: 0 }),
        animate('300ms ease-out', style({ height: '*', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ height: 0, opacity: 0 }))
      ])
    ]),
    trigger('notificationsAnimation', [
      transition(':enter', [
        style({ height: 0, opacity: 0 }),
        animate('300ms ease-out', style({ height: '*', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ height: 0, opacity: 0 }))
      ])
    ]),
  ]
})
export class MobileNavigationComponent {
  isOpen$: Observable<boolean>;
  showUserMenu$: Observable<boolean>;
  showNotifications$: Observable<boolean>;
  currentUser$: Observable<User | null>;

  navigationItems = [
    { path: '/dashboard', label: 'Dashboard', icon: '🏠' },
    { path: '/send-money', label: 'Send Money', icon: '📤', requiresAuth: true },
    { path: '/add-funds', label: 'Add Funds', icon: '💰', requiresAuth: true },
    { path: '/transactions', label: 'Transactions', icon: '📊', requiresAuth: true },
    { path: '/support', label: 'Support', icon: '🛠️', requiresAuth: true },
  ];
  quickActions = [
    { action: 'send-money', label: 'Send', icon: '📤', requiresAuth: true },
    { action: 'add-funds', label: 'Add', icon: '💰', requiresAuth: true },
    { action: 'request-money', label: 'Request', icon: '📥', requiresAuth: true },
    { action: 'pay-bills', label: 'Bills', icon: '🧾', requiresAuth: true },
  ];

  constructor(
    public navService: MobileNavigationService,
    private authService: AuthService,
    private router: Router
  ) {
    this.isOpen$ = this.navService.isOpen$();
    this.showUserMenu$ = this.navService.showUserMenu$();
    this.showNotifications$ = this.navService.showNotifications$();
    this.currentUser$ = this.authService.currentUser$;
  }

  getUserInitials(user: User | null): string {
    if (!user) return '';
    return (user.firstName?.[0] ?? '') + (user.lastName?.[0] ?? '');
  }

  getUserDisplayName(user: User | null): string {
    if (!user) return '';
    return `${user.firstName} ${user.lastName}`;
  }

  getTotalBalance(user: User | null): string {
    if (!user || !user.wallets) return '$0.00';
    const total = user.wallets.reduce((acc, wallet) => acc + wallet.balance, 0);
    return `${total.toFixed(2)}`;
  }

  getWalletCount(user: User | null): number {
    return user?.wallets?.length ?? 0;
  }

  getNotificationCount(user: User | null): number {
    return user?.Notifications?.length ?? 0;
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
    this.navService.closeNavigation();
  }

  executeQuickAction(action: string): void {
    this.navigateTo(`/${action}`);
  }

  isActiveRoute(path: string): boolean {
    return this.router.url === path;
  }

  toggleUserMenu(): void {
    this.navService.toggleUserMenu();
  }

  toggleNotifications(): void {
    this.navService.toggleNotifications();
  }

  toggleNavigation(): void {
    this.navService.toggleNavigation();
  }

  closeNavigation(): void {
    this.navService.closeNavigation();
  }

  logout() {
    this.authService.logout();
    this.navService.closeNavigation();
  }
}
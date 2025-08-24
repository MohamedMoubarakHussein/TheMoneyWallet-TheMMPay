import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { User, Notification, UserWallet } from '../../../entity/UnifiedResponse';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-sub-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sub-header.component.html',
  animations: [
    trigger('fadeInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class SubHeaderComponent {
  @Input() user: User | null = null;
  @Input() notifications: Notification[] = [];
  @Input() selectedWallet: UserWallet | null = null;

  @Output() walletSelected = new EventEmitter<UserWallet>();

  showNotifications = false;
  showUserMenu = false;

  constructor(private router: Router) {}

  get userName(): string {
    return this.user?.fullName || 'User';
  }

  get unreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    this.showUserMenu = false;
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
    this.showNotifications = false;
  }
  
  logout() {
    // Implement logout logic
  }
}

import { Component, Input, Output, EventEmitter, HostListener, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '../../../services/notification/notification-service.service';
import { WalletService } from '../../../services/wallet/wallet-service.service';
import { User, Notification,UserWallet } from '../../../entity/UnifiedResponse';

@Component({
  selector: 'app-sub-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sub-header.component.html',
  styleUrls: ['./sub-header.component.css']
})
export class SubHeaderComponent implements OnInit, OnDestroy {
  // Input properties from parent component
  @Input() user: User | null = null;
  @Input() notifications: Notification[] = [];
  @Input() selectedWallet: UserWallet | null = null;
  @Input() availableWallets: UserWallet[] = [];
  @Input() isUpdatingWallet: boolean = false;

  // Output events to parent component
  @Output() walletSelected = new EventEmitter<UserWallet>();
  @Output() notificationMarkedAsRead = new EventEmitter<string>();
  @Output() allNotificationsCleared = new EventEmitter<void>();
  @Output() primaryWalletUpdateRequested = new EventEmitter<string>();

  // Header-specific UI state
  showNotifications = false;
  showUserMenu = false;
  showWalletSelector = false;

  private destroy$ = new Subject<void>();

  constructor(
   // private authService: AuthService,
    private notificationService: NotificationService,
    private walletService: WalletService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Header-specific initialization if needed
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Computed properties (getters)
  get userName(): string {
    return this.user?.fullName || this.user?.firstName || 'User';
  }

  get unreadCount(): number {
    return this.notifications.filter(n => n).length;
  }

  get selectedWalletBalance(): number {
    return this.selectedWallet?.balance || 0;
  }

  get selectedWalletCurrency(): string {
    return this.selectedWallet?.currency || 'USD';
  }

  get selectedWalletName(): string {
    return this.selectedWallet?.name || 'Select Wallet';
  }

  // Host listeners for dropdown management
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    
    if (!target.closest('.notification-wrapper')) {
      this.showNotifications = false;
    }
    
    if (!target.closest('.user-menu-wrapper')) {
      this.showUserMenu = false;
    }
    
    if (!target.closest('.wallet-selector-wrapper')) {
      this.showWalletSelector = false;
    }
  }

  @HostListener('window:keydown.escape')
  onEscapeKey(): void {
    this.closeAllDropdowns();
  }

  // UI toggle methods
  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.showUserMenu = false;
      this.showWalletSelector = false;
    }
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
    if (this.showUserMenu) {
      this.showNotifications = false;
      this.showWalletSelector = false;
    }
  }

  toggleWalletSelector(): void {
    if (this.availableWallets.length === 0) return;
    
    this.showWalletSelector = !this.showWalletSelector;
    if (this.showWalletSelector) {
      this.showNotifications = false;
      this.showUserMenu = false;
    }
  }

  private closeAllDropdowns(): void {
    this.showNotifications = false;
    this.showUserMenu = false;
    this.showWalletSelector = false;
  }

  // Wallet management methods
  selectWallet(wallet: UserWallet): void {
    if (!this.isWalletActive(wallet)) {
      console.warn('Cannot select inactive wallet:', wallet.name);
      return;
    }

    this.showWalletSelector = false;
    console.log('Wallet selected:', wallet);
    
    // Emit wallet selection to parent
    this.walletSelected.emit(wallet);
    
    // Request primary wallet update if needed
    if (this.user && wallet.id !== this.user.primaryWalletId) {
      this.primaryWalletUpdateRequested.emit(wallet.id);
    }
  }

  // Wallet utility methods
  getWalletTypeIcon(walletType: string): string {
    const iconMap: { [key: string]: string } = {
      'checking': '🏦',
      'savings': '💰',
      'credit': '💳',
      'crypto': '₿',
      'business': '🏢',
      'investment': '📈',
      'default': '👛'
    };
    return iconMap[walletType?.toLowerCase()] || iconMap['default'];
  }

  getWalletTypeColor(walletType: string): string {
    const colorMap: { [key: string]: string } = {
      'checking': '#4CAF50',
      'savings': '#2196F3', 
      'credit': '#FF9800',
      'crypto': '#9C27B0',
      'business': '#795548',
      'investment': '#E91E63',
      'default': '#757575'
    };
    return colorMap[walletType?.toLowerCase()] || colorMap['default'];
  }

  isWalletActive(wallet: UserWallet): boolean {
    return wallet.status === 'active';
  }

  // Notification methods
  markAsRead(notificationId: string): void {
    const notification = this.notifications.find(n => n );
    if (notification && !notification) {
      // Optimistically update UI
     // notification.read = true;
      
      // Emit to parent for server update
      this.notificationMarkedAsRead.emit(notificationId);
    }
  }

  clearAllNotifications(): void {
    if (this.notifications.length === 0) return;
    
    // Emit to parent for server update
    this.allNotificationsCleared.emit();
    this.showNotifications = false;
  }

  // Navigation methods
  addWallet(): void {
    this.router.navigate(['/add-wallet']);
  }

  // User menu actions
  navigateToProfile(): void {
    this.router.navigate(['/profile']);
  }

  navigateToSettings(): void {
    this.router.navigate(['/settings']);
  }

 
}
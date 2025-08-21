import { Component, OnInit, OnDestroy, HostListener, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ResponsiveService } from '../../services/responsive/responsive.service';
import { AuthService } from '../../services/auth/auth.service';
import { User } from '../../entity/UnifiedResponse';
import { trigger, state, style, transition, animate, query, stagger, group } from '@angular/animations';
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
      state('*', style({ opacity: 1 })),
      transition('void <=> *', animate('300ms ease-in-out'))
    ]),
    trigger('menuAnimation', [
      state('void', style({ transform: 'translateX(100%)' })),
      state('*', style({ transform: 'translateX(0)' })),
      transition('void => *', [
        animate('400ms cubic-bezier(0.25, 0.8, 0.25, 1)')
      ]),
      transition('* => void', [
        animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)')
      ])
    ]),
    trigger('userMenuAnimation', [
      state('void', style({ height: '0', opacity: 0, overflow: 'hidden' })),
      state('*', style({ height: '*', opacity: 1 })),
      transition('void <=> *', [
        animate('300ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ])
    ]),
    trigger('notificationsAnimation', [
      state('void', style({ height: '0', opacity: 0, overflow: 'hidden' })),
      state('*', style({ height: '*', opacity: 1 })),
      transition('void <=> *', [
        animate('300ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ])
    ]),
    trigger('navItemsAnimation', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateX(20px)' }),
          stagger('50ms', [
            animate('300ms ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
          ])
        ], { optional: true })
      ])
    ]),
    trigger('quickActionsAnimation', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(10px)' }),
          stagger('75ms', [
            animate('250ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ]),
    trigger('bottomNavAnimation', [
      state('void', style({ transform: 'translateY(100%)' })),
      state('*', style({ transform: 'translateY(0)' })),
      transition('void <=> *', animate('300ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ]),
    trigger('fabAnimation', [
      state('void', style({ transform: 'scale(0)', opacity: 0 })),
      state('*', style({ transform: 'scale(1)', opacity: 1 })),
      transition('void <=> *', animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)'))
    ])
  ]
})
export class MobileNavigationComponent implements OnInit, OnDestroy {
  isOpen = false;
  isMobile = false;
  currentUser: User | null = null;
  showUserMenu = false;
  showNotifications = false;
  
  // Navigation items
  navigationItems = [
    { path: '/dashboard', label: 'Dashboard', icon: '🏠', badge: null },
    { path: '/send-money', label: 'Send Money', icon: '📤', badge: null },
    { path: '/add-funds', label: 'Add Funds', icon: '💰', badge: null },
    { path: '/pay-bill', label: 'Pay Bills', icon: '💳', badge: null },
    { path: '/request-money', label: 'Request Money', icon: '📥', badge: null },
    { path: '/transactions', label: 'Transactions', icon: '📊', badge: null },
    { path: '/invoices', label: 'Invoices', icon: '📄', badge: null },
    { path: '/budgets', label: 'Budgets', icon: '📈', badge: null },
    { path: '/recurring-payments', label: 'Recurring', icon: '🔄', badge: null },
    { path: '/analytics', label: 'Analytics', icon: '📊', badge: null },
    { path: '/contacts', label: 'Contacts', icon: '👥', badge: null },
    { path: '/notifications', label: 'Notifications', icon: '🔔', badge: null },
    { path: '/security', label: 'Security', icon: '🔒', badge: null },
    { path: '/settings', label: 'Settings', icon: '⚙️', badge: null }
  ];

  // Quick actions
  quickActions = [
    { action: 'sendMoney', label: 'Send Money', icon: '📤', color: '#4CAF50' },
    { action: 'addFunds', label: 'Add Funds', icon: '💰', color: '#2196F3' },
    { action: 'scanQR', label: 'Scan QR', icon: '📱', color: '#FF9800' },
    { action: 'payContact', label: 'Pay Contact', icon: '👤', color: '#9C27B0' }
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private responsiveService: ResponsiveService,
    private authService: AuthService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.setupResponsiveService();
    this.setupAuthService();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupResponsiveService(): void {
    this.responsiveService.isMobile$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isMobile => {
        this.isMobile = isMobile;
        if (!isMobile) {
          this.closeNavigation();
        }
      });
  }

  private setupAuthService(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    
    // Close navigation if clicking outside
    if (!target.closest('.mobile-nav') && !target.closest('.nav-toggle')) {
      this.closeNavigation();
    }
    
    // Close user menu if clicking outside
    if (!target.closest('.user-menu')) {
      this.showUserMenu = false;
    }
    
    // Close notifications if clicking outside
    if (!target.closest('.notifications-menu')) {
      this.showNotifications = false;
    }
  }

  @HostListener('window:keydown.escape')
  onEscapeKey(): void {
    this.closeNavigation();
    this.showUserMenu = false;
    this.showNotifications = false;
  }

  toggleNavigation(): void {
    this.isOpen = !this.isOpen;
    
    // Only access document in browser environment
    if (isPlatformBrowser(this.platformId)) {
      if (this.isOpen) {
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = '';
      }
    }
  }

  closeNavigation(): void {
    this.isOpen = false;
    // Only access document in browser environment
    if (isPlatformBrowser(this.platformId)) {
      document.body.style.overflow = '';
    }
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
    this.showNotifications = false;
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    this.showUserMenu = false;
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
    this.closeNavigation();
  }

  executeQuickAction(action: string): void {
    switch (action) {
      case 'sendMoney':
        this.router.navigate(['/send-money']);
        break;
      case 'addFunds':
        this.router.navigate(['/add-funds']);
        break;
      case 'scanQR':
        this.scanQRCode();
        break;
      case 'payContact':
        this.router.navigate(['/contacts']);
        break;
    }
    this.closeNavigation();
  }

  private scanQRCode(): void {
    // Implement QR code scanning functionality
    console.log('Scanning QR code...');
  }

  logout(): void {
    this.authService.logout();
    this.closeNavigation();
    this.router.navigate(['/signin']);
  }

  getUserInitials(): string {
    if (!this.currentUser) return 'U';
    
    const firstName = this.currentUser.firstName || '';
    const lastName = this.currentUser.lastName || '';
    
    if (firstName && lastName) {
      return `${firstName[0]}${lastName[0]}`.toUpperCase();
    } else if (firstName) {
      return firstName[0].toUpperCase();
    } else if (lastName) {
      return lastName[0].toUpperCase();
    } else if (this.currentUser.fullName) {
      return this.currentUser.fullName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
    }
    
    return 'U';
  }

  getUserDisplayName(): string {
    if (!this.currentUser) return 'User';
    
    if (this.currentUser.fullName) {
      return this.currentUser.fullName;
    } else if (this.currentUser.firstName && this.currentUser.lastName) {
      return `${this.currentUser.firstName} ${this.currentUser.lastName}`;
    } else if (this.currentUser.firstName) {
      return this.currentUser.firstName;
    } else if (this.currentUser.email) {
      return this.currentUser.email.split('@')[0];
    }
    
    return 'User';
  }

  // Check if current route is active
  isActiveRoute(path: string): boolean {
    return this.router.url === path;
  }

  // Get notification count (mock data)
  getNotificationCount(): number {
    return this.currentUser?.Notifications?.filter(n => !n.read).length || 0;
  }

  // Get wallet count
  getWalletCount(): number {
    return this.currentUser?.wallets?.length || 0;
  }

  // Get total balance
  getTotalBalance(): string {
    if (!this.currentUser?.totalBalance) return '$0.00';
    return `$${this.currentUser.totalBalance.toFixed(2)}`;
  }

  // Check if user has any active wallets
  hasActiveWallets(): boolean {
    return this.currentUser?.wallets?.some(w => w.status === 'active') || false;
  }

  // Get primary wallet
  getPrimaryWallet(): any {
    if (!this.currentUser?.wallets) return null;
    return this.currentUser.wallets.find(w => w.id === this.currentUser?.primaryWalletId) ||
           this.currentUser.wallets.find(w => w.status === 'active') ||
           this.currentUser.wallets[0];
  }

  // Get primary wallet balance
  getPrimaryWalletBalance(): string {
    const primaryWallet = this.getPrimaryWallet();
    if (!primaryWallet) return '$0.00';
    return `$${primaryWallet.balance.toFixed(2)}`;
  }

  // Get primary wallet currency
  getPrimaryWalletCurrency(): string {
    const primaryWallet = this.getPrimaryWallet();
    return primaryWallet?.currency || 'USD';
  }

  // Check if user has recent transactions
  hasRecentTransactions(): boolean {
    return (this.currentUser?.recentTransactions?.length || 0) > 0;
  }

  // Get recent transaction count
  getRecentTransactionCount(): number {
    return this.currentUser?.recentTransactions?.length || 0;
  }

  // Check if user has pending transactions
  hasPendingTransactions(): boolean {
    return this.currentUser?.recentTransactions?.some(t => t.status === 'pending') || false;
  }

  // Get pending transaction count
  getPendingTransactionCount(): number {
    return this.currentUser?.recentTransactions?.filter(t => t.status === 'pending').length || 0;
  }
} 
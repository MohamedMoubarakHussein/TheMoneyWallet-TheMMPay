import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { WalletService } from '../../services/wallet/wallet-service.service';
import { NotificationService } from '../../services/notification/notification-service.service';
import { TransactionService } from '../../services/transaction/transaction-service.service';
import { User, Transaction, Notification, UserWallet } from '../../entity/UnifiedResponse';
import { mockWallets, mockNotifications , mockTransactions ,mockUnifiedResponse , mockUsers } from '../../environments/mock';
import { RecentTransactionsComponent } from "./RecentTransactionsComponent/recentTransactions.component";
import { HeaderComponent } from "../header/header.component";
import { FooterComponent } from "../footer/footer.component";
import { SubHeaderComponent } from "./sub-header/sub-header.component";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RecentTransactionsComponent, FooterComponent, SubHeaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  // User and UI state
  user: User | null = null;
  userName: string = 'User';
  showNotifications = false;
  showUserMenu = false;
  showWalletSelector = false;

  // Data properties
  notifications: Notification[] = [];
  transactions: Transaction[] = [];
  selectedWallet: UserWallet | null = null;

  // Loading states
  isLoadingData = true;
  isUpdatingWallet = false;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private walletService: WalletService,
    private notificationService: NotificationService,
    private transactionService: TransactionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.setupDataRefresh();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Data loading methods
  private loadUserData(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (user) => {
          user = mockUsers[1];
          this.user = user;
          if (user) {

            this.userName = user.fullName || user.firstName || 'User';
            this.loadDashboardData(user);
            this.initializeSelectedWallet(user);
          }
          this.isLoadingData = false;
        },
        error: (error) => {
          console.error('Error loading user data:', error);
          this.isLoadingData = false;
        }
      });
  }

  private loadDashboardData(user: User): void {
    // Load notifications
    this.notifications = user.Notifications || [];
    
    // Load recent transactions
    this.transactions = user.recentTransactions || [];
    // Load additional data if needed
    this.loadRecentTransactions();
    this.loadNotifications();
  }

  private loadRecentTransactions(): void {
    if (this.selectedWallet) {
      this.transactionService.getRecentTransactions(this.selectedWallet.id, 5)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (transactions) => {
            this.transactions = transactions;
          },
          error: (error) => {
            console.error('Error loading transactions:', error);
          }
        });
    }
  }

  private loadNotifications(): void {
    this.notificationService.getNotifications()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (notifications) => {
          this.notifications = notifications;
        },
        error: (error) => {
          console.error('Error loading notifications:', error);
        }
      });
  }

  private setupDataRefresh(): void {
    // Refresh data every 30 seconds
    setInterval(() => {
      if (this.user && !this.isLoadingData) {
        this.loadRecentTransactions();
        this.loadNotifications();
      }
    }, 30000);
  }

  private initializeSelectedWallet(user: User): void {
    if (user.wallets && user.wallets.length > 0) {
      // Set primary wallet as default, or first active wallet
      this.selectedWallet = user.wallets.find(w => w.id === user.primaryWalletId && this.isWalletActive(w)) 
        || user.wallets.find(w => this.isWalletActive(w))
        || user.wallets[0];
    }
    this.selectedWallet =mockWallets[0];
  }

  // Computed properties (getters)
  get unreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  get availableWallets(): UserWallet[] {
    return this.user?.wallets || mockWallets;
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

  // Event handlers
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

  // Wallet management
  selectWallet(wallet: UserWallet): void {
    if (!this.isWalletActive(wallet)) {
      console.warn('Cannot select inactive wallet:', wallet.name);
      return;
    }

    this.selectedWallet = wallet;
    this.showWalletSelector = false;
    console.log('Wallet selected:', wallet);
    
    // Load transactions for the selected wallet
    this.loadRecentTransactions();
    
    // Update primary wallet if it's different
    if (this.user && wallet.id !== this.user.primaryWalletId) {
      this.updatePrimaryWallet(wallet.id);
    }
  }

  private updatePrimaryWallet(walletId: string): void {
    if (this.isUpdatingWallet) return;
    
    this.isUpdatingWallet = true;
    this.walletService.updatePrimaryWallet(walletId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('Primary wallet updated successfully');
          if (this.user) {
            this.user.primaryWalletId = walletId;
          }
          this.isUpdatingWallet = false;
        },
        error: (error) => {
          console.error('Error updating primary wallet:', error);
          this.isUpdatingWallet = false;
        }
      });
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

  // Action methods
  sendMoney(): void {
    if (!this.selectedWallet) {
      console.warn('No wallet selected for sending money');
      this.showWalletSelector = true;
      return;
    }
    
    if (!this.isWalletActive(this.selectedWallet)) {
      console.warn('Selected wallet is not active');
      return;
    }

    console.log('Send Money clicked from wallet:', this.selectedWallet.name);
    this.router.navigate(['/send-money'], { 
      queryParams: { walletId: this.selectedWallet.id } 
    });
  }

  addFunds(): void {
    if (!this.selectedWallet) {
      console.warn('No wallet selected for adding funds');
      this.showWalletSelector = true;
      return;
    }

    if (!this.isWalletActive(this.selectedWallet)) {
      console.warn('Selected wallet is not active');
      return;
    }

    console.log('Add Funds clicked to wallet:', this.selectedWallet.name);
    this.router.navigate(['/add-funds'], { 
      queryParams: { walletId: this.selectedWallet.id } 
    });
  }

  requestMoney(): void {
    if (!this.selectedWallet) {
      console.warn('No wallet selected for requesting money');
      this.showWalletSelector = true;
      return;
    }

    if (!this.isWalletActive(this.selectedWallet)) {
      console.warn('Selected wallet is not active');
      return;
    }

    console.log('Request Money clicked for wallet:', this.selectedWallet.name);
    this.router.navigate(['/request-money'], { 
      queryParams: { walletId: this.selectedWallet.id } 
    });
  }

  invoice(): void {
    if (!this.selectedWallet) {
      console.warn('No wallet selected for invoice');
      this.showWalletSelector = true;
      return;
    }

    if (!this.isWalletActive(this.selectedWallet)) {
      console.warn('Selected wallet is not active');
      return;
    }

    console.log('Invoice clicked for wallet:', this.selectedWallet.name);
    this.router.navigate(['/invoice'], { 
      queryParams: { walletId: this.selectedWallet.id } 
    });
  }

  // Notification methods
  markAsRead(notificationId: string): void {
    const notification = this.notifications.find(n => n.id === notificationId);
    if (notification && !notification.read) {
      notification.read = true;
      
      // Update on server
      this.notificationService.markAsRead(notificationId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            console.log('Notification marked as read');
          },
          error: (error) => {
            console.error('Error marking notification as read:', error);
            // Revert on error
            notification.read = false;
          }
        });
    }
  }

  clearAllNotifications(): void {
    if (this.notifications.length === 0) return;
    
    this.notificationService.clearAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notifications = [];
          this.showNotifications = false;
          console.log('All notifications cleared');
        },
        error: (error) => {
          console.error('Error clearing notifications:', error);
        }
      });
  }

  // User actions

/*  logout(): void {
    this.authService.logout()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Error during logout:', error);
          // Force redirect even on error
          this.router.navigate(['/login']);
        }
      });
  }
*/
  // Navigation methods
  viewAllTransactions(): void {
    this.router.navigate(['/transactions'], {
      queryParams: this.selectedWallet ? { walletId: this.selectedWallet.id } : {}
    });
  }

  addWallet(): void {
    this.router.navigate(['/add-wallet']);
  }

  // Utility methods
  refreshData(): void {
    if (!this.isLoadingData) {
      this.isLoadingData = true;
      this.loadUserData();
    }
  }

  formatTransactionAmount(transaction: Transaction): string {
    const sign = transaction.type === 'received' ? '+' : '-';
    return `${sign}${transaction.amount}`;
  }

  getTransactionIcon(transaction: Transaction): string {
    const iconMap: { [key: string]: string } = {
      'sent': '📤',
      'received': '📥',
      'deposit': '💰',
      'withdrawal': '💸',
      'transfer': '🔄',
      'payment': '💳',
      'refund': '↩️',
      'default': '💰'
    };
    return iconMap[transaction.type?.toLowerCase()] || iconMap['default'];
  }
}
import { Injectable } from '@angular/core';
import { combineLatest, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { AuthService } from './auth/auth.service';
import { WalletService } from './wallet/wallet-service.service';
import { TransactionService } from './transaction/transaction-service.service';
import { User, UserWallet, Transaction, Notification } from '../entity/UnifiedResponse';

export interface DashboardData {
  currentUser: User | null;
  wallets: UserWallet[];
  recentTransactions: Transaction[];
  unreadNotifications: Notification[];
  unreadNotificationCount: number;
  primaryWallet: UserWallet | null;
  totalBalance: number;
  walletCount: number;
  transactionCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardDataService {
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();

  private dashboardDataSubject = new BehaviorSubject<DashboardData | null>(null);
  dashboardData$ = this.dashboardDataSubject.asObservable();

  constructor(
    private authService: AuthService,
    private walletService: WalletService,
    private transactionService: TransactionService
  ) {}

  loadDashboardData(): void {
    this.isLoadingSubject.next(true);

    combineLatest([
      this.authService.currentUser$,
      this.walletService.wallets$,
      this.transactionService.transactions$
    ]).pipe(
      map(([currentUser, wallets, transactions]) => {
        const unreadNotifications = currentUser?.Notifications?.filter(n => !n.read) || [];
        const primaryWallet = this.getPrimaryWallet(wallets, currentUser);

        return {
          currentUser,
          wallets,
          recentTransactions: transactions.slice(0, 10),
          unreadNotifications,
          unreadNotificationCount: unreadNotifications.length,
          primaryWallet,
          totalBalance: currentUser?.totalBalance || 0,
          walletCount: wallets.length,
          transactionCount: transactions.length
        };
      }),
      tap(() => this.isLoadingSubject.next(false)),
    ).subscribe(data => {
      this.dashboardDataSubject.next(data as DashboardData);
    });

    // Initial data fetch
    this.walletService.getWallets().subscribe();
    this.transactionService.getTransactions().subscribe();
  }

  refreshData(): void {
    this.isLoadingSubject.next(true);
    // This will trigger the combineLatest to re-emit
    this.walletService.getWallets(true).subscribe();
    this.transactionService.getTransactions().subscribe();
  }

  private getPrimaryWallet(wallets: UserWallet[], currentUser: User | null): UserWallet | null {
    if (!wallets.length) return null;
    return wallets.find(w => w.id === currentUser?.primaryWalletId) ||
           wallets.find(w => w.status === 'active') ||
           wallets[0];
  }
}
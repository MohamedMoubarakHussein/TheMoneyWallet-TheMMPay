import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HoverAnimationDirective } from '../../directives/hover-animation/hover-animation.directive';
import { RippleDirective } from '../../directives/ripple/ripple.directive';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../services/auth/auth.service';
import { WalletService } from '../../services/wallet/wallet-service.service';
import { TransactionService } from '../../services/transaction/transaction-service.service';
import { User, UserWallet, Transaction, Notification } from '../../entity/UnifiedResponse';
import { NgChartsModule } from 'ng2-charts';
import { Chart, ChartConfiguration, ChartType } from 'chart.js';
import 'chart.js/auto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterModule, HoverAnimationDirective, RippleDirective, NgChartsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('transactionChart') transactionChartCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('balanceChart') balanceChartCanvas!: ElementRef<HTMLCanvasElement>;
  
  currentUser: User | null = null;
  wallets: UserWallet[] = [];
  recentTransactions: Transaction[] = [];
  isLoading = false;
  pageLoaded = false;
  
  // Chart configurations
  transactionChart: Chart | null = null;
  balanceChart: Chart | null = null;
  
  // Chart data
  transactionChartData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'Income',
        data: [1200, 1900, 1500, 2200, 1800, 2400],
        backgroundColor: 'rgba(16, 185, 129, 0.2)',
        borderColor: 'rgba(16, 185, 129, 1)',
        borderWidth: 2,
        tension: 0.4,
        fill: true
      },
      {
        label: 'Expenses',
        data: [900, 1200, 1700, 1400, 1600, 1300],
        backgroundColor: 'rgba(239, 68, 68, 0.2)',
        borderColor: 'rgba(239, 68, 68, 1)',
        borderWidth: 2,
        tension: 0.4,
        fill: true
      }
    ]
  };
  
  balanceChartData = {
    labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
    datasets: [
      {
        label: 'Balance',
        data: [3200, 3800, 3500, 4200],
        backgroundColor: 'rgba(59, 130, 246, 0.2)',
        borderColor: 'rgba(59, 130, 246, 1)',
        borderWidth: 2,
        tension: 0.4,
        fill: true,
        pointBackgroundColor: 'rgba(59, 130, 246, 1)',
        pointRadius: 4
      }
    ]
  };
  
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private walletService: WalletService,
    private transactionService: TransactionService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.setupAuthService();
    this.setupWalletService();
    this.setupTransactionService();
    this.loadDashboardData();
  }

  ngAfterViewInit(): void {
    // Initialize charts after view is initialized
    setTimeout(() => {
      this.initializeCharts();
    }, 500);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    
    // Destroy charts to prevent memory leaks
    if (this.transactionChart) {
      this.transactionChart.destroy();
    }
    
    if (this.balanceChart) {
      this.balanceChart.destroy();
    }
  }
  
  private initializeCharts(): void {
    if (this.transactionChartCanvas && this.balanceChartCanvas) {
      this.initTransactionChart();
      this.initBalanceChart();
    }
  }
  
  private initTransactionChart(): void {
    const ctx = this.transactionChartCanvas.nativeElement.getContext('2d');
    
    if (ctx) {
      this.transactionChart = new Chart(ctx, {
        type: 'line',
        data: this.transactionChartData,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'top',
              labels: {
                usePointStyle: true,
                padding: 20,
                font: {
                  family: "'Inter', sans-serif",
                  size: 12
                }
              }
            },
            tooltip: {
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
              titleColor: '#111827',
              bodyColor: '#4b5563',
              borderColor: '#e5e7eb',
              borderWidth: 1,
              padding: 12,
              boxPadding: 6,
              usePointStyle: true,
              callbacks: {
                label: function(context) {
                  return `${context.dataset.label}: $${context.parsed.y}`;
                }
              }
            }
          },
          scales: {
            x: {
              grid: {
                display: false
              },
              ticks: {
                font: {
                  family: "'Inter', sans-serif",
                  size: 12
                }
              }
            },
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(243, 244, 246, 1)'
              },
              ticks: {
                font: {
                  family: "'Inter', sans-serif",
                  size: 12
                },
                callback: function(value) {
                  return '$' + value;
                }
              }
            }
          },
          elements: {
            line: {
              tension: 0.4
            },
            point: {
              radius: 4,
              hitRadius: 10,
              hoverRadius: 6
            }
          },
          animation: {
            duration: 1000,
            easing: 'easeOutQuart'
          }
        }
      });
    }
  }
  
  private initBalanceChart(): void {
    const ctx = this.balanceChartCanvas.nativeElement.getContext('2d');
    
    if (ctx) {
      this.balanceChart = new Chart(ctx, {
        type: 'line',
        data: this.balanceChartData,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
              titleColor: '#111827',
              bodyColor: '#4b5563',
              borderColor: '#e5e7eb',
              borderWidth: 1,
              padding: 12,
              boxPadding: 6,
              callbacks: {
                label: function(context) {
                  return `Balance: $${context.parsed.y}`;
                }
              }
            }
          },
          scales: {
            x: {
              grid: {
                display: false
              },
              ticks: {
                font: {
                  family: "'Inter', sans-serif",
                  size: 12
                }
              }
            },
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(243, 244, 246, 1)'
              },
              ticks: {
                font: {
                  family: "'Inter', sans-serif",
                  size: 12
                },
                callback: function(value) {
                  return '$' + value;
                }
              }
            }
          },
          elements: {
            line: {
              tension: 0.4
            },
            point: {
              radius: 4,
              hitRadius: 10,
              hoverRadius: 6
            }
          },
          animation: {
            duration: 1000,
            easing: 'easeOutQuart'
          }
        }
      });
    }
  }

  private setupAuthService(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  private setupWalletService(): void {
    this.walletService.wallets$
      .pipe(takeUntil(this.destroy$))
      .subscribe(wallets => {
        this.wallets = wallets;
      });
  }

  private setupTransactionService(): void {
    this.transactionService.transactions$
      .pipe(takeUntil(this.destroy$))
      .subscribe(transactions => {
        this.recentTransactions = transactions.slice(0, 10);
      });
  }

  private loadDashboardData(): void {
    this.isLoading = true;
    
    // Load wallets and transactions
    Promise.all([
      this.walletService.getWallets().toPromise(),
      this.transactionService.getTransactions().toPromise()
    ]).finally(() => {
      this.isLoading = false;
      
      // Set page loaded status after a short delay to ensure animations work properly
      setTimeout(() => {
        this.pageLoaded = true;
        this.cdr.markForCheck();
      }, 300);
    });
  }

  // Navigation methods
  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  // Data refresh
  refreshData(): void {
    this.loadDashboardData();
    this.refreshCharts();
  }
  
  // Refresh charts with new data
  refreshCharts(): void {
    // In a real application, you would fetch new data here
    // For demo purposes, we'll just update with random data
    
    // Update transaction chart data
    this.transactionChartData.datasets[0].data = Array.from({length: 6}, () => Math.floor(Math.random() * 2000) + 800);
    this.transactionChartData.datasets[1].data = Array.from({length: 6}, () => Math.floor(Math.random() * 1500) + 500);
    
    // Update balance chart data
    this.balanceChartData.datasets[0].data = Array.from({length: 4}, () => Math.floor(Math.random() * 2000) + 2000);
    
    // Update charts
    if (this.transactionChart && this.balanceChart) {
      this.transactionChart.update();
      this.balanceChart.update();
    }
    
    this.cdr.markForCheck();
  }

  // Balance methods
  getTotalBalance(): number {
    if (!this.currentUser?.totalBalance) return 0;
    return this.currentUser.totalBalance;
  }

  getBalanceChange(): number {
    // Mock balance change - in real app, calculate from transaction history
    return 125.50;
  }

  // Wallet methods
  getPrimaryWallet(): UserWallet | null {
    if (!this.wallets.length) return null;
    return this.wallets.find(w => w.id === this.currentUser?.primaryWalletId) ||
           this.wallets.find(w => w.status === 'active') ||
           this.wallets[0];
  }

  getWalletCount(): number {
    return this.wallets.length;
  }

  selectWallet(wallet: UserWallet): void {
    // Navigate to wallet details or select wallet
    this.router.navigate(['/wallets', wallet.id]);
  }

  // Transaction methods
  getTransactionCount(): number {
    return this.recentTransactions.length;
  }

  getTransactionType(transaction: Transaction): string {
    if (transaction.type === 'received' || transaction.type === 'deposit') return 'credit';
    if (transaction.type === 'sent' || transaction.type === 'withdrawal') return 'debit';
    return 'transfer';
  }

  getTransactionIcon(transaction: Transaction): string {
    if (transaction.type === 'received' || transaction.type === 'deposit') return '📥';
    if (transaction.type === 'sent' || transaction.type === 'withdrawal') return '📤';
    return '🔄';
  }

  getTransactionSign(transaction: Transaction): string {
    if (transaction.type === 'received' || transaction.type === 'deposit') return '+';
    if (transaction.type === 'sent' || transaction.type === 'withdrawal') return '-';
    return '';
  }

  viewTransaction(transaction: Transaction): void {
    this.router.navigate(['/transactions'], { 
      queryParams: { id: transaction.id } 
    });
  }

  // Notification methods
  getUnreadNotificationCount(): number {
    if (!this.currentUser?.Notifications) return 0;
    return this.currentUser.Notifications.filter(n => !n.read).length;
  }

  getUnreadNotifications(): Notification[] {
    if (!this.currentUser?.Notifications) return [];
    return this.currentUser.Notifications.filter(n => !n.read);
  }

  viewNotification(notification: Notification): void {
    this.router.navigate(['/notifications'], { 
      queryParams: { id: notification.id } 
    });
  }

  // Utility methods
  getCurrentUser(): User | null {
    return this.currentUser;
  }

  getWallets(): UserWallet[] {
    return this.wallets;
  }

  getTransactions(): Transaction[] {
    return this.recentTransactions;
  }
}
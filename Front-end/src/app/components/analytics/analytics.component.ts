import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TransactionService } from '../../services/transaction/transaction-service.service';
import { BudgetService } from '../../services/budget/budget.service';
import { WalletService } from '../../services/wallet/wallet-service.service';
import { trigger, state, style, transition, animate, query, stagger } from '@angular/animations';
import { NgChartsModule } from 'ng2-charts';
import { AnimatedChartComponent } from '../animated-chart/animated-chart.component';
import { ProgressRingComponent } from '../progress-ring/progress-ring.component';
import { AnimatedCounterComponent } from '../animated-counter/animated-counter.component';
import { AnimationService } from '../../services/animation/animation.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    NgChartsModule, 
    AnimatedChartComponent, 
    ProgressRingComponent, 
    AnimatedCounterComponent
  ],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css'],
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInLeft', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(-30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ])
    ]),
    trigger('slideInRight', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ])
    ]),
    trigger('scaleIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.8)' }),
        animate('0.4s ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),
    trigger('bounceIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.3)' }),
        animate('0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55)', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),
    trigger('staggerIn', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(100, [
            animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ]),
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('0.3s ease-out', style({ opacity: 1 }))
      ])
    ])
  ]
})
export class AnalyticsComponent implements OnInit, OnDestroy {
  isLoading = false;
  selectedPeriod = 'month';
  selectedWallet = 'all';
  
  // Analytics data
  spendingByCategory: any[] = [];
  spendingTrends: any[] = [];
  incomeVsExpenses: any[] = [];
  walletPerformance: any[] = [];
  
  // Summary data
  totalSpending = 0;
  totalIncome = 0;
  totalSavings = 0;
  totalTransactions = 0;
  
  private destroy$ = new Subject<void>();

  // Chart data
  categoryChartData: any = {
    labels: [],
    datasets: []
  };
  
  trendChartData: any = {
    labels: [],
    datasets: []
  };
  
  incomeExpenseChartData: any = {
    labels: [],
    datasets: []
  };
  
  constructor(
    private transactionService: TransactionService,
    private budgetService: BudgetService,
    private walletService: WalletService,
    private animationService: AnimationService
  ) {}

  ngOnInit(): void {
    this.loadAnalytics();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadAnalytics(): void {
    this.isLoading = true;
    
    // Simulate API loading delay
    setTimeout(() => {
      // Load various analytics data
      this.loadSpendingByCategory();
      this.loadSpendingTrends();
      this.loadIncomeVsExpenses();
      this.loadWalletPerformance();
      this.loadSummaryData();
      
      // Prepare chart data
      this.prepareCategoryChartData();
      this.prepareTrendChartData();
      this.prepareIncomeExpenseChartData();
      
      this.isLoading = false;
    }, 1000);
  }
  
  /**
   * Prepare data for category spending chart
   */
  private prepareCategoryChartData(): void {
    this.categoryChartData = {
      labels: this.spendingByCategory.map(cat => cat.name),
      datasets: [{
        data: this.spendingByCategory.map(cat => cat.amount),
        backgroundColor: this.spendingByCategory.map((_, i) => this.getCategoryColor(i)),
        hoverBackgroundColor: this.spendingByCategory.map((_, i) => {
          const color = this.getCategoryColor(i);
          return this.adjustBrightness(color, 20);
        })
      }]
    };
  }
  
  /**
   * Prepare data for spending trends chart
   */
  private prepareTrendChartData(): void {
    this.trendChartData = {
      labels: this.spendingTrends.map(trend => {
        const date = new Date(trend.date);
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
      }),
      datasets: [{
        label: 'Spending',
        data: this.spendingTrends.map(trend => trend.amount),
        backgroundColor: 'rgba(59, 130, 246, 0.5)',
        borderColor: '#3b82f6',
        tension: 0.4,
        fill: true
      }]
    };
  }
  
  /**
   * Prepare data for income vs expenses chart
   */
  private prepareIncomeExpenseChartData(): void {
    this.incomeExpenseChartData = {
      labels: this.incomeVsExpenses.map(item => item.month),
      datasets: [
        {
          label: 'Income',
          data: this.incomeVsExpenses.map(item => item.income),
          backgroundColor: 'rgba(16, 185, 129, 0.7)',
          borderColor: '#10b981',
          borderWidth: 1
        },
        {
          label: 'Expenses',
          data: this.incomeVsExpenses.map(item => item.expenses),
          backgroundColor: 'rgba(239, 68, 68, 0.7)',
          borderColor: '#ef4444',
          borderWidth: 1
        }
      ]
    };
  }
  
  /**
   * Adjust brightness of a hex color
   */
  private adjustBrightness(color: string, percent: number): string {
    const num = parseInt(color.replace('#', ''), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = (num >> 8 & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;
    
    return '#' + (
      0x1000000 +
      (R < 255 ? (R < 0 ? 0 : R) : 255) * 0x10000 +
      (G < 255 ? (G < 0 ? 0 : G) : 255) * 0x100 +
      (B < 255 ? (B < 0 ? 0 : B) : 255)
    ).toString(16).slice(1);
  }

  private loadSpendingByCategory(): void {
    // Mock data for spending by category
    this.spendingByCategory = [
      { name: 'Food & Dining', amount: 850.25, percentage: 26.2 },
      { name: 'Transportation', amount: 650.50, percentage: 20.0 },
      { name: 'Shopping', amount: 520.75, percentage: 16.0 },
      { name: 'Entertainment', amount: 420.00, percentage: 12.9 },
      { name: 'Utilities', amount: 380.25, percentage: 11.7 },
      { name: 'Healthcare', amount: 323.00, percentage: 9.9 },
      { name: 'Other', amount: 100.00, percentage: 3.3 }
    ];
  }

  private loadSpendingTrends(): void {
    // Mock data for spending trends
    this.spendingTrends = [
      { date: '2024-01-01', amount: 120.50 },
      { date: '2024-01-02', amount: 85.25 },
      { date: '2024-01-03', amount: 210.75 },
      { date: '2024-01-04', amount: 95.00 },
      { date: '2024-01-05', amount: 180.50 },
      { date: '2024-01-06', amount: 145.25 },
      { date: '2024-01-07', amount: 320.00 }
    ];
  }

  private loadIncomeVsExpenses(): void {
    // Mock data for income vs expenses
    this.incomeVsExpenses = [
      { month: 'Jan', income: 8500, expenses: 3245 },
      { month: 'Feb', income: 9200, expenses: 3850 },
      { month: 'Mar', income: 7800, expenses: 2950 },
      { month: 'Apr', income: 9500, expenses: 4100 },
      { month: 'May', income: 8700, expenses: 3650 },
      { month: 'Jun', income: 10200, expenses: 4450 }
    ];
  }

  private loadWalletPerformance(): void {
    // Mock data for wallet performance
    this.walletPerformance = [
      {
        name: 'Primary Wallet',
        balance: 5420.50,
        growth: 12.5,
        transactions: 45
      },
      {
        name: 'Savings Wallet',
        balance: 12500.00,
        growth: 8.3,
        transactions: 12
      },
      {
        name: 'Business Wallet',
        balance: 8750.25,
        growth: 15.2,
        transactions: 28
      }
    ];
  }

  private loadSummaryData(): void {
    // Mock summary data
    this.totalSpending = 3245.75;
    this.totalIncome = 8500.00;
    this.totalSavings = 5254.25;
    this.totalTransactions = 85;
  }

  onPeriodChange(): void {
    this.loadAnalytics();
  }

  onWalletChange(): void {
    this.loadAnalytics();
  }

  exportAnalytics(): void {
    // Implementation for exporting analytics
    const analyticsData = {
      period: this.selectedPeriod,
      wallet: this.selectedWallet,
      summary: {
        totalSpending: this.totalSpending,
        totalIncome: this.totalIncome,
        totalSavings: this.totalSavings,
        totalTransactions: this.totalTransactions
      },
      spendingByCategory: this.spendingByCategory,
      spendingTrends: this.spendingTrends,
      incomeVsExpenses: this.incomeVsExpenses,
      walletPerformance: this.walletPerformance
    };
    
    const dataStr = JSON.stringify(analyticsData, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    
    const url = window.URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `analytics-${this.selectedPeriod}-${new Date().toISOString().split('T')[0]}.json`;
    link.click();
    
    window.URL.revokeObjectURL(url);
  }

  getCategoryColor(index: number): string {
    const colors = [
      '#3B82F6', '#EF4444', '#10B981', '#F59E0B', 
      '#8B5CF6', '#EC4899', '#06B6D4', '#84CC16'
    ];
    return colors[index % colors.length];
  }
} 
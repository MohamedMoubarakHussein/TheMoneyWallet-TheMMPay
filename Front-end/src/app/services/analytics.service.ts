import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ChartData } from 'chart.js';

// Interfaces
export interface AnalyticsData {
  totalSpending: number;
  totalIncome: number;
  totalSavings: number;
  totalTransactions: number;
  spendingByCategory: ChartData;
  spendingTrends: ChartData;
  incomeVsExpenses: ChartData;
  walletPerformance: WalletPerformance[];
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private analyticsData = new BehaviorSubject<AnalyticsData | null>(null);
  public isLoading = new BehaviorSubject<boolean>(false);
  private selectedPeriod = new BehaviorSubject<string>('month');
  private selectedWallet = new BehaviorSubject<string>('all');

  constructor() {
    this.loadAnalytics();
  }

  getAnalyticsData(): Observable<AnalyticsData | null> { return this.analyticsData.asObservable(); }

  loadAnalytics() {
    this.isLoading.next(true);
    // Mock data loading
    of({
      totalSpending: 3245.75,
      totalIncome: 8500.00,
      totalSavings: 5254.25,
      totalTransactions: 85,
      spendingByCategory: { labels: ['Food', 'Transport'], datasets: [{ data: [850, 650] }] },
      spendingTrends: { labels: ['Jan', 'Feb'], datasets: [{ data: [120, 85] }] },
      incomeVsExpenses: { labels: ['Jan', 'Feb'], datasets: [{ label: 'Income', data: [8500, 9200] }, { label: 'Expenses', data: [3245, 3850] }] },
      walletPerformance: [{ name: 'Primary', balance: 5420, growth: 12.5, transactions: 45 }]
    }).pipe(delay(1000)).subscribe(data => {
      this.analyticsData.next(data);
      this.isLoading.next(false);
    });
  }

  onPeriodChange(period: string) {
    this.selectedPeriod.next(period);
    this.loadAnalytics();
  }

  onWalletChange(wallet: string) {
    this.selectedWallet.next(wallet);
    this.loadAnalytics();
  }

  exportAnalytics(): string {
    return JSON.stringify(this.analyticsData.value, null, 2);
  }
}
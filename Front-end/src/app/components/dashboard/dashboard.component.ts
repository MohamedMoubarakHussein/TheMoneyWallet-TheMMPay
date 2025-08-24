import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HoverAnimationDirective } from '../../directives/hover-animation/hover-animation.directive';
import { RippleDirective } from '../../directives/ripple/ripple.directive';
import { Subject, Observable } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UserWallet, Transaction, Notification } from '../../entity/UnifiedResponse';
import { NgChartsModule } from 'ng2-charts';
import { Chart } from 'chart.js';
import { trigger, transition, style, animate, query, stagger } from '@angular/animations';
import { DashboardData, DashboardDataService } from '../../services/dashboard-data.service';
import { ChartService } from '../../services/chart.service';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterModule, HoverAnimationDirective, RippleDirective, NgChartsModule, HeaderComponent, FooterComponent],
  templateUrl: './dashboard.component.html',
  animations: [
    trigger('listAnimation', [
      transition('* <=> *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(-10px)' }),
          stagger('100ms', animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })))
        ], { optional: true })
      ])
    ])
  ]
})
export class DashboardComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('transactionChart') transactionChartCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('balanceChart') balanceChartCanvas!: ElementRef<HTMLCanvasElement>;

  dashboardData$: Observable<DashboardData | null>;
  isLoading$: Observable<boolean>;

  private transactionChart: Chart | null = null;
  private balanceChart: Chart | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private dashboardDataService: DashboardDataService,
    private chartService: ChartService
  ) {
    this.dashboardData$ = this.dashboardDataService.dashboardData$;
    this.isLoading$ = this.dashboardDataService.isLoading$;
  }

  ngOnInit(): void {
    this.dashboardDataService.loadDashboardData();
  }

  ngAfterViewInit(): void {
    this.dashboardData$.pipe(takeUntil(this.destroy$)).subscribe(data => {
      if (data) {
        this.initializeCharts();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.transactionChart?.destroy();
    this.balanceChart?.destroy();
  }

  private initializeCharts(): void {
    if (this.transactionChartCanvas && this.balanceChartCanvas) {
      // Mock data for charts - replace with real data from your service
      const transactionChartData = {
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

      const balanceChartData = {
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

      this.transactionChart = this.chartService.createTransactionChart(this.transactionChartCanvas.nativeElement, transactionChartData);
      this.balanceChart = this.chartService.createBalanceChart(this.balanceChartCanvas.nativeElement, balanceChartData);
      this.cdr.markForCheck();
    }
  }

  refreshData(): void {
    this.dashboardDataService.refreshData();
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  viewTransaction(transaction: Transaction): void {
    this.router.navigate(['/transactions'], { queryParams: { id: transaction.id } });
  }

  viewNotification(notification: Notification): void {
    this.router.navigate(['/notifications'], { queryParams: { id: notification.id } });
  }

  selectWallet(wallet: UserWallet): void {
    this.router.navigate(['/wallets', wallet.id]);
  }

  // Utility methods for template
  getTransactionIcon(transaction: Transaction): string {
    if (transaction.type === 'received' || transaction.type === 'deposit') return 'fa-arrow-down';
    if (transaction.type === 'sent' || transaction.type === 'withdrawal') return 'fa-arrow-up';
    return 'fa-exchange-alt';
  }

  getTransactionType(transaction: Transaction): string {
    if (transaction.type === 'received' || transaction.type === 'deposit') return 'text-green-500';
    if (transaction.type === 'sent' || transaction.type === 'withdrawal') return 'text-red-500';
    return 'text-gray-500';
  }

  getTransactionSign(transaction: Transaction): string {
    if (transaction.type === 'received' || transaction.type === 'deposit') return '+';
    if (transaction.type === 'sent' || transaction.type === 'withdrawal') return '-';
    return '';
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { AnalyticsService, AnalyticsData } from '../../services/analytics.service';
import { trigger, style, transition, animate } from '@angular/animations';
import { AnimatedChartComponent } from '../animated-chart/animated-chart.component';
import { AsStringArrayPipe } from '../../pipes/as-string-array.pipe';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule, AnimatedChartComponent, AsStringArrayPipe],
  templateUrl: './analytics.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class AnalyticsComponent {
  analyticsData$: Observable<AnalyticsData | null>;
  isLoading$: Observable<boolean>;
  selectedPeriod = 'month';
  selectedWallet = 'all';

  constructor(private analyticsService: AnalyticsService) {
    this.analyticsData$ = this.analyticsService.getAnalyticsData();
    this.isLoading$ = this.analyticsService.isLoading.asObservable();
  }

  onPeriodChange(): void {
    this.analyticsService.onPeriodChange(this.selectedPeriod);
  }

  onWalletChange(): void {
    this.analyticsService.onWalletChange(this.selectedWallet);
  }

  exportAnalytics(): void {
    const dataStr = this.analyticsService.exportAnalytics();
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = window.URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `analytics-${this.selectedPeriod}-${new Date().toISOString().split('T')[0]}.json`;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
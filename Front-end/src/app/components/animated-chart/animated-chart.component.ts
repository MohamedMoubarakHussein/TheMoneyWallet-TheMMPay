import { Component, Input, OnInit, AfterViewInit, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartData, ChartType, ChartDataset, ChartOptions, Plugin } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { ChartAnimationService } from '../../services/chart-animation.service';

interface ChartColors {
  backgroundColor: string[];
  borderColor: string[];
}

@Component({
  selector: 'app-animated-chart',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './animated-chart.component.html',
  animations: [
    ChartAnimationService.prototype.fadeIn,
    ChartAnimationService.prototype.chartAnimation
  ]
})
export class AnimatedChartComponent implements OnInit, AfterViewInit {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;
  
  @Input() chartType: ChartType = 'bar';
  @Input() labels: string[] = [];
  @Input() datasets: ChartDataset<ChartType>[] = [];
  
  @Input() height = '300px';
  @Input() loading = false;
  @Input() error = false;
  @Input() errorMessage = 'Failed to load chart data';
  @Input() isEmpty = false;
  
  @Output() retry = new EventEmitter<void>();
  
  chart: Chart | null = null;
  chartData: ChartData = { labels: [], datasets: [] };
  chartOptions: ChartOptions = {};
  chartPlugins: Plugin<ChartType, ChartData>[] = [];

  constructor() {}

  ngOnInit(): void {
    this.initializeChartData();
    this.initializeChartOptions();
  }
  
  ngAfterViewInit(): void {
    if (!this.loading && !this.error && !this.isEmpty && this.chartCanvas) {
      this.initializeChart();
    }
  }
  
  private initializeChartData(): void {
    this.chartData = {
      labels: this.labels,
      datasets: this.datasets.map(dataset => ({
        ...dataset,
        backgroundColor: dataset.backgroundColor || this.getDefaultColors().backgroundColor,
        borderColor: dataset.borderColor || this.getDefaultColors().borderColor,
      }))
    };
  }
  
  private initializeChartOptions(): void {
    this.chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      animation: {
        duration: 1000,
        easing: 'easeOutQuart'
      },
      scales: this.getScaleOptions(),
      plugins: {
        legend: { display: true, position: 'bottom' },
        tooltip: { enabled: true }
      }
    };
  }
  
  private initializeChart(): void {
    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (ctx) {
      if (this.chart) this.chart.destroy();
      this.chart = new Chart(ctx, {
        type: this.chartType,
        data: this.chartData,
        options: this.chartOptions,
        plugins: this.chartPlugins
      });
    }
  }
  
  private getScaleOptions(): ChartOptions['scales'] {
    if (this.chartType === 'line' || this.chartType === 'bar') {
      return {
        x: { grid: { display: false } },
        y: { beginAtZero: true }
      };
    }
    return {};
  }
  
  private getDefaultColors(): ChartColors {
    const baseColors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b', '#8b5cf6'];
    return {
      backgroundColor: baseColors.map(c => `${c}B3`),
      borderColor: baseColors,
    };
  }
  
  retryLoad(): void {
    this.retry.emit();
  }
}
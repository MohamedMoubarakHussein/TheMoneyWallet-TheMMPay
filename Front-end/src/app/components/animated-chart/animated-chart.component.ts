import { Component, Input, OnInit, AfterViewInit, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Chart, ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';

interface ChartColors {
  backgroundColor: string[];
  borderColor: string[];
  hoverBackgroundColor?: string[];
  hoverBorderColor?: string[];
  pointBackgroundColor?: string[];
  pointBorderColor?: string[];
  pointHoverBackgroundColor?: string[];
  pointHoverBorderColor?: string[];
}

@Component({
  selector: 'app-animated-chart',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  template: `
    <div class="chart-wrapper" [ngClass]="{'loading': loading}" [style.height]="height" [@fadeIn]>
      <!-- Loading Indicator -->
      <div class="chart-loading" *ngIf="loading">
        <div class="chart-spinner"></div>
        <div class="chart-loading-text">Loading chart data...</div>
      </div>
      
      <!-- Error State -->
      <div class="chart-error" *ngIf="error && !loading">
        <div class="chart-error-icon">⚠️</div>
        <div class="chart-error-text">{{ errorMessage }}</div>
        <button class="chart-retry-btn" (click)="retryLoad()">Retry</button>
      </div>

      <!-- Empty State -->
      <div class="chart-empty" *ngIf="!loading && !error && isEmpty">
        <div class="chart-empty-icon">📊</div>
        <div class="chart-empty-text">No data available for the selected period</div>
      </div>
      
      <!-- Chart Canvas -->
      <canvas
        *ngIf="!loading && !error && !isEmpty" 
        baseChart
        #chartCanvas
        [data]="chartData"
        [type]="chartType"
        [options]="chartOptions"
        [plugins]="chartPlugins"
        [@chartAnimation]
      ></canvas>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      width: 100%;
    }
    
    .chart-wrapper {
      position: relative;
      width: 100%;
      height: 100%;
      min-height: 200px;
      background-color: #ffffff;
      border-radius: 0.5rem;
      overflow: hidden;
    }
    
    .chart-loading {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background-color: rgba(255, 255, 255, 0.8);
      z-index: 5;
      animation: fadeIn 0.3s ease-out;
    }
    
    .chart-spinner {
      width: 40px;
      height: 40px;
      border: 3px solid rgba(59, 130, 246, 0.2);
      border-radius: 50%;
      border-top-color: #3b82f6;
      animation: spin 1s linear infinite;
      margin-bottom: 1rem;
    }
    
    .chart-loading-text {
      font-size: 0.875rem;
      color: #64748b;
      font-weight: 500;
    }
    
    .chart-error, .chart-empty {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background-color: #f8fafc;
      padding: 1.5rem;
      animation: fadeIn 0.3s ease-out;
    }
    
    .chart-error-icon, .chart-empty-icon {
      font-size: 2rem;
      margin-bottom: 1rem;
    }
    
    .chart-error-text, .chart-empty-text {
      font-size: 0.875rem;
      color: #64748b;
      text-align: center;
      max-width: 300px;
      margin-bottom: 1.5rem;
    }
    
    .chart-retry-btn {
      padding: 0.5rem 1rem;
      background-color: #3b82f6;
      color: white;
      border: none;
      border-radius: 0.25rem;
      font-weight: 500;
      cursor: pointer;
      transition: background-color 0.2s ease;
    }
    
    .chart-retry-btn:hover {
      background-color: #2563eb;
    }
    
    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }
    
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('0.4s ease-out', style({ opacity: 1 }))
      ])
    ]),
    trigger('chartAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.95)' }),
        animate('0.6s 0.2s cubic-bezier(0.25, 0.46, 0.45, 0.94)', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ])
  ]
})
export class AnimatedChartComponent implements OnInit, AfterViewInit {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;
  
  @Input() chartType: ChartType = 'bar';
  @Input() labels: string[] = [];
  @Input() datasets: Array<{
    label?: string;
    data: number[];
    backgroundColor?: string | string[];
    borderColor?: string | string[];
    fill?: boolean;
  }> = [];
  
  @Input() height: string = '300px';
  @Input() responsive: boolean = true;
  @Input() maintainAspectRatio: boolean = false;
  @Input() animationEnabled: boolean = true;
  @Input() animationDuration: number = 1000;
  @Input() colors: ChartColors | null = null;
  @Input() loading: boolean = false;
  @Input() error: boolean = false;
  @Input() errorMessage: string = 'Failed to load chart data';
  @Input() isEmpty: boolean = false;
  
  @Output() retry = new EventEmitter<void>();
  
  chart: Chart | null = null;
  chartData: ChartData = {
    labels: [],
    datasets: []
  };
  chartOptions: ChartConfiguration['options'] = {};
  chartPlugins: any[] = [];

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
      datasets: this.datasets.map(dataset => {
        return {
          label: dataset.label || '',
          data: dataset.data,
          backgroundColor: dataset.backgroundColor || this.getDefaultColors().backgroundColor,
          borderColor: dataset.borderColor || this.getDefaultColors().borderColor,
          fill: dataset.fill || false,
          // These settings enhance animations
          tension: 0.4,  // Adds smoothing to line charts
          borderWidth: 2,
          pointRadius: 4,
          pointHoverRadius: 7,
          // Enable per-dataset animation
          animation: {
            delay: (context: any) => {
              return context.dataIndex * 100 + context.datasetIndex * 100;
            }
          }
        };
      })
    };
  }
  
  private initializeChartOptions(): void {
    this.chartOptions = {
      responsive: this.responsive,
      maintainAspectRatio: this.maintainAspectRatio,
      animation: {
        duration: this.animationEnabled ? this.animationDuration : 0,
        easing: 'easeOutQuart'
      },
      scales: this.getScaleOptions(),
      plugins: {
        legend: {
          display: true,
          position: 'bottom',
          labels: {
            boxWidth: 12,
            padding: 15,
            usePointStyle: true,
            font: {
              size: 11
            }
          }
        },
        tooltip: {
          enabled: true,
          backgroundColor: 'rgba(17, 24, 39, 0.9)',
          titleFont: {
            size: 13,
            weight: 'bold'
          },
          bodyFont: {
            size: 12
          },
          padding: 12,
          cornerRadius: 6,
          caretSize: 6,
          displayColors: true,
          boxWidth: 10,
          boxHeight: 10,
          boxPadding: 4,
          animation: {
            duration: 150
          }
        }
      },
      hover: {
        mode: 'nearest',
        intersect: false
      }
    };
  }
  
  private initializeChart(): void {
    // Implement progressive animation for charts
    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (ctx) {
      if (this.chart) {
        this.chart.destroy();
      }
      
      this.chart = new Chart(ctx, {
        type: this.chartType,
        data: this.chartData,
        options: this.chartOptions,
        plugins: this.chartPlugins
      });
    }
  }
  
  private getScaleOptions(): any {
    switch (this.chartType) {
      case 'line':
      case 'bar':
        return {
          x: {
            grid: {
              display: false,
              drawBorder: false
            },
            ticks: {
              padding: 10,
              font: {
                size: 10
              }
            }
          },
          y: {
            grid: {
              color: 'rgba(226, 232, 240, 0.5)',
              drawBorder: false
            },
            ticks: {
              padding: 10,
              font: {
                size: 10
              },
              // Only show integers for y-axis if possible
              stepSize: 1
            },
            // Start at zero for most charts
            beginAtZero: true
          }
        };
      case 'pie':
      case 'doughnut':
      case 'polarArea':
        return {}; // No scales for these chart types
      default:
        return {};
    }
  }
  
  private getDefaultColors(): ChartColors {
    const baseColors = [
      '#3b82f6', // blue
      '#ef4444', // red
      '#10b981', // green
      '#f59e0b', // yellow
      '#8b5cf6', // purple
      '#ec4899', // pink
      '#06b6d4', // cyan
      '#84cc16', // lime
      '#64748b'  // slate
    ];
    
    return {
      backgroundColor: baseColors.map(c => this.hexToRgba(c, 0.7)),
      borderColor: baseColors.map(c => c),
      pointBackgroundColor: baseColors.map(c => c),
      pointBorderColor: baseColors.map(c => '#ffffff'),
      pointHoverBackgroundColor: baseColors.map(c => this.hexToRgba(c, 1)),
      pointHoverBorderColor: baseColors.map(c => this.hexToRgba(c, 1))
    };
  }
  
  private hexToRgba(hex: string, alpha: number): string {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
  }
  
  updateChartData(labels: string[], datasets: any[]): void {
    this.labels = labels;
    this.datasets = datasets;
    this.initializeChartData();
    
    if (this.chart) {
      this.chart.data = this.chartData;
      this.chart.update();
    }
  }
  
  retryLoad(): void {
    this.retry.emit();
  }
  
  // Helper methods for common chart types
  setAsPieChart(data: number[], labels: string[], title?: string): void {
    this.chartType = 'pie';
    this.updateChartData(labels, [{
      data: data,
      backgroundColor: this.getDefaultColors().backgroundColor,
      borderColor: this.getDefaultColors().borderColor
    }]);
    
    // Pie-specific options
    this.chartOptions = {
      ...this.chartOptions,
      plugins: {
        ...this.chartOptions?.plugins,
        legend: {
          position: 'right',
          labels: {
            boxWidth: 12,
            padding: 15,
            usePointStyle: true,
            font: { size: 11 }
          }
        },
        title: {
          display: !!title,
          text: title || ''
        },
        tooltip: {
          callbacks: {
            label: (context: any) => {
              const value = context.raw;
              const sum = context.dataset.data.reduce((a: number, b: number) => a + b, 0);
              const percentage = (value * 100 / sum).toFixed(1) + '%';
              return `${context.label}: ${percentage}`;
            }
          }
        }
      },
      // Pie chart doesn't need cutout percentage
    };
  }
  
  setAsDonutChart(data: number[], labels: string[], title?: string): void {
    this.setAsPieChart(data, labels, title);
    this.chartType = 'doughnut';
    this.chartOptions = {
      ...this.chartOptions,
      // Apply cutout percentage via Chart configuration
      plugins: {
        ...this.chartOptions?.plugins,
        tooltip: {
          ...this.chartOptions?.plugins?.tooltip
        }
      }
    };
  }
  
  setAsBarChart(data: number[], labels: string[], title?: string, horizontal: boolean = false): void {
    this.chartType = horizontal ? 'bar' : 'bar';
    this.updateChartData(labels, [{
      label: title,
      data: data,
      backgroundColor: this.getDefaultColors().backgroundColor
    }]);
    
    const indexAxis = horizontal ? 'y' : 'x';
    
    this.chartOptions = {
      ...this.chartOptions,
      indexAxis: indexAxis as 'x' | 'y',
      plugins: {
        ...this.chartOptions?.plugins,
        legend: {
          display: false
        },
        title: {
          display: !!title,
          text: title || ''
        }
      }
    };
  }
  
  setAsLineChart(datasets: Array<{label: string, data: number[]}>, labels: string[], title?: string): void {
    this.chartType = 'line';
    
    // Generate colors for each dataset
    const colors = this.getDefaultColors();
    
    const formattedDatasets = datasets.map((dataset, i) => {
      return {
        label: dataset.label,
        data: dataset.data,
        borderColor: Array.isArray(colors.borderColor) ? colors.borderColor[i % colors.borderColor.length] : colors.borderColor,
        backgroundColor: Array.isArray(colors.backgroundColor) ? colors.backgroundColor[i % colors.backgroundColor.length] : colors.backgroundColor,
        fill: false,
        tension: 0.4
      };
    });
    
    this.updateChartData(labels, formattedDatasets);
    
    this.chartOptions = {
      ...this.chartOptions,
      plugins: {
        ...this.chartOptions?.plugins,
        title: {
          display: !!title,
          text: title || ''
        }
      }
    };
  }
}

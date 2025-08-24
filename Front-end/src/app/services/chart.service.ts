import { Injectable } from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js';
import 'chart.js/auto';

@Injectable({
  providedIn: 'root'
})
export class ChartService {

  constructor() { }

  createTransactionChart(canvas: HTMLCanvasElement, data: ChartData): Chart {
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      throw new Error('Failed to get canvas context');
    }
    return new Chart(ctx, {
      type: 'line',
      data: data,
      options: this.getTransactionChartOptions()
    });
  }

  createBalanceChart(canvas: HTMLCanvasElement, data: ChartData): Chart {
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      throw new Error('Failed to get canvas context');
    }
    return new Chart(ctx, {
      type: 'line',
      data: data,
      options: this.getBalanceChartOptions()
    });
  }

  private getTransactionChartOptions(): ChartConfiguration['options'] {
    return {
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
    };
  }

  private getBalanceChartOptions(): ChartConfiguration['options'] {
    return {
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
    };
  }
}
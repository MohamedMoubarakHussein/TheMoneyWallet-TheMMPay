import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-progress-bar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="progress-container">
      <div class="progress-track">
        <div class="progress-fill" [style.width.%]="value" [class]="type">
          <div class="progress-glow"></div>
        </div>
      </div>
      <div class="progress-text" *ngIf="showPercentage">
        {{ value }}%
      </div>
      <div class="progress-label" *ngIf="label">
        {{ label }}
      </div>
    </div>
  `,
  styles: [`
    .progress-container {
      width: 100%;
      margin: 1rem 0;
    }

    .progress-track {
      height: 8px;
      width: 100%;
      background: rgba(203, 213, 225, 0.3);
      border-radius: 4px;
      overflow: hidden;
      position: relative;
    }

    .progress-fill {
      height: 100%;
      border-radius: 4px;
      position: relative;
      transition: width 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
    }

    .progress-fill.primary {
      background: linear-gradient(90deg, #1d4ed8, #3b82f6);
    }

    .progress-fill.success {
      background: linear-gradient(90deg, #047857, #10b981);
    }

    .progress-fill.warning {
      background: linear-gradient(90deg, #d97706, #f59e0b);
    }

    .progress-fill.danger {
      background: linear-gradient(90deg, #b91c1c, #ef4444);
    }

    .progress-fill.info {
      background: linear-gradient(90deg, #0369a1, #0ea5e9);
    }

    .progress-glow {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      width: 20px;
      background: radial-gradient(circle at right, rgba(255,255,255,0.8) 0%, rgba(255,255,255,0) 80%);
      animation: glowPulse 1.5s ease-in-out infinite;
    }

    .progress-text {
      margin-top: 0.5rem;
      font-size: 0.75rem;
      color: rgba(100, 116, 139, 0.8);
      text-align: right;
      font-weight: 500;
    }

    .progress-label {
      margin-top: 0.25rem;
      font-size: 0.75rem;
      color: rgba(100, 116, 139, 0.8);
    }

    @keyframes glowPulse {
      0%, 100% { opacity: 0.3; }
      50% { opacity: 1; }
    }
  `]
})
export class ProgressBarComponent implements OnChanges {
  @Input() value: number = 0;
  @Input() type: 'primary' | 'success' | 'warning' | 'danger' | 'info' = 'primary';
  @Input() showPercentage: boolean = true;
  @Input() label: string = '';
  @Input() animateOnChange: boolean = true;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['value'] && this.animateOnChange) {
      // Apply animation on value change if needed
      const element = document.querySelector('.progress-fill') as HTMLElement;
      if (element) {
        element.style.transition = 'width 0.4s cubic-bezier(0.25, 0.8, 0.25, 1)';
        setTimeout(() => {
          element.style.transition = 'width 0.4s cubic-bezier(0.25, 0.8, 0.25, 1)';
        }, 10);
      }
    }
  }
}

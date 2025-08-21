import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnimationService } from '../../services/animation/animation.service';

@Component({
  selector: 'app-animated-counter',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="counter-container" [ngClass]="size">
      <div class="counter-content">
        <div class="counter-prefix" *ngIf="prefix">{{ prefix }}</div>
        <div class="counter-value">{{ displayValue }}</div>
        <div class="counter-suffix" *ngIf="suffix">{{ suffix }}</div>
      </div>
      <div class="counter-label" *ngIf="label">{{ label }}</div>
    </div>
  `,
  styles: [`
    .counter-container {
      display: inline-flex;
      flex-direction: column;
      align-items: center;
      font-family: 'Inter', sans-serif;
      transition: transform 0.3s ease;
    }
    
    .counter-container:hover {
      transform: translateY(-3px);
    }
    
    .counter-content {
      display: flex;
      align-items: baseline;
    }
    
    .counter-value {
      font-weight: 700;
    }
    
    .counter-prefix {
      font-weight: 500;
      margin-right: 2px;
    }
    
    .counter-suffix {
      font-weight: 500;
      margin-left: 2px;
    }
    
    .counter-label {
      font-weight: 500;
      margin-top: 0.5rem;
      text-align: center;
    }
    
    /* Size variants */
    .counter-container.small .counter-value {
      font-size: 1.5rem;
    }
    
    .counter-container.small .counter-prefix,
    .counter-container.small .counter-suffix {
      font-size: 1rem;
    }
    
    .counter-container.small .counter-label {
      font-size: 0.75rem;
    }
    
    .counter-container.medium .counter-value {
      font-size: 2.5rem;
    }
    
    .counter-container.medium .counter-prefix,
    .counter-container.medium .counter-suffix {
      font-size: 1.25rem;
    }
    
    .counter-container.medium .counter-label {
      font-size: 0.875rem;
    }
    
    .counter-container.large .counter-value {
      font-size: 3.5rem;
    }
    
    .counter-container.large .counter-prefix,
    .counter-container.large .counter-suffix {
      font-size: 1.5rem;
    }
    
    .counter-container.large .counter-label {
      font-size: 1rem;
    }
    
    /* Color variants */
    .counter-container.primary .counter-value {
      color: #3b82f6;
    }
    
    .counter-container.success .counter-value {
      color: #10b981;
    }
    
    .counter-container.danger .counter-value {
      color: #ef4444;
    }
    
    .counter-container.warning .counter-value {
      color: #f59e0b;
    }
    
    .counter-container.info .counter-value {
      color: #3b82f6;
    }
  `]
})
export class AnimatedCounterComponent implements OnInit, OnChanges {
  @Input() value: number = 0;
  @Input() startValue: number = 0;
  @Input() duration: number = 1000;
  @Input() decimal: number = 0;
  @Input() prefix: string = '';
  @Input() suffix: string = '';
  @Input() label: string = '';
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  @Input() color: 'primary' | 'success' | 'danger' | 'warning' | 'info' | 'default' = 'default';
  @Input() easing: 'linear' | 'easeIn' | 'easeOut' | 'easeInOut' = 'easeOut';
  @Input() separator: string = ',';
  @Input() formatFn: ((value: number) => string) | null = null;
  
  displayValue: string = '0';
  private animationStartTime: number = 0;
  private animationFrameId: number = 0;

  constructor() {}

  ngOnInit(): void {
    this.formatDisplayValue(this.startValue || 0);
    this.startCounterAnimation();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['value']) {
      this.startCounterAnimation();
    }
  }

  private startCounterAnimation(): void {
    // Cancel any existing animation
    if (this.animationFrameId) {
      cancelAnimationFrame(this.animationFrameId);
    }
    
    const start = this.startValue;
    const end = this.value;
    const startTime = performance.now();
    this.animationStartTime = startTime;
    
    const animate = (currentTime: number) => {
      const elapsedTime = currentTime - this.animationStartTime;
      
      if (elapsedTime < this.duration) {
        // Calculate progress based on easing
        const progress = this.getEasedProgress(elapsedTime / this.duration);
        
        // Calculate the current value
        const currentValue = start + (progress * (end - start));
        
        // Format and update the display value
        this.formatDisplayValue(currentValue);
        
        // Request the next frame
        this.animationFrameId = requestAnimationFrame(animate);
      } else {
        // Animation complete
        this.formatDisplayValue(end);
      }
    };
    
    // Start the animation
    this.animationFrameId = requestAnimationFrame(animate);
  }

  private getEasedProgress(progress: number): number {
    switch (this.easing) {
      case 'linear':
        return progress;
      case 'easeIn':
        return progress * progress;
      case 'easeOut':
        return 1 - (1 - progress) * (1 - progress);
      case 'easeInOut':
        return progress < 0.5 
          ? 2 * progress * progress 
          : 1 - Math.pow(-2 * progress + 2, 2) / 2;
      default:
        return progress;
    }
  }

  private formatDisplayValue(value: number): void {
    if (this.formatFn) {
      this.displayValue = this.formatFn(value);
      return;
    }
    
    // Format with specified decimal places
    const fixedValue = value.toFixed(this.decimal);
    
    // Format with thousand separators if needed
    if (this.separator) {
      const parts = fixedValue.toString().split('.');
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, this.separator);
      this.displayValue = parts.join('.');
    } else {
      this.displayValue = fixedValue;
    }
  }
}

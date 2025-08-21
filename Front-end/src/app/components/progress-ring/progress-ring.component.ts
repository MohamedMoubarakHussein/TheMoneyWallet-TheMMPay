import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-progress-ring',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div 
      class="progress-ring-container"
      [style.width.px]="size" 
      [style.height.px]="size">
      
      <svg 
        class="progress-ring"
        [style.width.px]="size" 
        [style.height.px]="size"
        viewBox="0 0 100 100">
        
        <!-- Background Circle -->
        <circle
          class="progress-ring-circle-bg"
          [attr.stroke]="bgColor"
          [attr.stroke-width]="thickness"
          [attr.cx]="50"
          [attr.cy]="50"
          [attr.r]="radius"
          fill="transparent"
          [@fadeIn]="'in'">
        </circle>
        
        <!-- Progress Circle -->
        <circle
          class="progress-ring-circle"
          [attr.stroke]="getProgressColor()"
          [attr.stroke-width]="thickness"
          [attr.cx]="50"
          [attr.cy]="50"
          [attr.r]="radius"
          fill="transparent"
          [attr.stroke-dasharray]="circumference + ' ' + circumference"
          [attr.stroke-dashoffset]="strokeDashoffset"
          transform="rotate(-90 50 50)"
          [@progressAnimation]="{ value: 'progress', params: { animationDuration: animationDuration + 'ms' } }">
        </circle>
        
        <!-- Text in Center -->
        <text 
          *ngIf="showValue"
          class="progress-text" 
          x="50" 
          y="45"
          text-anchor="middle" 
          dominant-baseline="middle"
          [attr.font-size]="textSize"
          [attr.fill]="textColor"
          [@countUp]="{ value: value.toString(), params: { animationDuration: animationDuration + 'ms' } }">
          {{ displayValue }}
        </text>
        
        <!-- Label in Center -->
        <text 
          *ngIf="label"
          class="progress-label" 
          x="50" 
          y="65"
          text-anchor="middle" 
          dominant-baseline="middle"
          [attr.font-size]="labelSize"
          [attr.fill]="labelColor"
          [@fadeIn]="'in'">
          {{ label }}
        </text>
      </svg>
      
      <!-- Optional Icon -->
      <div 
        *ngIf="icon" 
        class="progress-icon"
        [style.width.px]="iconSize"
        [style.height.px]="iconSize"
        [style.top.px]="(size - iconSize) / 2"
        [style.left.px]="(size - iconSize) / 2"
        [style.color]="iconColor"
        [@fadeInScale]="'in'">
        {{ icon }}
      </div>
    </div>
  `,
  styles: [`
    .progress-ring-container {
      position: relative;
      display: inline-flex;
      align-items: center;
      justify-content: center;
    }
    
    .progress-ring {
      transform: rotate(-90deg);
      overflow: visible;
    }
    
    .progress-ring-circle {
      transition: stroke-dashoffset 0.35s;
      transform-origin: 50% 50%;
      stroke-linecap: round;
    }
    
    .progress-ring-circle-bg {
      stroke-opacity: 0.2;
    }
    
    .progress-text {
      font-weight: 600;
      font-family: 'Inter', sans-serif;
    }
    
    .progress-label {
      font-weight: 500;
      font-family: 'Inter', sans-serif;
      opacity: 0.8;
    }
    
    .progress-icon {
      position: absolute;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.25rem;
    }
  `],
  animations: [
    trigger('fadeIn', [
      state('in', style({ opacity: 1 })),
      transition('void => in', [
        style({ opacity: 0 }),
        animate('400ms ease-out')
      ])
    ]),
    trigger('fadeInScale', [
      state('in', style({ opacity: 1, transform: 'scale(1)' })),
      transition('void => in', [
        style({ opacity: 0, transform: 'scale(0.5)' }),
        animate('500ms 300ms cubic-bezier(0.25, 0.46, 0.45, 0.94)')
      ])
    ]),
    trigger('progressAnimation', [
      state('progress', style({ strokeDashoffset: '*' })),
      transition('void => progress', [
        style({ strokeDashoffset: '{{ circumference }}' }),
        animate('{{ animationDuration }} cubic-bezier(0.25, 0.46, 0.45, 0.94)')
      ], { params: { circumference: 0, animationDuration: '1000ms' } })
    ]),
    trigger('countUp', [
      state('*', style({ opacity: 1 })),
      transition('* => *', [
        style({ opacity: 0.3 }),
        animate('{{ animationDuration }} ease-out')
      ], { params: { animationDuration: '1000ms' } })
    ])
  ]
})
export class ProgressRingComponent implements OnInit, OnChanges {
  @Input() value: number = 0;              // Current value (0-100)
  @Input() size: number = 100;             // Size of the ring in pixels
  @Input() thickness: number = 8;          // Thickness of the ring
  @Input() color: string = '#3b82f6';      // Color of the progress ring
  @Input() bgColor: string = '#e2e8f0';    // Background color
  @Input() animationDuration: number = 1000; // Animation duration in ms
  @Input() showValue: boolean = true;      // Whether to show percentage text
  @Input() valueUnit: string = '%';        // Unit to display after the value
  @Input() valuePrefix: string = '';       // Prefix to display before the value
  @Input() textColor: string = '#1e293b';  // Color of the value text
  @Input() textSize: number = 14;          // Size of the value text
  @Input() label: string = '';             // Label text below the value
  @Input() labelColor: string = '#64748b'; // Color of the label text
  @Input() labelSize: number = 10;         // Size of the label text
  @Input() icon: string = '';              // Optional icon in the center
  @Input() iconColor: string = '#3b82f6';  // Icon color
  @Input() iconSize: number = 30;          // Icon size
  @Input() successColor: string = '#10b981'; // Color when value >= successThreshold
  @Input() warningColor: string = '#f59e0b'; // Color when value < successThreshold && value >= warningThreshold
  @Input() dangerColor: string = '#ef4444'; // Color when value < warningThreshold
  @Input() successThreshold: number = 75;  // Threshold for success color
  @Input() warningThreshold: number = 50;  // Threshold for warning color
  @Input() formatValue: ((value: number) => string) | null = null; // Custom value formatter

  // Calculated properties
  radius: number = 0;
  circumference: number = 0;
  strokeDashoffset: number = 0;
  displayValue: string = '';

  ngOnInit(): void {
    this.initialize();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['value'] || changes['size'] || changes['thickness']) {
      this.initialize();
    }
  }

  private initialize(): void {
    // Calculate radius (accounting for stroke width)
    this.radius = 50 - (this.thickness / 2);
    
    // Calculate the circumference
    this.circumference = 2 * Math.PI * this.radius;
    
    // Calculate the stroke dash offset
    const actualValue = Math.max(0, Math.min(100, this.value));
    this.strokeDashoffset = this.circumference - (actualValue / 100 * this.circumference);
    
    // Format the display value
    if (this.formatValue) {
      this.displayValue = this.formatValue(this.value);
    } else {
      this.displayValue = `${this.valuePrefix}${Math.round(this.value)}${this.valueUnit}`;
    }
  }

  getProgressColor(): string {
    if (this.value >= this.successThreshold) {
      return this.successColor;
    } else if (this.value >= this.warningThreshold) {
      return this.warningColor;
    } else {
      return this.dangerColor;
    }
  }
}
